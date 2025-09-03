package co.com.pragma.usecase.registerloanapplication;

import co.com.pragma.model.applicationstatus.ApplicationStatus;
import co.com.pragma.model.common.enums.BusinessExceptionMessage;
import co.com.pragma.model.common.exception.BusinessException;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class RegisterLoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;

    public Mono<LoanApplication> execute(LoanApplication loanApplication) {
        return Mono.just(loanApplication)
                .map(this::initializeLoanApplication)
                .flatMap(this::validateAndSetLoanType)
                .flatMap(loanApplicationRepository::save);
    }

    private Mono<LoanApplication> validateAndSetLoanType(LoanApplication loanApplication) {
        Long loanTypeId = loanApplication.getLoanType().getId();

        return loanTypeRepository.findById(loanTypeId)
                .switchIfEmpty(Mono.error(new BusinessException(BusinessExceptionMessage.LOAN_TYPE_NOT_FOUND)))
                .map(loanType -> {
                    loanApplication.setLoanType(loanType);
                    return loanApplication;
                });
    }

    public Mono<LoanApplication> findById(UUID id) {
        return loanApplicationRepository.findById(id);
    }

    private LoanApplication initializeLoanApplication(LoanApplication loanApplication) {
        loanApplication.setId(UUID.randomUUID());
        loanApplication.setCreatedAt(LocalDateTime.now());
        loanApplication.setStatus(ApplicationStatus.builder().id(1L).build());

        return loanApplication;
    }
}
