package co.com.pragma.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoanApplicationResponseDTO {

    private UUID id;
    private Double amount;
    private Integer term;
    private UserResponseDTO user;
    private LoanTypeResponseDTO loanType;
    private ApplicationStatusResponseDTO status;
    private LocalDateTime createdAt;
    private Double monthlyPayment;

    public String getAmount() {
        return amount == null ? null : String.format("%,.2f", amount);
    }

    public String getMonthlyPayment() {
        return monthlyPayment == null ? null : String.format("%,.2f", monthlyPayment);
    }

}
