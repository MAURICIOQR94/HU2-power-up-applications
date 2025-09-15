package co.com.pragma.externalwebclient.adapter;

import co.com.pragma.domain.gateways.restconsumer.ExternalService;
import co.com.pragma.externalwebclient.dto.UserExternalResponseDTO;
import co.com.pragma.externalwebclient.mapper.UserExternalMapper;
import co.com.pragma.model.user.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ExternalServiceWebClientAdapter implements ExternalService {

    private final WebClient userWebClient;
    private final WebClient internalUserWebClient;
    private final UserExternalMapper userExternalMapper;

    public ExternalServiceWebClientAdapter(
            @Qualifier("userServiceWebClient") WebClient userWebClient,
            @Qualifier("internalUserServiceWebClient") WebClient internalUserWebClient,
            UserExternalMapper userExternalMapper) {
        this.userWebClient = userWebClient;
        this.internalUserWebClient =  internalUserWebClient;
        this.userExternalMapper = userExternalMapper;
    }


    @Override
    public Mono<User> getUserByEmailAsClient(String email) {
        return userWebClient.get()
                .uri("/usuarios?email={email}", email)
                .retrieve()
                .bodyToMono(UserExternalResponseDTO.class)
                .map(userExternalMapper::toModel);
    }

    @Override
    public Mono<User> getUserByEmailAsService(String email) {
        return internalUserWebClient.get()
                .uri("/usuarios?email={email}", email)
                .retrieve()
                .bodyToMono(UserExternalResponseDTO.class)
                .map(userExternalMapper::toModel);
    }


}