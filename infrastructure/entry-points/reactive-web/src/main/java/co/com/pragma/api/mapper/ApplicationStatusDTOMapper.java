package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.ApplicationStatusResponseDTO;
import co.com.pragma.model.applicationstatus.ApplicationStatus;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ApplicationStatusDTOMapper {
    ApplicationStatusResponseDTO toDto(ApplicationStatus status);
}
