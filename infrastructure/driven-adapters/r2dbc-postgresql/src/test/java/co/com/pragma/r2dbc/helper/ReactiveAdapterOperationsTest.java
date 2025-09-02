package co.com.pragma.r2dbc.helper;

import co.com.pragma.r2dbc.adapter.ILoanApplicationRepository;
import co.com.pragma.r2dbc.entity.LoanApplicationEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;
import java.util.function.Function;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

class ReactiveAdapterOperationsTest {

    private ILoanApplicationRepository repository;
    private LoanApplicationAdapter operations;

    private final UUID id = UUID.randomUUID();
    private final TestLoanApplication domain = new TestLoanApplication(id, "maria@test.com");
    private final LoanApplicationEntity data = Mockito.mock(LoanApplicationEntity.class);

    @BeforeEach
    void setUp() {
        repository = mock(ILoanApplicationRepository.class);

        Function<TestLoanApplication, LoanApplicationEntity> toDataFn = e -> data;
        Function<LoanApplicationEntity, TestLoanApplication> toEntityFn = d -> domain;

        operations = new LoanApplicationAdapter(repository, toDataFn, toEntityFn);
    }

    @Test
    void toData_returnsData() {
        Assertions.assertSame(data, operations.toData(domain));
    }

    @Test
    void toEntity_returnsDomain() {
        Assertions.assertSame(domain, operations.toEntity(data));
    }

    @Test
    void save_returnsMappedDomain() {
        when(repository.save(data)).thenReturn(Mono.just(data));

        StepVerifier.create(operations.save(domain))
                .expectNext(domain)
                .verifyComplete();

        verify(repository).save(data);
    }

    @Test
    void findById_returnsMappedDomain() {
        when(repository.findById(id)).thenReturn(Mono.just(data));

        StepVerifier.create(operations.findById(id))
                .expectNext(domain)
                .verifyComplete();

        verify(repository).findById(id);
    }

    static final class TestLoanApplication {
        final UUID id;
        final String documentNumber;
        TestLoanApplication(UUID id, String documentNumber) { this.id = id; this.documentNumber = documentNumber; }
    }

    static final class LoanApplicationAdapter extends ReactiveAdapterOperations<TestLoanApplication, LoanApplicationEntity, UUID, ILoanApplicationRepository> {
        LoanApplicationAdapter(ILoanApplicationRepository repo, Function<TestLoanApplication, LoanApplicationEntity> toData, Function<LoanApplicationEntity, TestLoanApplication> toEntity) {
            super(repo, toData, toEntity);
        }
        public Mono<TestLoanApplication> findById(UUID id) {
            return repository.findById(id)
                    .map(this::toEntity);
        }
    }
}
