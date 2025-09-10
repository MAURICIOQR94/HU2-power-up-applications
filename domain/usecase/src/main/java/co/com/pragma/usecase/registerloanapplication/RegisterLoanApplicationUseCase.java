package co.com.pragma.usecase.registerloanapplication;

import co.com.pragma.model.applicationstatus.ApplicationStatus;
import co.com.pragma.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.pragma.common.exception.BusinessException;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static co.com.pragma.common.enums.BusinessExceptionMessage.APPLICATION_STATUS_NOT_FOUND;
import static co.com.pragma.common.enums.BusinessExceptionMessage.LOAN_TYPE_NOT_FOUND;

@RequiredArgsConstructor
public class RegisterLoanApplicationUseCase {

    private static final String DEFAULT_STATUS_NAME = "PENDIENTE";

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final ApplicationStatusRepository applicationStatusRepository;

    public Mono<LoanApplication> execute(LoanApplication loanApplication) {

        Mono<LoanType> loanTypeMono = loanTypeRepository.findByName(loanApplication.getLoanType().getName())
                .switchIfEmpty(Mono.error(new BusinessException(LOAN_TYPE_NOT_FOUND)));

        Mono<ApplicationStatus> statusMono = applicationStatusRepository.findByName(DEFAULT_STATUS_NAME)
                .switchIfEmpty(Mono.error(new BusinessException(APPLICATION_STATUS_NOT_FOUND)));

        return Mono.zip(loanTypeMono, statusMono)
                .flatMap(tuple -> {

                    LoanType foundLoanType = tuple.getT1();
                    ApplicationStatus foundStatus = tuple.getT2();

                    LoanApplication newLoanApplication = loanApplication.toBuilder()
                            .id(UUID.randomUUID())
                            .createdAt(LocalDateTime.now())
                            .loanType(foundLoanType)
                            .status(foundStatus)
                            .build();

                    return loanApplicationRepository.save(newLoanApplication);
                });
    }

}
