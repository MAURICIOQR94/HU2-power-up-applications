package co.com.pragma.r2dbc.adapter;

import co.com.pragma.common.exception.TechnicalException;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.r2dbc.entity.LoanTypeEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.mapper.LoanTypeMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import static co.com.pragma.common.enums.TechnicalExceptionMessage.LOAN_TYPE_FIND_BY_ID;

@Repository
public class LoanTypeRepositoryAdapter extends ReactiveAdapterOperations<LoanType, LoanTypeEntity, Long, ILoanTypeRepository> implements LoanTypeRepository {

    public LoanTypeRepositoryAdapter(ILoanTypeRepository repository, LoanTypeMapper mapper) {
        super(repository, mapper::toData, mapper::toEntity);
    }

    @Override
    public Mono<LoanType> findById(Long id) {
        return repository.findById(id)
                .map(this::toEntity)
                .onErrorMap(e -> new TechnicalException(e, LOAN_TYPE_FIND_BY_ID));
    }

    @Override
    public Mono<LoanType> findByName(String name) {
        return repository.findByName(name)
                .map(this::toEntity);
    }
}