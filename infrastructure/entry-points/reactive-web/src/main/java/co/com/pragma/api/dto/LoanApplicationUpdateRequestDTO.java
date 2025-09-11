package co.com.pragma.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class LoanApplicationUpdateRequestDTO {

    @NotBlank(message = "- Status is required")
    String status;

}