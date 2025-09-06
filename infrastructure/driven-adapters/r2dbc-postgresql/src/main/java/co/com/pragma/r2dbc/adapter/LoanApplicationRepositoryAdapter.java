package co.com.pragma.r2dbc.adapter;

import co.com.pragma.common.exception.TechnicalException;
import co.com.pragma.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.entity.LoanApplicationEntity;
import co.com.pragma.r2dbc.mapper.LoanApplicationMapper;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static co.com.pragma.common.enums.TechnicalExceptionMessage.LOAN_APPLICATION_SAVE;

@Repository
public class LoanApplicationRepositoryAdapter extends ReactiveAdapterOperations<LoanApplication, LoanApplicationEntity, UUID, ILoanApplicationRepository> implements LoanApplicationRepository {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    public LoanApplicationRepositoryAdapter(ILoanApplicationRepository repository, LoanApplicationMapper mapper, R2dbcEntityTemplate r2dbcEntityTemplate) {
        super(repository, mapper::toData, mapper::toEntity);
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    @Override
    public Mono<LoanApplication> save(LoanApplication loanApplication) {
        return Mono.just(loanApplication)
                .map(this::toData)
                .flatMap(r2dbcEntityTemplate::insert)
                .map(this::toEntity)
                .onErrorMap(e -> new TechnicalException(e, LOAN_APPLICATION_SAVE));
    }

    @Override
    public Mono<LoanApplication> findById(UUID id) {
        return repository.findById(id)
                .map(this::toEntity);
    }
}
