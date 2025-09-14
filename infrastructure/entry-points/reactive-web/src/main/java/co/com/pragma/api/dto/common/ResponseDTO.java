package co.com.pragma.api.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Data
@Builder(toBuilder = true)
public final class ResponseDTO<T> {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final MetaDTO.Meta meta;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T error;

    private static <T> Mono<ServerResponse> buildResponse(HttpStatus status, T body) {
        return ServerResponse
                .status(status)
                .contentType(APPLICATION_JSON)
                .bodyValue(body);
    }

    public static <T> Mono<ServerResponse> success(ServerRequest request, T data) {
        return buildResponse(
                HttpStatus.OK,
                ResponseDTO.builder()
                        .meta(MetaDTO.build(data))
                        .data(data)
                        .build()
        );
    }

    public static <T> Mono<ServerResponse> failed(ServerRequest request, T error, HttpStatus status) {
        return buildResponse(status,
                ResponseDTO.builder()
                        .error(error)
                        .build()
        );
    }

}
