package co.com.pragma.model.lambdavalidation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PaymentPlan {

    private Integer quota;
    private Double capital;
    private Double interest;
    private Double total;
}
