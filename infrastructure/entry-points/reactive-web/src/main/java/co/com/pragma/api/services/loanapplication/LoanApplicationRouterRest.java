package co.com.pragma.api.services.loanapplication;

import co.com.pragma.api.config.ApiProperties;
import lombok.RequiredArgsConstructor;
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
@RequiredArgsConstructor
public non-sealed class LoanApplicationRouterRest extends LoanApplicationApiDoc{

    private final ApiProperties apiProperties;

    @Bean
    public RouterFunction<ServerResponse> routerFunction(LoanApplicationHandler handler) {
        return SpringdocRouteBuilder.route()
                .POST(apiProperties.basePath(),
                        accept(MediaType.APPLICATION_JSON),
                        handler::save,
                        save()
                )
                .build();
    }

}