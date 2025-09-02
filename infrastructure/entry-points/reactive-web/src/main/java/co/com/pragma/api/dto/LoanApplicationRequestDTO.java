package co.com.pragma.api.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class LoanApplicationRequestDTO {

    @NotBlank(message = "- Id user is required")
    String idUser;

    @NotBlank(message = "- Document id is required")
    String documentNumber;

    @NotNull(message = "- Loan type is required")
    Long idLoanType;

    @NotNull(message = "- Loan type is required")
    Long idStatus;

    @NotNull(message = "- Amound is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amound must be greater than 0")
    Double amount;

    @NotNull(message = "- Base salary is required")
    Integer term;

}
