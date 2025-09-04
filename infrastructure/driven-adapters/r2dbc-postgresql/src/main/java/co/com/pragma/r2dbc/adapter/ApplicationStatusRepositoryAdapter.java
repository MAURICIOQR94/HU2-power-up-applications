package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.applicationstatus.ApplicationStatus;
import co.com.pragma.model.applicationstatus.gateways.ApplicationStatusRepository;
import co.com.pragma.r2dbc.entity.ApplicationStatusEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.mapper.ApplicationStatusMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class ApplicationStatusRepositoryAdapter extends ReactiveAdapterOperations<ApplicationStatus, ApplicationStatusEntity, Long, IApplicationStatusRepository> implements ApplicationStatusRepository {

    protected ApplicationStatusRepositoryAdapter(IApplicationStatusRepository repository, ApplicationStatusMapper mapper) {
        super(repository, mapper::toData, mapper::toEntity);
    }

    @Override
    public Mono<ApplicationStatus> findByName(String name) {
        return repository.findByName(name)
                .map(this::toEntity);
    }
}
