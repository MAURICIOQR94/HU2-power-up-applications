package co.com.pragma.r2dbc.adapter;

import co.com.pragma.r2dbc.entity.ApplicationStatusEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IApplicationStatusRepository extends ReactiveCrudRepository<ApplicationStatusEntity, Long>, ReactiveQueryByExampleExecutor<ApplicationStatusEntity> {
    Mono<ApplicationStatusEntity> findByName(String name);
    Flux<ApplicationStatusEntity> findByNameIn(List<String> names);
}
