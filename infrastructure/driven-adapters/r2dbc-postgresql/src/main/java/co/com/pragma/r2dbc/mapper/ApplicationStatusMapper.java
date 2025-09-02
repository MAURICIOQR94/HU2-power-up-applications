package co.com.pragma.r2dbc.mapper;

import co.com.pragma.model.applicationstatus.ApplicationStatus;
import co.com.pragma.r2dbc.entity.ApplicationStatusEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ApplicationStatusMapper {

    ApplicationStatus toEntity(ApplicationStatusEntity entity);
    ApplicationStatusEntity toData(ApplicationStatus data);

}
