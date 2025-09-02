package co.com.pragma.api.services.loanapplication;

import co.com.pragma.api.dto.LoanApplicationRequestDTO;
import co.com.pragma.api.mapper.LoanApplicationDTOMapper;
import co.com.pragma.api.mapper.ResponseDTO;
import co.com.pragma.usecase.registerloanapplication.RegisterLoanApplicationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LoanApplicationHandler {
    private final RegisterLoanApplicationUseCase registerLoanApplicationUseCase;
    private final LoanApplicationDTOMapper requestMapper;

    public Mono<ServerResponse> save(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoanApplicationRequestDTO.class)
                .map(requestMapper::toEntity)
                .flatMap(loanApplication -> registerLoanApplicationUseCase.execute(loanApplication))
                .map(requestMapper::toDTO)
                .flatMap(loanApplication -> ResponseDTO.success(serverRequest, loanApplication));
    }

}
