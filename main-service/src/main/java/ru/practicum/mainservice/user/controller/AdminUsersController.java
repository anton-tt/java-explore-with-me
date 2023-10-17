package ru.practicum.mainservice.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.user.dto.RequestUserDto;
import ru.practicum.mainservice.user.dto.ResponseUserDto;
import ru.practicum.mainservice.user.service.UserService;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminUsersController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseUserDto createUser(@RequestBody @Valid RequestUserDto userDto) {
        log.info("");
        log.info("Добавление нового пользователя: {}", userDto);
        return userService.create(userDto);
    }

    @GetMapping
    public List<ResponseUserDto> getAllUsers(@RequestParam(required = false) List<Long> ids,
                                             @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                             @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("");
        log.info("Поиск пользователей по заданным параметрам");
        return userService.getAll(ids, from, size);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        log.info("");
        log.info("Удаление всех данных пользователя c id = {}", id);
        userService.delete(id);
    }

}