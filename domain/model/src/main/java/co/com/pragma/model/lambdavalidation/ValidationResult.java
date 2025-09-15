package co.com.pragma.model.lambdavalidation;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class ValidationResult {

    private String finalStatus;
    private List<PaymentPlan> paymentPlan;
}
