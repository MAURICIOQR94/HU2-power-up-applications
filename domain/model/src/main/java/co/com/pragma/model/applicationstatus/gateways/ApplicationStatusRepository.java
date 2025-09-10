package co.com.pragma.model.applicationstatus.gateways;

import co.com.pragma.model.applicationstatus.ApplicationStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ApplicationStatusRepository {

    Mono<ApplicationStatus> findByName(String name);
    Mono<ApplicationStatus> findById(Long id);
    Flux<ApplicationStatus> findByNameIn(List<String> statuses);

}
