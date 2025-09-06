package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.applicationstatus.ApplicationStatus;

import co.com.pragma.r2dbc.entity.ApplicationStatusEntity;
import co.com.pragma.r2dbc.mapper.ApplicationStatusMapper;
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
class ApplicationStatusRepositoryAdapterTest {

    @InjectMocks
    private ApplicationStatusRepositoryAdapter repositoryAdapter;

    @Mock
    private IApplicationStatusRepository repository;

    @Mock
    private ApplicationStatusMapper mapper;

    private ApplicationStatus applicationStatus;
    private ApplicationStatusEntity entity;

    @BeforeEach
    void setUp() {
        applicationStatus = ApplicationStatus.builder().id(1L).name("PENDING").build();
        entity = ApplicationStatusEntity.builder().id(1L).name("PENDING").build();
    }

    @Test
    void findByName() {
        when(repository.findByName(applicationStatus.getName())).thenReturn(Mono.just(entity));
        when(mapper.toEntity(entity)).thenReturn(applicationStatus);

        Mono<ApplicationStatus> result = repositoryAdapter.findByName(applicationStatus.getName());

        StepVerifier.create(result)
                .expectNext(applicationStatus)
                .verifyComplete();
    }
}