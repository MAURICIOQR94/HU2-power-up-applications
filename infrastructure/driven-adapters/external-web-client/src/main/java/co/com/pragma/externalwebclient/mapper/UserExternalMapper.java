package co.com.pragma.externalwebclient.mapper;

import co.com.pragma.externalwebclient.dto.UserExternalResponseDTO;
import co.com.pragma.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserExternalMapper {

    User toModel(UserExternalResponseDTO userExternalResponseDTO);

}
