package co.com.pragma.model.loanapplication.gateways;

import co.com.pragma.model.loanapplication.LoanApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface LoanApplicationRepository {

    Mono<LoanApplication> save(LoanApplication loanApplication);
    Mono<LoanApplication> findById(UUID id);
    Flux<LoanApplication> findAllByDocumentNumberAndIdStatus(String documentNumber, Long idStatus);
    Flux<LoanApplication> findAll();
    Flux<LoanApplication> findByIdStatusInPaged(List<Long> statuses, int page, int size);
    Mono<Long> countByIdStatusIn(List<Long> idStatuses);
    Mono<LoanApplication> update(LoanApplication loanApplication);


}

