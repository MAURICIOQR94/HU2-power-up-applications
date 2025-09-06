package co.com.pragma.model.applicationstatus.gateways;

import co.com.pragma.model.applicationstatus.ApplicationStatus;
import reactor.core.publisher.Mono;

public interface ApplicationStatusRepository {

    Mono<ApplicationStatus> findByName(String name);

}
