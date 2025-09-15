package co.com.pragma.r2dbc.adapter;

import co.com.pragma.r2dbc.entity.LoanApplicationEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ILoanApplicationRepository extends ReactiveCrudRepository<LoanApplicationEntity, UUID>, ReactiveQueryByExampleExecutor<LoanApplicationEntity> {
    Flux<LoanApplicationEntity> findAllByDocumentNumberAndIdStatus(String documentNumber, Long idStatus);
}
