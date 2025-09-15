package co.com.pragma.model.lambdavalidation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class CapacityCalculation {

    private Double baseSalary;
    private Double amount;
    private Integer term;
    private Float interestRate;
    private Double totalMonthlyPayment;
}
