package co.com.pragma.usecase.registerloanapplication;

import co.com.pragma.common.exception.BusinessException;
import co.com.pragma.domain.gateways.restconsumer.ExternalService;
import co.com.pragma.domain.gateways.sqs.QueueSenderService;
import co.com.pragma.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.pragma.model.lambdavalidation.ValidationResult;
import co.com.pragma.model.lambdavalidation.gateways.LambdaValidationService;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.applicationstatus.ApplicationStatus;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterLoanApplicationUseCaseTest {

    private static final String DEFAULT_STATUS_NAME = "PENDIENTE";
    private static final String LOAN_TYPE_CONSUMO = "CONSUMO";
    private static final String STATUS_APROVED = "APROBADO";

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private ApplicationStatusRepository applicationStatusRepository;

    @Mock
    private ExternalService externalService;

    @Mock
    private LambdaValidationService lambdaValidationService;

    @Mock
    private QueueSenderService queueSenderService;

    @InjectMocks
    private RegisterLoanApplicationUseCase useCase;

    private LoanApplication loanApplication;
    private ApplicationStatus status;
    private LoanType loanType;
    private User user;

    @BeforeEach
    void setUp() {
        status = ApplicationStatus.builder().id(1L).name(DEFAULT_STATUS_NAME).build();
        loanType = LoanType.builder().id(1L).name(LOAN_TYPE_CONSUMO).build();

        loanApplication = LoanApplication.builder()
                .id(UUID.randomUUID())
                .idUser(UUID.randomUUID())
                .email("mauricio@email.com")
                .amount(10000.0)
                .term(12)
                .status(status)
                .loanType(loanType)
                .createdAt(LocalDateTime.now())
                .build();

        user = User.builder()
                .firstName("Mauricio")
                .documentNumber("123456")
                .lastName("Quintero")
                .email("mauricio@email.com")
                .baseSalary(5000.0)
                .build();
    }

    @Test
    void registerApplicationManualValidation() {
        when(externalService.getUserByEmailAsService(loanApplication.getEmail())).thenReturn(Mono.just(user));
        when(loanTypeRepository.findByName(anyString())).thenReturn(Mono.just(loanType));
        when(applicationStatusRepository.findByName(anyString())).thenReturn(Mono.just(status));
        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<LoanApplication> result = useCase.execute(loanApplication);

        StepVerifier.create(result)
                .assertNext(saved -> {
                    assertNotNull(saved.getId());
                    assertEquals(LOAN_TYPE_CONSUMO, saved.getLoanType().getName());
                    assertEquals(DEFAULT_STATUS_NAME, saved.getStatus().getName());
                })
                .verifyComplete();

        verify(loanApplicationRepository, times(1)).save(any(LoanApplication.class));
    }

    @Test
    void registerLoanApplicationAutomaticValidationApproved() {
        loanType.setAutomaticValidation(true);

        ApplicationStatus approvedStatus = ApplicationStatus.builder().id(2L).name(STATUS_APROVED).build();

        ValidationResult validationResult = ValidationResult.builder()
                .finalStatus(STATUS_APROVED)
                .paymentPlan(List.of())
                .build();

        when(externalService.getUserByEmailAsService(anyString())).thenReturn(Mono.just(user));
        when(loanTypeRepository.findByName(LOAN_TYPE_CONSUMO)).thenReturn(Mono.just(loanType));
        when(applicationStatusRepository.findByName(STATUS_APROVED)).thenReturn(Mono.just(approvedStatus));
        when(loanApplicationRepository.findAllByDocumentNumberAndIdStatus(anyString(), anyLong()))
                .thenReturn(Flux.empty());
        when(lambdaValidationService.validateLoanApplication(any())).thenReturn(Mono.just(validationResult));
        when(queueSenderService.send(any(),any())).thenReturn(Mono.empty());
        when(loanApplicationRepository.save(any())).thenAnswer(invocation ->
                Mono.just(invocation.getArgument(0)));

        StepVerifier.create(useCase.execute(loanApplication))
                .assertNext(saved -> {
                    assertNotNull(saved.getId());
                    assertEquals(LOAN_TYPE_CONSUMO, saved.getLoanType().getName());
                    assertEquals(STATUS_APROVED, saved.getStatus().getName());
                })
                .verifyComplete();

        verify(queueSenderService, times(2)).send(any(),any());
        verify(loanApplicationRepository, times(1)).save(any());
    }

    @Test
    void failToRegisterApplicationIfRepositoryFails() {
        when(externalService.getUserByEmailAsService(anyString())).thenReturn(Mono.just(user));
        when(loanTypeRepository.findByName(anyString())).thenReturn(Mono.just(loanType));
        when(applicationStatusRepository.findByName(anyString())).thenReturn(Mono.just(status));
        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        Mono<LoanApplication> result = useCase.execute(loanApplication);

        StepVerifier.create(result)
                .expectErrorMatches(ex -> ex instanceof RuntimeException &&
                        ex.getMessage().equals("DB error"))
                .verify();

        verify(loanApplicationRepository, times(1)).save(any(LoanApplication.class));
    }

    @Test
    void failWhenLoanTypeNotFound() {
        when(externalService.getUserByEmailAsService(anyString())).thenReturn(Mono.just(user));
        when(loanTypeRepository.findByName(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(loanApplication))
                .expectErrorMatches(BusinessException.class::isInstance)
                .verify();

        verify(loanApplicationRepository, never()).save(any());
    }

    @Test
    void failWhenApplicationStatusNotFound() {
        loanType.setAutomaticValidation(false);
        when(externalService.getUserByEmailAsService(anyString())).thenReturn(Mono.just(user));
        when(loanTypeRepository.findByName(anyString())).thenReturn(Mono.just(loanType));
        when(applicationStatusRepository.findByName(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.execute(loanApplication))
                .expectErrorMatches(BusinessException.class::isInstance)
                .verify();

        verify(loanApplicationRepository, never()).save(any());
    }
}