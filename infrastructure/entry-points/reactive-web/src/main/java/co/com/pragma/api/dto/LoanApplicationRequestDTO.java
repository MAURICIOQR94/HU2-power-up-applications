package co.com.pragma.api.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.validator.constraints.UUID;

@Value
@Builder
@Jacksonized
public class LoanApplicationRequestDTO {

    @UUID
    @NotBlank(message = "- Id user is required")
    String idUser;

    @NotBlank(message = "- Document number is required")
    String documentNumber;

    @NotNull(message = "- Loan type is required")
    Long idLoanType;

    @NotNull(message = "- Amound is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amound must be greater than 0")
    Double amount;

    @Min(value = 1, message = "- The minimum application period must be 1 month")
    @NotNull(message = "- Term is required")
    Integer term;

}
