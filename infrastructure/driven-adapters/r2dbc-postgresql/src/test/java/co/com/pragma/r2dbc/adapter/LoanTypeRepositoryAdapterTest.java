package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.r2dbc.entity.LoanTypeEntity;
import co.com.pragma.r2dbc.mapper.LoanTypeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanTypeRepositoryAdapterTest {

    @InjectMocks
    private LoanTypeRepositoryAdapter repositoryAdapter;

    @Mock
    private LoanTypeMapper mapper;

    @Mock
    private ILoanTypeRepository repository;

    private LoanType loanType;
    private LoanTypeEntity  entity;

    @BeforeEach
    void setUp() {
        loanType = LoanType.builder().id(1L).name("PERSONAL").build();
        entity = LoanTypeEntity.builder().id(1L).name("PERSONAL").build();
    }

    @Test
    void findById() {
        when(repository.findById(loanType.getId())).thenReturn(Mono.just(entity));
        when(mapper.toEntity(entity)).thenReturn(loanType);

        Mono<LoanType> result = repositoryAdapter.findById(loanType.getId());

        StepVerifier.create(result)
                .expectNext(loanType)
                .verifyComplete();
    }

    @Test
    void findByName() {
        when(repository.findByName(loanType.getName())).thenReturn(Mono.just(entity));
        when(mapper.toEntity(entity)).thenReturn(loanType);

        Mono<LoanType> result = repositoryAdapter.findByName(loanType.getName());

        StepVerifier.create(result)
                .expectNext(loanType)
                .verifyComplete();
    }
}