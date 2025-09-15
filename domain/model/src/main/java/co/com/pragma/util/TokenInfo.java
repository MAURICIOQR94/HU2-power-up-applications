package co.com.pragma.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class TokenInfo {

    private String email;
    private String userId;

}
