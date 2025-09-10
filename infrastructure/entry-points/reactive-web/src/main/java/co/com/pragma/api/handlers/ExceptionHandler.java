package co.com.pragma.api.handlers;

import co.com.pragma.api.dto.common.ErrorDTO;
import co.com.pragma.api.dto.common.ResponseDTO;
import co.com.pragma.common.exception.GeneralException;
import co.com.pragma.common.exception.TechnicalException;
import co.com.pragma.common.exception.BusinessException;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Log4j2
@Component
@Order(-2)
public class ExceptionHandler extends AbstractErrorWebExceptionHandler {

    private static final String PROJECT_NAME = "applications";

    public ExceptionHandler(ErrorAttributes errorAttributes, WebProperties webProperties,
                            ApplicationContext applicationContext,
                            ServerCodecConfigurer configurator) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        this.setMessageReaders(configurator.getReaders());
        this.setMessageWriters(configurator.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        return Mono.just(request)
                .map(this::getError)
                .flatMap(Mono::error)
                .onErrorResume(BusinessException.class, this::buildErrorResponse)
                .onErrorResume(TechnicalException.class, this::buildErrorResponse)
                .onErrorResume(GeneralException.class, this::buildErrorResponse)
                .onErrorResume(this::buildErrorResponse)
                .cast(Tuple2.class)
                .flatMap(error -> ResponseDTO.failed(request, error.get(0), (HttpStatus) error.get(1)));
    }

    protected Mono<Tuple2<ErrorDTO, HttpStatus>> buildErrorResponse(BusinessException ex) {
        log.error("Business exception occurred: {}", ex.getBusinessExceptionMessage().getMessage());
        return Mono.just(ErrorDTO.builder()
                        .id(ex.getBusinessExceptionMessage().getCode())
                        .title(ex.getBusinessExceptionMessage().toString())
                        .type("Business")
                        .message(ex.getBusinessExceptionMessage().getMessage())
                        .source(PROJECT_NAME)
                        .build())
                .map(e -> Tuples.of(e, HttpStatus.CONFLICT));
    }

    protected Mono<Tuple2<ErrorDTO, HttpStatus>> buildErrorResponse(TechnicalException ex) {
        log.error("Technical exception occurred: {}", ex.getTechnicalExceptionMessage().getMessage(), ex);
        return Mono.just(ErrorDTO.builder()
                        .id(ex.getTechnicalExceptionMessage().getCode())
                        .title(ex.getTechnicalExceptionMessage().toString())
                        .type("Technical")
                        .message(ex.getTechnicalExceptionMessage().getMessage())
                        .source(PROJECT_NAME)
                        .build())
                .map(e -> Tuples.of(e, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    protected Mono<Tuple2<ErrorDTO, HttpStatus>> buildErrorResponse(GeneralException ex) {
        log.error("General exception occurred: {}", ex.getGeneralExceptionMessage().getMessage(), ex);
        return Mono.just(ErrorDTO.builder()
                        .id(ex.getGeneralExceptionMessage().getCode())
                        .title(ex.getGeneralExceptionMessage().toString())
                        .type("General")
                        .message(ex.getMessage())
                        .source(PROJECT_NAME)
                        .build())
                .map(e -> Tuples.of(e, HttpStatus.resolve(ex.getGeneralExceptionMessage().getStatusCode())));
    }

    protected Mono<Tuple2<ErrorDTO, HttpStatus>> buildErrorResponse(Throwable throwable) {
        log.error("Unhandled exception occurred: {}", throwable.getMessage(), throwable);
        return Mono.just(ErrorDTO.builder()
                        .message(throwable.getMessage())
                        .title(throwable.getMessage())
                        .source(PROJECT_NAME)
                        .build())
                .map(e -> e.getMessage().contains(HttpStatus.NOT_FOUND.toString())
                        ? Tuples.of(e, HttpStatus.NOT_FOUND) : Tuples.of(e, HttpStatus.INTERNAL_SERVER_ERROR)
                );
    }

}
