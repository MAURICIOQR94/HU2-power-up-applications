package co.com.pragma.externalwebclient.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ClientRequest;
import reactor.core.publisher.Mono;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebClientAuthFilter {

    private static final String AUTHORIZATION =  "Authorization";

    public static ExchangeFilterFunction bearerAuthFromContext() {
        return (request, next) -> {
            return Mono.deferContextual(ctx -> {
                if (ctx.hasKey(AUTHORIZATION)) {
                    String token = ctx.get(AUTHORIZATION);
                    ClientRequest filteredRequest = ClientRequest.from(request)
                            .header(AUTHORIZATION, token)
                            .build();
                    return next.exchange(filteredRequest);
                }
                return next.exchange(request);
            });
        };
    }
}
