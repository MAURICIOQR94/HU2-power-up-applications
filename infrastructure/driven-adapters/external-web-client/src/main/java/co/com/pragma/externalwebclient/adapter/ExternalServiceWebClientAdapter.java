package co.com.pragma.externalwebclient.adapter;

import co.com.pragma.domain.gateways.restconsumer.ExternalService;
import co.com.pragma.externalwebclient.config.UserServiceProperties;
import co.com.pragma.externalwebclient.dto.UserExternalResponseDTO;
import co.com.pragma.externalwebclient.mapper.UserExternalMapper;
import co.com.pragma.model.user.User;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ExternalServiceWebClientAdapter implements ExternalService {

    private final WebClient webClient;
    private final UserExternalMapper userExternalMapper;

    public ExternalServiceWebClientAdapter(WebClient.Builder builder, UserExternalMapper userExternalMapper, UserServiceProperties properties) {
        this.userExternalMapper = userExternalMapper;
        this.webClient = builder.baseUrl(properties.baseUrl()).build();
    }

    public Mono<User> getUserByEmail(String email) {
        return webClient.get()
                .uri("/usuarios?email={email}", email)
                .retrieve()
                .bodyToMono(UserExternalResponseDTO.class)
                .map(userExternalMapper::toEntity);
    }
}