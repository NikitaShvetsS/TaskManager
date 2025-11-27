package org.example.util;

import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateUserFromDto(UserRequestDto dto, @MappingTarget User user);
    public User toEntity(UserRequestDto dto) {

        return User.builder()
                .email(dto.getEmail())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .role(Role.USER)
                .build();
    }

    public UserResponseDto toDto(User user) {

        return UserResponseDto.builder()
                .id(user.getUuid())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .build();

    }
}
