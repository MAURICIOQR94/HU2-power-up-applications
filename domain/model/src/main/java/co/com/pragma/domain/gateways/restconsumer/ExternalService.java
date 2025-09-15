package co.com.pragma.domain.gateways.restconsumer;

import co.com.pragma.model.user.User;
import reactor.core.publisher.Mono;

public interface ExternalService {
    Mono<User> getUserByEmailAsClient(String email);
    Mono<User> getUserByEmailAsService(String email);
}
