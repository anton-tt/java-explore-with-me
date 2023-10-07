package ru.practicum.mainservice.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.mainservice.user.dto.RequestUserDto;
import ru.practicum.mainservice.user.dto.ResponseUserDto;
import ru.practicum.mainservice.user.dto.ShortResponseUserDto;
import ru.practicum.mainservice.user.model.User;

@UtilityClass
public class UserMapper {

    public User toUser(RequestUserDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public ResponseUserDto toResponseUserDto(User user) {
        return ResponseUserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public ShortResponseUserDto toShortResponseUserDto(User user) {
        return ShortResponseUserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

}