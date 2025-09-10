package co.com.pragma.security;

import co.com.pragma.common.exception.GeneralException;
import co.com.pragma.domain.gateways.security.JwtUtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static co.com.pragma.common.enums.GeneralExceptionMessage.INVALID_JWT;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtUtilService jwtUtil;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        if (!jwtUtil.validateToken(token)) {
            return Mono.error(new GeneralException(INVALID_JWT));
        }

        UUID userId = jwtUtil.extractUserId(token);
        String role = jwtUtil.extractRole(token);

        var authorities = List.of(new SimpleGrantedAuthority(role));

        Authentication authToken = new UsernamePasswordAuthenticationToken(
                userId.toString(),
                token,
                authorities
        );

        return Mono.just(authToken);
    }
}