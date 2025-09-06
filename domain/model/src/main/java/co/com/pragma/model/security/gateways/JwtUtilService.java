package co.com.pragma.model.security.gateways;

import co.com.pragma.model.tokeninfo.TokenInfo;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface JwtUtilService {

    boolean validateToken(String token);
    UUID extractUserId(String token);
    String extractRole(String token);
    String extractDocumentNumber(String token);
    Mono<TokenInfo> getClaims(String token);

}
