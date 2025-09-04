package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.LoanApplicationRequestDTO;
import co.com.pragma.api.dto.LoanApplicationResponseDTO;
import co.com.pragma.model.applicationstatus.ApplicationStatus;
import co.com.pragma.model.loanapplication.LoanApplication;
import co.com.pragma.model.loantype.LoanType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanApplicationDTOMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "loanType", source = "loanType")
    LoanApplication toEntity(LoanApplicationRequestDTO loanApplicationRequestDTO);

    LoanApplicationResponseDTO toData(LoanApplication loanApplication);

    default LoanType mapLoanType(String name) {
        return name !=null ? LoanType.builder().name(name).build() : null;
    }

    default ApplicationStatus mapStatus(Long id) {
        return id != null ? ApplicationStatus.builder().id(id).build() : null;
    }

}
