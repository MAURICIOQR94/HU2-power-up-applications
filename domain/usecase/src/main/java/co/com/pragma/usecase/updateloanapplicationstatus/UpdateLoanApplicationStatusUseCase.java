package co.com.pragma.usecase.updateloanapplicationstatus;

import co.com.pragma.common.exception.BusinessException;
import co.com.pragma.domain.gateways.restconsumer.ExternalService;
import co.com.pragma.domain.gateways.sqs.QueueSenderService;
import co.com.pragma.domain.gateways.sqs.QueueType;
import co.com.pragma.model.applicationstatus.ApplicationStatus;
import co.com.pragma.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.util.NotificationMessage;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static co.com.pragma.common.enums.BusinessExceptionMessage.*;

@RequiredArgsConstructor
public class UpdateLoanApplicationStatusUseCase {

    private static final String PENDING = "PENDIENTE";
    private static final String MANUAL_REVIEW = "REVISION_MANUAL";
    private static final String APPROVED = "APROBADO";
    private static final String REJECTED = "RECHAZADO";
    private static final String CANCELED = "CANCELADO";

    private final LoanApplicationRepository loanApplicationRepository;
    private final ApplicationStatusRepository applicationStatusRepository;
    private final ExternalService externalService;
    private final QueueSenderService queueSenderService;

    public Mono<LoanApplication> execute(UUID id, String newStatus) {
        return loanApplicationRepository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(LOAN_APPLICATION_NOT_FOUND)))
                .flatMap(loanApplication ->
                        validateAndFindNewStatus(loanApplication, newStatus)
                                .flatMap(newStatusEntity ->
                                        updateLoanApplicationStatus(loanApplication, newStatusEntity, newStatus))
                );
    }

    private Mono<ApplicationStatus> validateAndFindNewStatus(LoanApplication loanApplication, String newStatus) {
        return applicationStatusRepository.findById(loanApplication.getStatus().getId())
                .switchIfEmpty(Mono.error(new BusinessException(APPLICATION_STATUS_NOT_FOUND)))
                .flatMap(currentStatus -> {
                    if (currentStatus.getName().equals(newStatus)) {
                        return Mono.error(new BusinessException(STATUS_ALREADY_SET));
                    }
                    if (!isValidStatusTransition(currentStatus.getName(), newStatus)) {
                        return Mono.error(new BusinessException(CANNOT_CHANGE_STATUS));
                    }
                    return applicationStatusRepository.findByName(newStatus.toUpperCase())
                            .switchIfEmpty(Mono.error(new BusinessException(APPLICATION_STATUS_NOT_FOUND)));
                });
    }

    private Mono<LoanApplication> updateLoanApplicationStatus(LoanApplication loanApplication,
                                                              ApplicationStatus newStatusEntity,
                                                              String newStatus) {
        loanApplication.setStatus(newStatusEntity);

        return loanApplicationRepository.update(loanApplication)
                .flatMap(updatedLoanApplication -> {
                    if (APPROVED.equals(newStatus) || REJECTED.equals(newStatus)) {
                        return notifyStatusChange(updatedLoanApplication, newStatus);
                    }
                    return Mono.just(updatedLoanApplication);
                });
    }

    private Mono<LoanApplication> notifyStatusChange(LoanApplication updatedApplication, String newStatus) {
        return externalService.getUserByEmailAsClient(updatedApplication.getEmail())
                .flatMap(user -> {
                    NotificationMessage message = NotificationMessage.builder()
                            .email(user.getEmail())
                            .name(user.getFirstName())
                            .status(newStatus)
                            .build();
                    return queueSenderService.send(QueueType.NOTIFICATIONS, message).thenReturn(updatedApplication);
                });
    }

    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        Map<String, List<String>> allowedTransitions = Map.of(
                PENDING, Arrays.asList(APPROVED, REJECTED, CANCELED),
                MANUAL_REVIEW, Arrays.asList(APPROVED, REJECTED, CANCELED),
                APPROVED, List.of(CANCELED),
                REJECTED, List.of(),
                CANCELED, List.of()
        );

        return allowedTransitions.getOrDefault(currentStatus.toUpperCase(), List.of())
                .contains(newStatus.toUpperCase());
    }

}
