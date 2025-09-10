package co.com.pragma.api.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class AuthorizationContextWebFilter implements WebFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = request.getHeaders().getFirst(AUTHORIZATION_HEADER);
        if (token != null) {
            return chain.filter(exchange)
                    .contextWrite(ctx -> ctx.put(AUTHORIZATION_HEADER, token));
        }
        return chain.filter(exchange);
    }
}