package ru.practicum.mainservice.user.service;

import ru.practicum.mainservice.user.dto.RequestUserDto;
import ru.practicum.mainservice.user.dto.ResponseUserDto;
import java.util.List;

public interface UserService {

    ResponseUserDto create(RequestUserDto userDto);

    List<ResponseUserDto> getAll(List<Long> ids, Integer from, Integer size);

    void delete(long id);

}