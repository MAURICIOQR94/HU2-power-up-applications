package co.com.pragma.externalwebclient.dto;

import co.com.pragma.model.lambdavalidation.PaymentPlan;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CapacityCalculationResponseDTO {

    private String finalStatus;
    private List<PaymentPlan> paymentPlan;

}
