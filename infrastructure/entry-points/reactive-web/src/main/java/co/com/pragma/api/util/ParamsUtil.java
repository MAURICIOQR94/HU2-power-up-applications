package co.com.pragma.api.util;

import co.com.pragma.common.exception.GeneralException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static co.com.pragma.common.enums.GeneralExceptionMessage.PARAM_MISSING_ERROR;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParamsUtil {

    public static final String ID = "id";

    private static Mono<String> ofEmptyParams(String value) {
        return (Objects.isNull(value) || value.isEmpty()) ?
                Mono.error(new GeneralException(PARAM_MISSING_ERROR)) : Mono.just(value);
    }

    public static Mono<String> getIdPathParam(ServerRequest request) {
        return Mono.justOrEmpty(request.pathVariable(ID))
                .flatMap(ParamsUtil::ofEmptyParams);
    }

}
