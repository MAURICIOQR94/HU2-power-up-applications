package co.com.pragma.api.services.loanapplication;

import co.com.pragma.api.dto.LoanApplicationRequestDTO;
import co.com.pragma.api.handlers.ValidatorHandler;
import co.com.pragma.api.mapper.LoanApplicationDTOMapper;
import co.com.pragma.api.mapper.ResponseDTO;
import co.com.pragma.common.exception.GeneralException;
import co.com.pragma.usecase.registerloanapplication.RegisterLoanApplicationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static co.com.pragma.common.enums.GeneralExceptionMessage.INVALID_BODY_PARAMETER;

@Log4j2
@Component
@RequiredArgsConstructor
public class LoanApplicationHandler {

    private final RegisterLoanApplicationUseCase registerLoanApplicationUseCase;
    private final TransactionalOperator transactionalOperator;
    private final LoanApplicationDTOMapper requestMapper;
    private final ValidatorHandler validatorHandler;

    public Mono<ServerResponse> save(ServerRequest serverRequest) {
        log.info("Processing loan application registration");
        return serverRequest.bodyToMono(LoanApplicationRequestDTO.class)
                .switchIfEmpty(Mono.error(new GeneralException(INVALID_BODY_PARAMETER)))
                .doOnNext(validatorHandler::validateObject)
                .map(requestMapper::toEntity)
                .flatMap(loanApplication -> registerLoanApplicationUseCase.execute(loanApplication)
                        .doOnSuccess(la -> log.info("Loan application successfully registered: {}", la.getId()))
                )
                .map(requestMapper::toDTO)
                .flatMap(loanApplication -> ResponseDTO.success(serverRequest, loanApplication))
                .as(transactionalOperator::transactional);
    }
}
