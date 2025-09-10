package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.applicationstatus.ApplicationStatus;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.r2dbc.entity.LoanApplicationEntity;
import co.com.pragma.r2dbc.mapper.LoanApplicationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ILoanApplicationRepositoryAdapterTest {

    @InjectMocks
    LoanApplicationRepositoryAdapter repositoryAdapter;

    @Mock
    ILoanApplicationRepository repository;

    @Mock
    private LoanApplicationMapper mapper;

    @Mock
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    private LoanApplication domain;
    private LoanApplicationEntity entity;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        UUID idUser = UUID.randomUUID();
        entity = LoanApplicationEntity.builder()
                .id(id)
                .idUser(idUser)
                .documentNumber("123456789")
                .amount(5000000.0)
                .term(24)
                .idLoanType(1L)
                .idStatus(1L)
                .createdAt(LocalDateTime.now())
                .build();

        domain = LoanApplication.builder()
                .id(id)
                .idUser(idUser)
                .documentNumber("123456789")
                .amount(5000000.0)
                .term(24)
                .loanType(LoanType.builder().id(1L).build())
                .status(ApplicationStatus.builder().id(1L).build())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void mustFindValueById() {

        when(repository.findById(id)).thenReturn(Mono.just(entity));
        when(mapper.toEntity(entity)).thenReturn(domain);

        Mono<LoanApplication> result = repositoryAdapter.findById(id);

        StepVerifier.create(result)
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        when(mapper.toData(domain)).thenReturn(entity);
        when(r2dbcEntityTemplate.insert(entity)).thenReturn(Mono.just(entity));
        when(mapper.toEntity(entity)).thenReturn(domain);

        Mono<LoanApplication> result = repositoryAdapter.save(domain);

        StepVerifier.create(result)
                .assertNext(saved -> {
                    assertEquals(domain.getIdUser(), saved.getIdUser());
                    assertEquals(domain.getDocumentNumber(), saved.getDocumentNumber());
                    assertEquals(domain.getAmount(), saved.getAmount());
                    assertEquals(domain.getTerm(), saved.getTerm());
                    assertEquals(domain.getLoanType().getId(), saved.getLoanType().getId());
                    assertEquals(domain.getStatus().getId(), saved.getStatus().getId());

                    assertNotNull(saved.getId());
                    assertNotNull(saved.getCreatedAt());
                })
                .verifyComplete();
    }
}
