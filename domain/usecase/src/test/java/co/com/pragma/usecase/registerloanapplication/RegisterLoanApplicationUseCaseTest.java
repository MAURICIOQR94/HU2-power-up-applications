package co.com.pragma.usecase.registerloanapplication;

import co.com.pragma.model.applicationstatus.ApplicationStatus;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class RegisterLoanApplicationUseCaseTest {

    private LoanTypeRepository loanTypeRepository;
    private LoanApplicationRepository loanApplicationRepository;
    private RegisterLoanApplicationUseCase registerLoanApplicationUseCase;

    private LoanApplication loanApplication;
    private UUID id;


    @BeforeEach
    void setUp() {
        loanApplicationRepository = mock(LoanApplicationRepository.class);
        loanTypeRepository = mock(LoanTypeRepository.class);
        registerLoanApplicationUseCase = new RegisterLoanApplicationUseCase(loanApplicationRepository , loanTypeRepository);

        id = UUID.randomUUID();
        loanApplication = LoanApplication.builder()
                .id(id)
                .idUser(UUID.randomUUID())
                .documentNumber("123456")
                .amount(BigDecimal.valueOf(10000.0))
                .term(12)
                .loanType(LoanType.builder().id(1L).build())
                .status(ApplicationStatus.builder().id(1L).build())
                .build();
    }

    @Test
    void shouldRegisterApplicationSuccessfully() {
        when(loanTypeRepository.findById(anyLong())).thenReturn(Mono.just(LoanType.builder().id(1L).build()));

        when(loanApplicationRepository.findById(id)).thenReturn(Mono.empty());

        when(loanApplicationRepository.save(loanApplication)).thenReturn(Mono.just(loanApplication));

        StepVerifier.create(registerLoanApplicationUseCase.execute(loanApplication))
                .expectNext(loanApplication)
                .verifyComplete();

        verify(loanApplicationRepository, times(1)).save(loanApplication);
    }

    @Test
    void shouldFailToRegisterApplicationIfRepositoryFails() {
        when(loanTypeRepository.findById(anyLong())).thenReturn(Mono.just(LoanType.builder().id(1L).build()));

        when(loanApplicationRepository.findById(id)).thenReturn(Mono.empty());

        when(loanApplicationRepository.save(loanApplication)).thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(registerLoanApplicationUseCase.execute(loanApplication))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException
                        && throwable.getMessage().equals("DB error"))
                .verify();

        verify(loanApplicationRepository, times(1)).save(loanApplication);
    }
}