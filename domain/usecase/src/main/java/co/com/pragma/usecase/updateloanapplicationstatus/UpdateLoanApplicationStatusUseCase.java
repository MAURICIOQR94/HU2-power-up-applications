package co.com.pragma.usecase.updateloanapplicationstatus;

import co.com.pragma.common.exception.BusinessException;
import co.com.pragma.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static co.com.pragma.common.enums.BusinessExceptionMessage.*;

@RequiredArgsConstructor
public class UpdateLoanApplicationStatusUseCase {

    private static final String PENDIENTE = "PENDIENTE";
    private static final String REVISION_MANUAL = "REVISION_MANUAL";
    private static final String APPROBADO = "APPROBADO";
    private static final String RECHAZADO = "RECHAZADO";
    private static final String CANCELADO = "CANCELADO";

    private final LoanApplicationRepository loanApplicationRepository;
    private final ApplicationStatusRepository applicationStatusRepository;

    public Mono<LoanApplication> execute(UUID id, String newStatus) {
        return loanApplicationRepository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(LOAN_APPLICATION_NOT_FOUND)))
                .flatMap(loanApplication -> applicationStatusRepository.findById(loanApplication.getStatus().getId())
                        .switchIfEmpty(Mono.error(new BusinessException(APPLICATION_STATUS_NOT_FOUND)))
                        .flatMap(currentStatus -> {
                            if (currentStatus.getName().equals(newStatus)) {
                                return Mono.error(new BusinessException(STATUS_ALREADY_SET));
                            }
                            if (isValidStatusTransition(currentStatus.getName(), newStatus)) {
                                return Mono.error(new BusinessException(CANNOT_CHANGE_STATUS));
                            }

                            return applicationStatusRepository.findByName(newStatus.toUpperCase())
                                    .switchIfEmpty(Mono.error(new BusinessException(APPLICATION_STATUS_NOT_FOUND)))
                                    .flatMap(newStatusEntity -> {
                                        loanApplication.setStatus(newStatusEntity);

                                        return loanApplicationRepository.update(loanApplication);
                                    });
                        }));
    }

    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        Map<String, List<String>> allowedTransitions = Map.of(
                PENDIENTE, Arrays.asList(APPROBADO, RECHAZADO, CANCELADO),
                REVISION_MANUAL, Arrays.asList(APPROBADO, RECHAZADO, CANCELADO),
                APPROBADO, List.of(CANCELADO),
                RECHAZADO, List.of(),
                CANCELADO, List.of()
        );

        return allowedTransitions.getOrDefault(currentStatus.toUpperCase(), List.of())
                .contains(newStatus.toUpperCase());
    }

}
