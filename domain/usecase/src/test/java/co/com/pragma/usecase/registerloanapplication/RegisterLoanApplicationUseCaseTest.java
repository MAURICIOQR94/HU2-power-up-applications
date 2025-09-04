package co.com.pragma.usecase.registerloanapplication;

import co.com.pragma.model.applicationstatus.ApplicationStatus;
import co.com.pragma.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class RegisterLoanApplicationUseCaseTest {

    private LoanTypeRepository loanTypeRepository;
    private LoanApplicationRepository loanApplicationRepository;
    private RegisterLoanApplicationUseCase registerLoanApplicationUseCase;
    private ApplicationStatusRepository applicationStatusRepository;

    private LoanApplication loanApplication;
    private UUID id;


    @BeforeEach
    void setUp() {
        loanApplicationRepository = mock(LoanApplicationRepository.class);
        loanTypeRepository = mock(LoanTypeRepository.class);
        applicationStatusRepository = mock(ApplicationStatusRepository.class);
        registerLoanApplicationUseCase = new RegisterLoanApplicationUseCase(loanApplicationRepository , loanTypeRepository, applicationStatusRepository);

        id = UUID.randomUUID();
        loanApplication = LoanApplication.builder()
                .id(id)
                .idUser(UUID.randomUUID())
                .documentNumber("123456")
                .amount(BigDecimal.valueOf(10000.0))
                .term(12)
                .loanType(LoanType.builder().id(1L).name("CONSUMO").build())
                .status(ApplicationStatus.builder().id(1L).name("PENDING").build())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldRegisterApplicationSuccessfully() {
        LoanType loanType = LoanType.builder().id(1L).name("CONSUMO").build();
        ApplicationStatus status = ApplicationStatus.builder().id(1L).name("PENDIENTE").build();

        when(loanTypeRepository.findByName(anyString())).thenReturn(Mono.just(loanType));
        when(applicationStatusRepository.findByName(anyString())).thenReturn(Mono.just(status));
        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(registerLoanApplicationUseCase.execute(loanApplication))
                .assertNext(saved -> {
                    assertNotNull(saved.getId());
                    assertEquals("CONSUMO", saved.getLoanType().getName());
                    assertEquals("PENDIENTE", saved.getStatus().getName());
                })
                .verifyComplete();

        verify(loanApplicationRepository, times(1)).save(any(LoanApplication.class));
    }

    @Test
    void shouldFailToRegisterApplicationIfRepositoryFails() {
        LoanType loanType = LoanType.builder().id(1L).name("CONSUMO").build();
        ApplicationStatus status = ApplicationStatus.builder().id(1L).name("PENDIENTE").build();

        when(loanTypeRepository.findByName(anyString())).thenReturn(Mono.just(loanType));
        when(applicationStatusRepository.findByName(anyString())).thenReturn(Mono.just(status));
        when(loanApplicationRepository.save(any(LoanApplication.class)))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(registerLoanApplicationUseCase.execute(loanApplication))
                .expectErrorMatches(ex -> ex instanceof RuntimeException &&
                        ex.getMessage().equals("DB error"))
                .verify();

        verify(loanApplicationRepository, times(1)).save(any(LoanApplication.class));
    }
}