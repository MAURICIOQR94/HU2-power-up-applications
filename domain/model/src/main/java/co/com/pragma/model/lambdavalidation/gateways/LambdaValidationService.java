package co.com.pragma.model.lambdavalidation.gateways;

import co.com.pragma.model.lambdavalidation.ValidationResult;
import co.com.pragma.model.lambdavalidation.CapacityCalculation;
import reactor.core.publisher.Mono;

public interface LambdaValidationService {
    Mono<ValidationResult> validateLoanApplication(CapacityCalculation request);
}
