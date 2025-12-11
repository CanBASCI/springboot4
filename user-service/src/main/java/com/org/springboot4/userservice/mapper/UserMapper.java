package com.org.springboot4.userservice.mapper;

import com.org.springboot4.userservice.domain.User;
import com.org.springboot4.userservice.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto dto);
}

