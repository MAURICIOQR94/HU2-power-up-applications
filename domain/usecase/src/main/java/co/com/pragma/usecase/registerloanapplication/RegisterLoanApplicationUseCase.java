package co.com.pragma.usecase.registerloanapplication;

import co.com.pragma.domain.gateways.restconsumer.ExternalService;
import co.com.pragma.domain.gateways.sqs.QueueSenderService;
import co.com.pragma.domain.gateways.sqs.QueueType;
import co.com.pragma.model.applicationstatus.ApplicationStatus;
import co.com.pragma.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.pragma.common.exception.BusinessException;
import co.com.pragma.model.lambdavalidation.ValidationResult;
import co.com.pragma.model.lambdavalidation.gateways.LambdaValidationService;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.service.LoanCalculator;
import co.com.pragma.model.lambdavalidation.CapacityCalculation;
import co.com.pragma.util.LoanReportMessage;
import co.com.pragma.util.NotificationMessage;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static co.com.pragma.common.enums.BusinessExceptionMessage.APPLICATION_STATUS_NOT_FOUND;
import static co.com.pragma.common.enums.BusinessExceptionMessage.LOAN_TYPE_NOT_FOUND;

@RequiredArgsConstructor
public class RegisterLoanApplicationUseCase {

    private static final String DEFAULT_STATUS_NAME = "PENDIENTE";
    private static final String APPROVED = "APROBADO";
    private static final String REJECTED = "RECHAZADO";

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final ApplicationStatusRepository applicationStatusRepository;
    private final LambdaValidationService lambdaValidationService;
    private final QueueSenderService queueSenderService;
    private final ExternalService externalService;

    public Mono<LoanApplication> execute(LoanApplication loanApplication) {
        return externalService.getUserByEmailAsService(loanApplication.getEmail())
                .flatMap(user -> {
                    loanApplication.setUser(user);
                    loanApplication.setDocumentNumber(user.getDocumentNumber());

                    return loanTypeRepository.findByName(loanApplication.getLoanType().getName())
                            .switchIfEmpty(Mono.error(new BusinessException(LOAN_TYPE_NOT_FOUND)))
                            .flatMap(foundLoanType -> {
                                if (foundLoanType.isAutomaticValidation()) {
                                    return handleAutomaticValidation(loanApplication, foundLoanType);
                                } else {
                                    return handleManualValidation(loanApplication, foundLoanType);
                                }
                            });
                });
    }

    private Mono<LoanApplication> handleAutomaticValidation(LoanApplication loanApplication, LoanType foundLoanType) {
        User user = loanApplication.getUser();
        return applicationStatusRepository.findByName(APPROVED)
                .switchIfEmpty(Mono.error(new BusinessException(APPLICATION_STATUS_NOT_FOUND)))
                .flatMap(approvedStatus ->
                        loanApplicationRepository.findAllByDocumentNumberAndIdStatus(
                                        loanApplication.getDocumentNumber(),
                                        approvedStatus.getId()
                                )
                                .flatMap(app ->
                                        loanTypeRepository.findById(app.getLoanType().getId())
                                                .map(fullLoanType -> {
                                                    app.setLoanType(fullLoanType);
                                                    return app;
                                                })
                                )
                                .collectList()
                                .flatMap(approvedApplications -> {
                                    double totalMonthlyPayment = approvedApplications.stream()
                                            .mapToDouble(app -> LoanCalculator.calculateMonthlyPayment(
                                                    app.getAmount(),
                                                    app.getLoanType().getInterestRate(),
                                                    app.getTerm()
                                            ))
                                            .sum();

                                    CapacityCalculation request = CapacityCalculation.builder()
                                            .baseSalary(user.getBaseSalary())
                                            .amount(loanApplication.getAmount())
                                            .term(loanApplication.getTerm())
                                            .interestRate(foundLoanType.getInterestRate())
                                            .totalMonthlyPayment(totalMonthlyPayment)
                                            .build();

                                    return lambdaValidationService.validateLoanApplication(request)
                                            .flatMap(validationResult ->
                                                    processValidationResult(
                                                            validationResult,
                                                            loanApplication,
                                                            foundLoanType)
                                            );
                                })
                );
    }

    private Mono<LoanApplication> handleManualValidation(LoanApplication loanApplication, LoanType foundLoanType) {
        return applicationStatusRepository.findByName(DEFAULT_STATUS_NAME)
                .switchIfEmpty(Mono.error(new BusinessException(APPLICATION_STATUS_NOT_FOUND)))
                .flatMap(defaultStatus -> {
                    LoanApplication newLoanApplication = buildLoanApplication(loanApplication, foundLoanType, defaultStatus);
                    return loanApplicationRepository.save(newLoanApplication);
                });
    }

    private Mono<LoanApplication> processValidationResult(ValidationResult validationResult,
                                                          LoanApplication loanApplication,
                                                          LoanType foundLoanType) {
        return applicationStatusRepository.findByName(validationResult.getFinalStatus())
                .switchIfEmpty(Mono.error(new BusinessException(APPLICATION_STATUS_NOT_FOUND)))
                .flatMap(finalStatus -> {
                    LoanApplication newLoanApplication =
                            buildLoanApplication(loanApplication, foundLoanType, finalStatus);

                    Mono<LoanApplication> notifyMono =
                            (APPROVED.equalsIgnoreCase(finalStatus.getName())
                                    || REJECTED.equalsIgnoreCase(finalStatus.getName()))
                                    ? notifyStatus(newLoanApplication, finalStatus.getName(), validationResult)
                                    : Mono.just(newLoanApplication);

                    return notifyMono.flatMap(loanApplicationRepository::save);
                });
    }

    private LoanApplication buildLoanApplication(LoanApplication loanApplication,
                                                 LoanType foundLoanType,
                                                 ApplicationStatus finalStatus) {
        return loanApplication.toBuilder()
                .id(UUID.randomUUID())
                .createdAt(LocalDateTime.now())
                .loanType(foundLoanType)
                .status(finalStatus)
                .build();
    }

    private Mono<LoanApplication> notifyStatus(LoanApplication updatedApplication,
                                               String newStatus,
                                               ValidationResult validationResult) {
        User user = updatedApplication.getUser();

        NotificationMessage notificationMessage = NotificationMessage.builder()
                .email(user.getEmail())
                .name(user.getFirstName())
                .status(newStatus)
                .paymentPlan(validationResult.getPaymentPlan())
                .build();

        Mono<String> notificationSent = queueSenderService.send(QueueType.NOTIFICATIONS, notificationMessage);

        if (APPROVED.equalsIgnoreCase(newStatus)) {
            LoanReportMessage reportMessage = LoanReportMessage.builder()
                    .status(APPROVED)
                    .amount(updatedApplication.getAmount())
                    .build();

            Mono<String> reportSent = queueSenderService.send(QueueType.REPORTS, reportMessage);

            return Mono.zip(notificationSent, reportSent).thenReturn(updatedApplication);
        }

        return notificationSent.thenReturn(updatedApplication);
    }

}
