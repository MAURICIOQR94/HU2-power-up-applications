package co.com.pragma.security.util;

import co.com.pragma.domain.gateways.security.JwtUtilService;
import co.com.pragma.util.TokenInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.UUID;

@Component
public class JwtUtilServiceImpl implements JwtUtilService {

    private static final String ROLE = "role";
    private static final String EMAIL = "email";

    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public boolean validateToken(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public UUID extractUserId(String token) {
        return UUID.fromString(parse(token).getBody().getSubject());
    }

    @Override
    public String extractRole(String token) {
        return parse(token).getBody().get(ROLE, String.class);
    }

    @Override
    public Mono<TokenInfo> getClaims(String token) {
        return Mono.fromCallable(() -> {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return TokenInfo.builder()
                    .userId(claims.getSubject())
                    .email(claims.get(EMAIL, String.class))
                    .build();
        });
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}