package co.com.pragma.r2dbc.adapter;

import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.model.loantype.gateways.LoanTypeRepository;
import co.com.pragma.r2dbc.entity.LoanTypeEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import co.com.pragma.r2dbc.mapper.LoanTypeMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class LoanTypeRepositoryAdapter extends ReactiveAdapterOperations<LoanType, LoanTypeEntity, Long, ILoanTypeRepository> implements LoanTypeRepository {

    public LoanTypeRepositoryAdapter(ILoanTypeRepository repository, LoanTypeMapper mapper) {
        super(repository, mapper::toData, mapper::toEntity);
    }

    @Override
    public Mono<LoanType> findById(Long id) {
        return repository.findById(id)
                .map(this::toEntity);
    }
}
