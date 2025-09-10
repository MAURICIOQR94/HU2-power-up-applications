package co.com.pragma.api.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ErrorDTO {

    private String id;
    private String type;
    private String title;
    private String message;
    private String source;

}
