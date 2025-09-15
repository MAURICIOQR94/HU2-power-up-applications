package co.com.pragma.domain.gateways.security;

import co.com.pragma.util.TokenInfo;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface JwtUtilService {

    boolean validateToken(String token);
    UUID extractUserId(String token);
    String extractRole(String token);
    Mono<TokenInfo> getClaims(String token);

}
