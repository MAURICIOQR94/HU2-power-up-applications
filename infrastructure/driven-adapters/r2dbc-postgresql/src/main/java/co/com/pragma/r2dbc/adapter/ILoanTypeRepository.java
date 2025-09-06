package co.com.pragma.r2dbc.adapter;

import co.com.pragma.r2dbc.entity.LoanTypeEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ILoanTypeRepository  extends ReactiveCrudRepository <LoanTypeEntity, Long>, ReactiveQueryByExampleExecutor<LoanTypeEntity> {
    Mono<LoanTypeEntity> findByName(String name);
}
