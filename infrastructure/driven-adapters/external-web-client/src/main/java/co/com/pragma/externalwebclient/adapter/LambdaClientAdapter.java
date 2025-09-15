package co.com.pragma.externalwebclient.adapter;

import co.com.pragma.externalwebclient.mapper.CapacityCalculationMapper;
import co.com.pragma.model.lambdavalidation.ValidationResult;
import co.com.pragma.model.lambdavalidation.gateways.LambdaValidationService;
import co.com.pragma.model.lambdavalidation.CapacityCalculation;
import co.com.pragma.externalwebclient.dto.CapacityCalculationResponseDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class LambdaClientAdapter implements LambdaValidationService {

    private final WebClient webClient;
    private final CapacityCalculationMapper mapper;

    public LambdaClientAdapter(@Qualifier("lambdaServiceWebClient") WebClient webClient,  CapacityCalculationMapper mapper) {
        this.webClient = webClient;
        this.mapper = mapper;
    }

    public Mono<ValidationResult> validateLoanApplication(CapacityCalculation request) {
        return webClient.post()
                .uri("/api/v1/calcular-capacidad")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(CapacityCalculationResponseDTO.class)
                .map(mapper::toModel);
    }

}
