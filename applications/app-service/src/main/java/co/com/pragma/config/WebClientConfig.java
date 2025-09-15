package co.com.pragma.config;

import co.com.pragma.externalwebclient.config.ServiceTokenProvider;
import co.com.pragma.externalwebclient.config.WebClientAuthFilter;
import co.com.pragma.properties.AuthenticationServiceProperties;
import co.com.pragma.properties.LambdaValidationServiceProperties;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@AllArgsConstructor
public class WebClientConfig {

    private final AuthenticationServiceProperties authenticationProperties;
    private final LambdaValidationServiceProperties lambdaValidationProperties;

    @Bean("userServiceWebClient")
    public WebClient userServiceWebClient() {
        return WebClient.builder()
                .baseUrl(authenticationProperties.baseUrl())
                .filter(WebClientAuthFilter.bearerAuthFromContext())
                .build();
    }

    @Bean("internalUserServiceWebClient")
    public WebClient internalUserServiceWebClient(ServiceTokenProvider tokenProvider) {
        return WebClient.builder()
                .baseUrl(authenticationProperties.baseUrl())
                .defaultHeader("Authorization", tokenProvider.getServiceToken())
                .build();
    }

    @Bean("lambdaServiceWebClient")
    public WebClient lambdaServiceWebClient() {
        return WebClient.builder()
                .baseUrl(lambdaValidationProperties.baseUrl())
                .build();
    }

}
