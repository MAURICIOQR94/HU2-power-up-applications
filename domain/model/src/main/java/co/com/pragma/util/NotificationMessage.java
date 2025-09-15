package co.com.pragma.util;

import co.com.pragma.model.lambdavalidation.PaymentPlan;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class NotificationMessage {

    private String email;
    private String name;
    private String status;
    private List<PaymentPlan> paymentPlan;
}
