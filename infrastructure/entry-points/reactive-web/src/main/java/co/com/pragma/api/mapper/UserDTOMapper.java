package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.UserResponseDTO;
import co.com.pragma.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDTOMapper {
    UserResponseDTO toDto(User user);
}
