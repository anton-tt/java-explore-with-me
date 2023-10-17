package ru.practicum.mainservice.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.user.dto.RequestUserDto;
import ru.practicum.mainservice.user.dto.ResponseUserDto;
import ru.practicum.mainservice.user.mapper.UserMapper;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public ResponseUserDto create(RequestUserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        log.info("Данные пользователя добавлены в БД: {}.", user);
        ResponseUserDto responseUserDto = UserMapper.toResponseUserDto(user);
        log.info("Новый пользователь создан: {}.", responseUserDto);
        return responseUserDto;
    }

    @Override
    public List<ResponseUserDto> getAll(List<Long> ids, Integer from, Integer size) {
        List<User> userList;
        Pageable pageable = PageRequest.of(from / size, size);
        if (ids == null) {
            userList = userRepository.findAll(pageable).getContent();
        } else {
            userList = userRepository.findAllByIdIn(ids, pageable);
        }

        List<ResponseUserDto> userDtoList = new ArrayList<>();
        if (userList != null) {
            userDtoList = userList
                    .stream()
                    .map(UserMapper::toResponseUserDto)
                    .collect(Collectors.toList());
        }
        log.info("Сформирован список пользователей в количестве {} в соответствии с поставленным запросом.",
                userDtoList.size());
        return userDtoList;
    }

    @Override
    public void delete(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Пользователь " +
                "с id = %s отсутствует в БД. Выполнить операцию невозможно!", id)));
        log.info("Пользователь найден в БД: {}.", user);
        userRepository.delete(user);
        log.info("Все данные пользователя удалены.");
    }

}