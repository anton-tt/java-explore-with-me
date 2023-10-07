package ru.practicum.mainservice.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.category.dto.ResponseCategoryDto;
import ru.practicum.mainservice.category.mapper.CategoryMapper;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.repository.CategoryRepository;
import ru.practicum.mainservice.event.dto.RequestEventDto;
import ru.practicum.mainservice.event.dto.ResponseEventDto;
import ru.practicum.mainservice.event.mapper.EventMapper;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.exception.ValidationException;
import ru.practicum.mainservice.location.dto.ResponseLocationDto;
import ru.practicum.mainservice.location.mapper.LocationMapper;
import ru.practicum.mainservice.location.model.Location;
import ru.practicum.mainservice.location.repository.LocationRepository;
import ru.practicum.mainservice.user.dto.ShortResponseUserDto;
import ru.practicum.mainservice.user.mapper.UserMapper;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

    private Event getEventById(long id) {
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Событие " +
                "с id = %s отсутствует в БД. Выполнить операцию невозможно!", id)));
    }

    private User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Пользователь " +
                "с id = %s отсутствует в БД. Выполнить операцию невозможно!", id)));
    }

    private Category getCategoryById(long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Пользователь " +
                "с id = %s отсутствует в БД. Выполнить операцию невозможно!", id)));
    }

    @Override
    public ResponseEventDto create(Long userId, RequestEventDto eventDto) {
        User initiator = getUserById(userId);
        Category category = getCategoryById(eventDto.getCategoryId());

        Location locationData = LocationMapper.toLocation(eventDto.getLocation());
        Location location = locationRepository.save(locationData);

        LocalDateTime currentMoment = LocalDateTime.now();
        LocalDateTime eventDate = eventDto.getEventDate();
        isStartNotBeforeTwoHours(currentMoment, eventDate);

        Event eventData = EventMapper.toNewEvent(eventDto, category, location, initiator, currentMoment);
        Event event = eventRepository.save(eventData);
        log.info("Данные события добавлены в БД: {}.", event);

        ResponseCategoryDto categoryDto = CategoryMapper.toResponseCategoryDto(category);
        ResponseLocationDto locationDto = LocationMapper.toResponseLocationDto(location);
        ShortResponseUserDto initiatorDto = UserMapper.toShortResponseUserDto(initiator);

        ResponseEventDto responseEventDto = EventMapper.toResponseEventDto(event, categoryDto, locationDto, initiatorDto);
        log.info("Новое событие создано: {}.", responseEventDto);
        return responseEventDto;
    }

    /*@Override
    public ResponseEventDto update(Long userId, RequestEventDto eventDto) {*/

    private void isStartNotBeforeTwoHours(LocalDateTime currentMoment, LocalDateTime eventDate) {
        if (eventDate.isBefore(currentMoment.plusHours(2))) {
            throw new ValidationException("До начала события остаётся менее двух часов. Выполнить операцию невозможно!");
        }
    }

}