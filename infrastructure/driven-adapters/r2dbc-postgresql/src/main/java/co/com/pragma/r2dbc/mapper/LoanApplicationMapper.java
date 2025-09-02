package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.applicationstatus.ApplicationStatus;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loantype.LoanType;
import co.com.pragma.r2dbc.entity.LoanApplicationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanApplicationMapper {

    @Mapping(target = "idLoanType", source = "loanType")
    @Mapping(target = "idStatus", source = "status")
    LoanApplicationEntity toData(LoanApplication data);

    @Mapping(target = "loanType", source = "idLoanType")
    @Mapping(target = "status", source = "idStatus")
    LoanApplication toEntity(LoanApplicationEntity entity);

    default LoanType mapLoanType(Long id) {
        return id !=null ? LoanType.builder().id(id).build() : null;
    }

    default ApplicationStatus mapStatus(Long id) {
        return id != null ? ApplicationStatus.builder().id(id).build() : null;
    }

    default Long mapLoanType(LoanType loanType) {
        return loanType.getId();
    }

    default Long mapStatus(ApplicationStatus id) {
        return id.getId();
    }
}
