package co.com.pragma.security.config;

import co.com.pragma.common.exception.GeneralException;
import co.com.pragma.security.JwtServerAuthenticationConverter;
import co.com.pragma.security.service.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import static co.com.pragma.common.enums.GeneralExceptionMessage.ACCESS_DENIED;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ROLE_CLIENT = "CLIENTE";
    private static final String ROLE_ASESOR = "ASESOR";
    private static final String ENDPOINT = "/api/v1/solicitudes";

    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtServerAuthenticationConverter authenticationConverter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        AuthenticationWebFilter jwtFilter = new AuthenticationWebFilter(authenticationManager);
        jwtFilter.setServerAuthenticationConverter(authenticationConverter);

        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .pathMatchers(HttpMethod.GET, ENDPOINT).hasAnyAuthority(ROLE_ASESOR)
                        .pathMatchers(HttpMethod.POST, ENDPOINT).hasAuthority(ROLE_CLIENT)
                        .pathMatchers(HttpMethod.PUT, ENDPOINT).hasAuthority(ROLE_ASESOR)
                        .anyExchange().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler((exchange, denied) ->
                                Mono.error(new GeneralException(ACCESS_DENIED))
                        )
                )
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
