package co.com.pragma.usecase.registerloanapplication;

import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class RegisterLoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;

    public Mono<LoanApplication> execute(LoanApplication loanApplication) {
        return validateAndSetLoanType(loanApplication)
                .then(Mono.defer(() -> loanApplicationRepository.save(loanApplication)));
    }

    private Mono<LoanApplication> validateAndSetLoanType(LoanApplication loanApplication) {
        Long loanTypeId = loanApplication.getLoanType().getId();

        return loanTypeRepository.findById(loanTypeId)
                .map(loanType -> {
                    loanApplication.setLoanType(loanType);
                    return loanApplication;
                });
    }

    public Mono<LoanApplication> findById(UUID id) {
        return loanApplicationRepository.findById(id);
    }
}
