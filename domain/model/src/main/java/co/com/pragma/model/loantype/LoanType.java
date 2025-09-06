package co.com.pragma.model.loantype;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
public class LoanType {

    private Long id;
    private String name;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private Float interestRate;
    private boolean automaticValidation;

}
