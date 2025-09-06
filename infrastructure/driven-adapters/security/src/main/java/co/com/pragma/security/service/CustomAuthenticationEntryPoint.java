package co.com.pragma.security.service;

import co.com.pragma.common.exception.GeneralException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static co.com.pragma.common.enums.GeneralExceptionMessage.ACCESS_DENIED;

public class CustomAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        return Mono.error(new GeneralException(ex.getMessage(), ACCESS_DENIED));
    }
}
