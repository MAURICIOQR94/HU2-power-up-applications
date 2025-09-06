package co.com.pragma.model.loanapplication.gateways;

import co.com.pragma.model.loanapplication.LoanApplication;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LoanApplicationRepository {

    Mono<LoanApplication> save(LoanApplication loanApplication);
    Mono<LoanApplication> findById(UUID id);

}
