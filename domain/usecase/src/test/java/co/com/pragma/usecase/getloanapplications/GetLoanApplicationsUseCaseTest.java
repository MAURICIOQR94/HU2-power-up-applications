package co.com.pragma.usecase.getloanapplications;

import co.com.pragma.domain.gateways.restconsumer.ExternalService;
import co.com.pragma.model.applicationstatus.ApplicationStatus;
import co.com.pragma.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.util.PageResult;
import co.com.pragma.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetLoanApplicationsUseCaseTest {

    @Mock
    private LoanApplicationRepository loanRepository;

    @Mock
    private ApplicationStatusRepository statusRepository;

    @Mock
    private LoanTypeRepository typeRepository;

    @Mock
    private ExternalService externalService;

    @InjectMocks
    private GetLoanApplicationsUseCase useCase;

    @BeforeEach
    void setup() {

    }

    @Test
    void returnEmptyPageWhenNoStatusesFound() {
        when(statusRepository.findByNameIn(any())).thenReturn(Flux.empty());

        Mono<PageResult<LoanApplication>> result =
                useCase.findByStatusPaged(List.of("PENDING"), 0, 10);

        StepVerifier.create(result)
                .expectNextMatches(page ->
                        page.getData().isEmpty()
                                && page.getTotalElements() == 0
                                && page.getTotalPages() == 0
                                && page.getPage() == 0
                                && page.getSize() == 10
                )
                .verifyComplete();
    }

    @Test
    void returnEnrichedLoanApplications() {
        ApplicationStatus status = ApplicationStatus.builder().id(1L).name("APPROVED").build();
        LoanType type = LoanType.builder().id(1L).name("PERSONAL").interestRate(10.0f).build();
        User user = User.builder().email("test@mail.com").firstName("John").lastName("Smith").build();

        LoanApplication loanApp = LoanApplication.builder()
                .id(UUID.randomUUID())
                .email("test@mail.com")
                .amount(12000.0)
                .term(12)
                .status(ApplicationStatus.builder().id(1L).build())
                .loanType(LoanType.builder().id(1L).build())
                .build();

        when(statusRepository.findByNameIn(any())).thenReturn(Flux.just(status));
        when(loanRepository.findByIdStatusInPaged(any(), Mockito.eq(0), Mockito.eq(10))).thenReturn(Flux.just(loanApp));
        when(loanRepository.countByIdStatusIn(any())).thenReturn(Mono.just(1L));
        when(statusRepository.findById(1L)).thenReturn(Mono.just(status));
        when(typeRepository.findById(1L)).thenReturn(Mono.just(type));
        when(externalService.getUserByEmailAsClient("test@mail.com")).thenReturn(Mono.just(user));

        Mono<PageResult<LoanApplication>> result =
                useCase.findByStatusPaged(List.of("APPROVED"), 0, 10);

        StepVerifier.create(result)
                .expectNextMatches(page -> {
                    LoanApplication app = page.getData().getFirst();
                    return page.getTotalElements() == 1
                            && page.getTotalPages() == 1
                            && app.getUser().getFirstName().equals("John")
                            && app.getLoanType().getInterestRate() == 10.0f
                            && app.getMonthlyPayment() != null;
                })
                .verifyComplete();
    }
}
