package co.com.pragma.api.services.loanapplication;

import co.com.pragma.api.config.ApiProperties;
import co.com.pragma.api.dto.LoanApplicationRequestDTO;
import co.com.pragma.api.dto.PagedResponseDTO;
import co.com.pragma.api.dto.common.ResponseDTO;
import co.com.pragma.api.handlers.ValidatorHandler;
import co.com.pragma.api.mapper.LoanApplicationDTOMapper;
import co.com.pragma.common.exception.GeneralException;
import co.com.pragma.domain.gateways.security.JwtUtilService;
import co.com.pragma.model.tokeninfo.TokenInfo;
import co.com.pragma.usecase.getloanapplications.GetLoanApplicationsUseCase;
import co.com.pragma.usecase.registerloanapplication.RegisterLoanApplicationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static co.com.pragma.common.enums.GeneralExceptionMessage.INVALID_BODY_PARAMETER;

@Log4j2
@Component
@RequiredArgsConstructor
public class LoanApplicationHandler {

    private final RegisterLoanApplicationUseCase registerLoanApplicationUseCase;
    private final GetLoanApplicationsUseCase getLoanApplicationsUseCase;
    private final TransactionalOperator transactionalOperator;
    private final LoanApplicationDTOMapper mapper;
    private final ValidatorHandler validatorHandler;
    private final JwtUtilService jwtUtilService;
    private final ApiProperties apiProperties;

    public Mono<ServerResponse> save(ServerRequest serverRequest) {
        log.info("Processing loan application registration");

        String token = serverRequest.headers().firstHeader("Authorization").split(" ")[1];
        return Mono.zip(
                        serverRequest.bodyToMono(LoanApplicationRequestDTO.class)
                                .switchIfEmpty(Mono.error(new GeneralException(INVALID_BODY_PARAMETER)))
                                .doOnNext(validatorHandler::validateObject),
                        jwtUtilService.getClaims(token)
                ).map(tuple -> {
                    LoanApplicationRequestDTO loanApplicationRequestDTO = tuple.getT1();
                    TokenInfo tokenInfo = tuple.getT2();

                    var entity = mapper.toEntity(loanApplicationRequestDTO);
                    entity.setIdUser(UUID.fromString(tokenInfo.getUserId()));
                    entity.setDocumentNumber(tokenInfo.getDocumentNumber());
                    entity.setEmail(tokenInfo.getEmail());

                    return entity;
                }).flatMap(loanApplication ->
                        registerLoanApplicationUseCase.execute(loanApplication)
                                .doOnSuccess(la -> log.info("Loan application successfully registered: {}", la.getId()))
                ).flatMap(saved -> ServerResponse.created(
                        URI.create(apiProperties.basePath().concat(String.format("/%s", saved.getId().toString())))
                ).bodyValue(ResponseDTO.builder()
                        .message("Loan application created successfully")
                        .data(Map.of(
                                "email", saved.getEmail(),
                                "createdAt", saved.getCreatedAt()
                        ))
                        .build()))
                .as(transactionalOperator::transactional);
    }

    public Mono<ServerResponse> findByStatusPaged(ServerRequest request) {
        log.info("Obtaining loan applications by filtering by status and pagination");
        List<String> statuses = request.queryParam("status")
                .map(param -> Arrays.stream(param.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList())
                .orElse(List.of());

        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));

        return getLoanApplicationsUseCase.findByStatusPaged(statuses, page, size)
                .doOnSuccess(x -> log.info("The loan applications were successfully obtained: {} elements", x.getSize()))
                .map(pageResult -> new PagedResponseDTO<>(
                        pageResult.getSize(),
                        pageResult.getTotalPages(),
                        pageResult.getPage(),
                        pageResult.getTotalElements(),
                        pageResult.getData().stream()
                                .map(mapper::toData)
                                .toList()
                ))
                .flatMap(result -> ServerResponse.ok().bodyValue(result));
    }

}
