package co.com.pragma.model.tokeninfo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class TokenInfo {

    private String userId;
    private String documentNumber;

}
