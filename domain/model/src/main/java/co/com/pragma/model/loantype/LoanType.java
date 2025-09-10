package co.com.pragma.model.loantype;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class LoanType {

    private Long id;
    private String name;
    private Double minAmount;
    private Double maxAmount;
    private Float interestRate;
    private boolean automaticValidation;

}
