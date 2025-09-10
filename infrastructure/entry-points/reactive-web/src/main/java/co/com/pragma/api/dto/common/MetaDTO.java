package co.com.pragma.api.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MetaDTO {

    private static final Integer ONE = 1;

    @Data
    @Builder(toBuilder = true)
    public static class Meta {

        @JsonProperty("_requestDate")
        private LocalDateTime requestDate;

        @JsonProperty("_responseSize")
        private int responseSize;

    }

    public static <T> Meta build(T data) {
        return Meta.builder()
                .requestDate(LocalDateTime.now())
                .responseSize(getDataSize(data))
                .build();
    }

    private static <T> int getDataSize(T data) {
        if (data instanceof List<?>) {
            return ((List<?>) data).size();
        }
        return ONE;
    }
}

