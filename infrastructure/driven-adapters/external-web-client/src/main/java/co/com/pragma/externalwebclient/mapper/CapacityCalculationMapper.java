package co.com.pragma.externalwebclient.mapper;

import co.com.pragma.externalwebclient.dto.CapacityCalculationResponseDTO;
import co.com.pragma.model.lambdavalidation.ValidationResult;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CapacityCalculationMapper {

    ValidationResult toModel(CapacityCalculationResponseDTO responseDTO);
}
