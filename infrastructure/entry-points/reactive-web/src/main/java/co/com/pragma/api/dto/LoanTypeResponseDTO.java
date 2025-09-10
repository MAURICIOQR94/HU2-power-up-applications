package co.com.pragma.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LoanTypeResponseDTO {

    private String name;
    private Float interestRate;

}
