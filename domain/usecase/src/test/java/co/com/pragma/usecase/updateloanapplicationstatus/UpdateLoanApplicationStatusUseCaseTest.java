package co.com.pragma.usecase.updateloanapplicationstatus;

import co.com.pragma.common.exception.BusinessException;
import co.com.pragma.domain.gateways.restconsumer.ExternalService;
import co.com.pragma.domain.gateways.sqs.QueueSenderService;
import co.com.pragma.model.applicationstatus.ApplicationStatus;
import co.com.pragma.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.util.NotificationMessage;
import co.com.pragma.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static co.com.pragma.common.enums.BusinessExceptionMessage.CANNOT_CHANGE_STATUS;
import static co.com.pragma.common.enums.BusinessExceptionMessage.STATUS_ALREADY_SET;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.UUID;

@ExtendWith(SpringExtension.class)
class UpdateLoanApplicationStatusUseCaseTest {

    private static final String APROBADO = "APROBADO";
    private static final String PENDIENTE = "PENDIENTE";
    private static final String RECHAZADO = "RECHAZADO";

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private ApplicationStatusRepository applicationStatusRepository;

    @Mock
    private ExternalService externalService;

    @Mock
    private QueueSenderService queueSenderService;

    @InjectMocks
    private UpdateLoanApplicationStatusUseCase useCase;

    private UUID loanApplicationId;
    private LoanApplication pendingLoanApplication;
    private LoanApplication aprovedLoanApplication;
    private LoanApplication rejectedLoanApplication;
    private ApplicationStatus pendingStatus;
    private ApplicationStatus approvedStatus;
    private ApplicationStatus rejectedStatus;

    @BeforeEach
    void setUp() {
        loanApplicationId = UUID.randomUUID();

        pendingStatus = ApplicationStatus.builder().id(1L).name(PENDIENTE).build();
        approvedStatus = ApplicationStatus.builder().id(2L).name(APROBADO).build();
        rejectedStatus = ApplicationStatus.builder().id(3L).name(RECHAZADO).build();

        pendingLoanApplication = LoanApplication.builder()
                .id(loanApplicationId)
                .email("test@example.com")
                .status(pendingStatus)
                .build();

        aprovedLoanApplication = LoanApplication.builder()
                .id(loanApplicationId)
                .status(approvedStatus)
                .build();

        rejectedLoanApplication = LoanApplication.builder()
                .id(loanApplicationId)
                .status(rejectedStatus)
                .build();
    }

    @Test
    void execute() {
        when(loanApplicationRepository.findById(loanApplicationId))
                .thenReturn(Mono.just(pendingLoanApplication));
        when(applicationStatusRepository.findById(pendingStatus.getId()))
                .thenReturn(Mono.just(pendingStatus));
        when(applicationStatusRepository.findByName(APROBADO))
                .thenReturn(Mono.just(approvedStatus));
        when(loanApplicationRepository.update(any(LoanApplication.class)))
                .thenReturn(Mono.just(pendingLoanApplication.toBuilder().status(approvedStatus).build()));
        when(externalService.getUserByEmailAsClient(anyString()))
                .thenReturn(Mono.just(User.builder().email("test@example.com").firstName("John").lastName("Test").build()));
        when(queueSenderService.send(any(),any()))
                .thenReturn(Mono.empty());

        Mono<LoanApplication> resultMono = useCase.execute(loanApplicationId, APROBADO);

        StepVerifier.create(resultMono)
                .expectNextMatches(loanApplication ->
                        loanApplication.getStatus().getName().equals(APROBADO)
                )
                .verifyComplete();

        verify(loanApplicationRepository, times(1)).findById(loanApplicationId);
        verify(applicationStatusRepository, times(1)).findById(pendingStatus.getId());
        verify(applicationStatusRepository, times(1)).findByName(APROBADO);
        verify(loanApplicationRepository, times(1)).update(any(LoanApplication.class));
        verify(externalService, times(1)).getUserByEmailAsClient("test@example.com");
        verify(queueSenderService, times(1)).send(any(),any());
    }

    @Test
    void executeFailedStatusAlreadySet(){
        when(loanApplicationRepository.findById(loanApplicationId))
                .thenReturn(Mono.just(aprovedLoanApplication));
        when(applicationStatusRepository.findById(approvedStatus.getId()))
                .thenReturn(Mono.just(approvedStatus));

        Mono<LoanApplication> resultMono = useCase.execute(loanApplicationId, APROBADO);

        StepVerifier.create(resultMono)
                .expectErrorMatches(ex ->
                        ex instanceof BusinessException && ex.getMessage().equals(STATUS_ALREADY_SET.getMessage()))
                .verify();

    }

    @Test
    void executeFailedCannotChangeStatus(){
        when(loanApplicationRepository.findById(loanApplicationId))
                .thenReturn(Mono.just(rejectedLoanApplication));
        when(applicationStatusRepository.findById(rejectedStatus.getId()))
                .thenReturn(Mono.just(rejectedStatus));

        Mono<LoanApplication> resultMono = useCase.execute(loanApplicationId, APROBADO);

        StepVerifier.create(resultMono)
                .expectErrorMatches(ex ->
                        ex instanceof BusinessException && ex.getMessage().equals(CANNOT_CHANGE_STATUS.getMessage()))
                .verify();

    }
}