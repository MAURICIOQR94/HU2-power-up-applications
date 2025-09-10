package co.com.pragma.usecase.registerloanapplication;

import co.com.pragma.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.applicationstatus.ApplicationStatus;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.model.loantype.LoanType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterLoanApplicationUseCaseTest {

    private static final String APPLICATION_STATUS = "PENDIENTE";
    private static final String LOAN_TYPE = "CONSUMO";

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private ApplicationStatusRepository applicationStatusRepository;

    @InjectMocks
    private RegisterLoanApplicationUseCase registerLoanApplicationUseCase;

    private LoanApplication loanApplication;
    private ApplicationStatus status;
    private LoanType loanType;

    @BeforeEach
    void setUp() {
        status = ApplicationStatus.builder().id(1L).name(APPLICATION_STATUS).build();
        loanType = LoanType.builder().id(1L).name(LOAN_TYPE).build();

        loanApplication = LoanApplication.builder()
                .id(UUID.randomUUID())
                .idUser(UUID.randomUUID())
                .documentNumber("123456")
                .amount(10000.0)
                .term(12)
                .status(status)
                .loanType(loanType)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldRegisterApplicationSuccessfully() {
        when(loanTypeRepository.findByName(anyString())).thenReturn(Mono.just(loanType));
        when(applicationStatusRepository.findByName(anyString())).thenReturn(Mono.just(status));
        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<LoanApplication> result = registerLoanApplicationUseCase.execute(loanApplication);

        StepVerifier.create(result)
                .assertNext(saved -> {
                    assertNotNull(saved.getId());
                    assertEquals(LOAN_TYPE, saved.getLoanType().getName());
                    assertEquals(APPLICATION_STATUS, saved.getStatus().getName());
                })
                .verifyComplete();

        verify(loanApplicationRepository, times(1)).save(any(LoanApplication.class));
    }

    @Test
    void shouldFailToRegisterApplicationIfRepositoryFails() {
        when(loanTypeRepository.findByName(anyString())).thenReturn(Mono.just(loanType));
        when(applicationStatusRepository.findByName(anyString())).thenReturn(Mono.just(status));
        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        Mono<LoanApplication> result = registerLoanApplicationUseCase.execute(loanApplication);

        StepVerifier.create(result)
                .expectErrorMatches(ex -> ex instanceof RuntimeException &&
                        ex.getMessage().equals("DB error"))
                .verify();

        verify(loanApplicationRepository, times(1)).save(any(LoanApplication.class));
    }
}