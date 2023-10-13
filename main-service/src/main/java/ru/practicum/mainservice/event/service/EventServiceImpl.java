package ru.practicum.mainservice.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.mainservice.category.dto.ResponseCategoryDto;
import ru.practicum.mainservice.category.mapper.CategoryMapper;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.repository.CategoryRepository;
import ru.practicum.mainservice.event.dto.RequestEventDto;
import ru.practicum.mainservice.event.dto.ResponseEventDto;
import ru.practicum.mainservice.event.dto.ShortResponseEventDto;
import ru.practicum.mainservice.event.dto.UpdateRequestEventDto;
import ru.practicum.mainservice.event.mapper.EventMapper;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.event.requestListDto.InitialRequestListDto;
import ru.practicum.mainservice.event.requestListDto.ResultRequestListDto;
import ru.practicum.mainservice.event.states.EventState;
import ru.practicum.mainservice.event.states.StateAction;
import ru.practicum.mainservice.exception.DataConflictException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.exception.ValidationException;
import ru.practicum.mainservice.location.dto.RequestLocationDto;
import ru.practicum.mainservice.location.dto.ResponseLocationDto;
import ru.practicum.mainservice.location.mapper.LocationMapper;
import ru.practicum.mainservice.location.model.Location;
import ru.practicum.mainservice.location.repository.LocationRepository;
import ru.practicum.mainservice.request.dto.ResponseRequestDto;
import ru.practicum.mainservice.request.mapper.RequestMapper;
import ru.practicum.mainservice.request.model.Request;
import ru.practicum.mainservice.request.repository.RequestRepository;
import ru.practicum.mainservice.request.status.RequestStatus;
import ru.practicum.mainservice.user.dto.ShortResponseUserDto;
import ru.practicum.mainservice.user.mapper.UserMapper;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final StatClient statClient;

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

    /*private Request getRequestById(long id) {
        return requestRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Запрос " +
                "с id = %s отсутствует в БД. Выполнить операцию невозможно!", id)));
    }*/

    @Override
    public ResponseEventDto create(Long userId, RequestEventDto eventDto) {
        User initiator = getUserById(userId);
        Category category = getCategoryById(eventDto.getCategoryId());

        Location locationData = LocationMapper.toLocation(eventDto.getLocation());
        Location location = locationRepository.save(locationData);

        LocalDateTime currentMoment = LocalDateTime.now();
        LocalDateTime eventDate = eventDto.getEventDate();
        isStartNotBeforeNHours(currentMoment, eventDate, 2);

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

    @Override
    public ResponseEventDto getById(Long id) {
        Event event = getEventById(id);
        log.info("Данные события получены из БД: {}.", event);

        addViews(List.of(event));

        ResponseCategoryDto categoryDto = CategoryMapper.toResponseCategoryDto(event.getCategory());
        ResponseLocationDto locationDto = LocationMapper.toResponseLocationDto(event.getLocation());
        ShortResponseUserDto initiatorDto = UserMapper.toShortResponseUserDto(event.getInitiator());

        ResponseEventDto responseEventDto = EventMapper.toResponseEventDto(event, categoryDto, locationDto, initiatorDto);
        log.info("Новое событие создано: {}.", responseEventDto);
        return responseEventDto;
    }

    @Override
    public ResponseEventDto getByIdByInitiator(Long userId, Long id) {
        User initiator = getUserById(userId);
        Event event = getEventById(id);
        isInitiatorUser(event.getInitiator().getId(), userId);
        log.info("Данные события получены из БД: {}.", event);

        addViews(List.of(event));

        ResponseCategoryDto categoryDto = CategoryMapper.toResponseCategoryDto(event.getCategory());
        ResponseLocationDto locationDto = LocationMapper.toResponseLocationDto(event.getLocation());
        ShortResponseUserDto initiatorDto = UserMapper.toShortResponseUserDto(initiator);

        ResponseEventDto responseEventDto = EventMapper.toResponseEventDto(event, categoryDto, locationDto, initiatorDto);
        log.info("Данные события получены: {}.", responseEventDto);
        return responseEventDto;
    }

    @Override
    public List<ShortResponseEventDto> getEventsOneInitiator(Long userId, Integer from, Integer size) {
        isExistsUser(userId);
        List<ShortResponseEventDto> eventDtoList;
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> eventsOneInitiator = eventRepository.findAllByInitiatorId(userId, pageable);

        if (eventsOneInitiator == null) {
            eventDtoList = new ArrayList<>();
        } else {
            addViews(eventsOneInitiator);
            eventDtoList = eventsOneInitiator.stream()
                    .map(event -> EventMapper.toShortResponseEventDto(event,
                            CategoryMapper.toResponseCategoryDto(event.getCategory()),
                            UserMapper.toShortResponseUserDto(event.getInitiator())))
                    .collect(Collectors.toList());
        }
        log.info("Сформирован список событий пользователя с id = {} в количестве {} в соответствии " +
                "с поставленным запросом.", userId, eventDtoList.size());
        return eventDtoList;
    }

    @Override
    public List<ResponseEventDto> getFullEvents(List<User> users, List<String> states, List<Long> categories,
    LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        isStartBeforeEnd(rangeStart, rangeEnd);
        Specification<Event> specification = Specification.where(null);
        if (users != null && !users.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("initiator").get("id").in(users));
        }

        if (states != null && !states.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("state").as(String.class).in(states));
        }

        if (categories != null && !categories.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }

        if (rangeStart != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                            criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }

        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAll(specification, pageable);
        List<ResponseEventDto> eventDtoList;

        if (events == null) {
            eventDtoList = new ArrayList<>();
        } else {
            addViews(events);
            eventDtoList = events.stream()
                    .map(event -> EventMapper.toResponseEventDto(event,
                            CategoryMapper.toResponseCategoryDto(event.getCategory()),
                            LocationMapper.toResponseLocationDto(event.getLocation()),
                            UserMapper.toShortResponseUserDto(event.getInitiator())))
                    .collect(Collectors.toList());
        }
        log.info("Сформирован список событий в соответствии с поставленным запросом.");
        return eventDtoList;
    }

    @Override
    public List<ShortResponseEventDto> getPublishedEvents(String text, List<Long> categories, Boolean paid,
                                                          LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                          Boolean onlyAvailable, String sort, Integer from,
                                                          Integer size, HttpServletRequest requestData) {
        isStartBeforeEnd(rangeStart, rangeEnd);
        saveHitStatistic(requestData);

        Specification<Event> specification = Specification.where(null);
        if (text != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")),
                                    "%" + text.toLowerCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                                    "%" + text.toLowerCase() + "%")
                    ));

            if (categories != null && !categories.isEmpty()) {
                specification = specification.and((root, query, criteriaBuilder) ->
                        root.get("category").get("id").in(categories));
            }

            if (rangeStart == null) {
                rangeStart = LocalDateTime.now();
            }
            LocalDateTime finalRangeStart = rangeStart;
            specification = specification.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), finalRangeStart));
            }

        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        if (onlyAvailable != null && onlyAvailable) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0));
        }

        specification = specification.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("state"), EventState.PUBLISHED));

        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAll(specification, pageable);
        List<ShortResponseEventDto> eventDtoList;

        if (events == null) {
            eventDtoList = new ArrayList<>();
        } else {
            addViews(events);
            eventDtoList = events.stream()
                    .map(event -> EventMapper.toShortResponseEventDto(event,
                            CategoryMapper.toResponseCategoryDto(event.getCategory()),
                            UserMapper.toShortResponseUserDto(event.getInitiator())))
                    .collect(Collectors.toList());
        }
        log.info("Сформирован список событий в соответствии с поставленным запросом.");
        return eventDtoList;
    }

    @Override
    public ResponseEventDto privateUpdate(Long userId, Long id, UpdateRequestEventDto eventDto) {
        User initiator = getUserById(userId);
        Event initialEvent = getEventById(id);

        isInitiatorUser(initialEvent.getInitiator().getId(), userId);
        isStateNotPublished(initialEvent.getState());

        privateUpdateInitialEvent(initialEvent, eventDto, initiator);

        Event event = eventRepository.save(initialEvent);
        log.info("Данные события обновлены в БД: {}.", event);

        ResponseCategoryDto categoryDto = CategoryMapper.toResponseCategoryDto(event.getCategory());
        ResponseLocationDto locationDto = LocationMapper.toResponseLocationDto(event.getLocation());
        ShortResponseUserDto initiatorDto = UserMapper.toShortResponseUserDto(initiator);

        ResponseEventDto responseEventDto = EventMapper.toResponseEventDto(event, categoryDto, locationDto, initiatorDto);
        log.info("Событие обновлено: {}.", responseEventDto);
        return responseEventDto;
    }

    @Override
    public ResponseEventDto adminUpdate(Long id, UpdateRequestEventDto eventDto) {
        Event initialEvent = getEventById(id);
        adminUpdateInitialEvent(initialEvent, eventDto);

        Event event = eventRepository.save(initialEvent);
        log.info("Данные события обновлены в БД: {}.", event);

        ResponseCategoryDto categoryDto = CategoryMapper.toResponseCategoryDto(event.getCategory());
        ResponseLocationDto locationDto = LocationMapper.toResponseLocationDto(event.getLocation());
        ShortResponseUserDto initiatorDto = UserMapper.toShortResponseUserDto(event.getInitiator());

        ResponseEventDto responseEventDto = EventMapper.toResponseEventDto(event, categoryDto, locationDto, initiatorDto);
        log.info("Событие обновлено: {}.", responseEventDto);
        return responseEventDto;
    }

    @Override
    public ResultRequestListDto updateRequestsStatus(Long userId, Long eventId, InitialRequestListDto initialRequestListDto) {
        isExistsUser(userId);
        Event event = getEventById(eventId);
        Integer eventsParticipantLimit = event.getParticipantLimit();
        Integer confirmedRequests = event.getConfirmedRequests();
        isInitiatorUser(event.getInitiator().getId(), userId);
        isNotExceededLimitRequests(eventsParticipantLimit, confirmedRequests);
        isEventRequireConfirmation(eventsParticipantLimit, event.getRequestModeration());
        RequestStatus requestStatus = initialRequestListDto.getStatus();

        List<Request> requests = requestRepository.findAllByIdInAndStatusPendingOrderByCreatedAsc(
                initialRequestListDto.getRequestIds());
        List<ResponseRequestDto> confirmedRequestsList = new ArrayList<>();
        List<ResponseRequestDto> rejectedRequestsList = new ArrayList<>();

        switch (requestStatus) {
            case CONFIRMED:
                for (Request request : requests) {
                    if (confirmedRequests < eventsParticipantLimit) {
                        updateConfirmedRequestsStatus(request, userId, confirmedRequestsList);
                        confirmedRequests = confirmedRequests + 1;
                    } else {
                        updateRejectedRequestsStatus(request, userId, rejectedRequestsList);
                    }
                }
                event.setConfirmedRequests(confirmedRequests);
                eventRepository.save(event);
                break;

            case REJECTED:
                for (Request request : requests) {
                    updateRejectedRequestsStatus(request, userId, rejectedRequestsList);
                }
                break;

            default:
                throw new ValidationException(String.format("Unknown requestStatus: %s", requestStatus));
        }
        log.info("Статусы запросов пользователей на участие в событии обновлены.");
        return ResultRequestListDto.builder()
                .confirmedRequests(confirmedRequestsList)
                .rejectedRequests(rejectedRequestsList)
                .build();
    }

    @Override
    public List<ResponseRequestDto> getAllRequestOneEvent(Long userId, Long eventId) {
        isExistsUser(userId);
        getEventById(eventId);
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        List<ResponseRequestDto> requestDtoList = new ArrayList<>();
        if (requests.isEmpty()) {
            return requestDtoList;
        }
        log.info("Из БД получен список запросов на участие в событии с id = {}.", eventId);
        requestDtoList = requests.stream()
                .map(RequestMapper::toResponseRequestDto)
                .collect(Collectors.toList());
        log.info("Сформирован итоговый список запросов в количестве {}.", requestDtoList.size());
        return requestDtoList;
    }

    private void updateConfirmedRequestsStatus(Request request, Long userId,
                                               List<ResponseRequestDto> confirmedRequestsList) {
        request.setStatus(RequestStatus.CONFIRMED);
        requestRepository.save(request);
        confirmedRequestsList.add(RequestMapper.toResponseRequestDto(request));
    }

    private void updateRejectedRequestsStatus(Request request, Long userId,
                                              List<ResponseRequestDto> rejectedRequestsList) {
        request.setStatus(RequestStatus.REJECTED);
        requestRepository.save(request);
        rejectedRequestsList.add(RequestMapper.toResponseRequestDto(request));
    }

    private void isExistsUser(long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(String.format("Пользователь с id = %s отсутствует в БД. " +
                    "Выполнить операцию невозможно!", id));
        }
    }

    private void isStartNotBeforeNHours(LocalDateTime currentMoment, LocalDateTime eventDate, Integer hours) {
        if (eventDate.isBefore(currentMoment.plusHours(hours))) {
            throw new DataConflictException("До начала события остаётся слишком мало времени. " +
                    "Выполнить операцию невозможно!");
        }
    }

    private void isInitiatorUser(Long initiatorId, Long userId) {
        if (!initiatorId.equals(userId)) {
            throw new DataConflictException("Пользователь, запросивший обновление события, не является его инициатором. " +
                    "Выполнить операцию невозможно!");
        }
    }

    private void isStateNotPublished(EventState state) {
        if (state.equals(EventState.PUBLISHED)) {
            throw new DataConflictException("Событие имеет статус PUBLISHED. Выполнить операцию невозможно!");
        }
    }

    private void isEventRequireConfirmation(Integer participantLimit, boolean requestModeration) {
        if ((participantLimit == 0) || (!requestModeration)) {
            throw new DataConflictException("Заявкам пользователей на участие в событии не требуется подтверждение, " +
                    "т.к. на данное событие заявки подтверждаются уже при их создании. Выполнить операцию невозможно!");
        }
    }

    private void isNotExceededLimitRequests(Integer participantLimit, Integer confirmedRequests) {
        if ((participantLimit != 0) && (confirmedRequests >= participantLimit)) {
            throw new DataConflictException("В событии, на участие в котором отправлен запрос, " +
                    "превышен лимит участников. Выполнить операцию невозможно!");
        }
    }

    private void isStartBeforeEnd(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new DataConflictException("Некорректно задан временной интервал, в котором " +
                    "будет производиться поиск событий. Выполнить операцию невозможно!");
        }
    }

    private void privateUpdateInitialEvent(Event initialEvent, UpdateRequestEventDto eventDto, User initiator) {

        LocalDateTime eventDate = eventDto.getEventDate();
        if (eventDate != null) {
            isStartNotBeforeNHours(LocalDateTime.now(), eventDate, 2);
            initialEvent.setEventDate(eventDate);
        }

        String title = eventDto.getTitle();
        if (title != null) {
            initialEvent.setTitle(title);
        }

        String annotation = eventDto.getAnnotation();
        if (!annotation.isBlank()) {
            initialEvent.setAnnotation(annotation);
        }

        String description = eventDto.getDescription();
        if (description != null) {
            initialEvent.setDescription(description);
        }

        StateAction stateAction = eventDto.getStateAction();
        if (stateAction != null) {
            switch (stateAction) {
                case SEND_TO_REVIEW:
                    initialEvent.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    initialEvent.setState(EventState.CANCELED);
                    break;
                default: throw new ValidationException(String.format("Unknown stateAction: %s", stateAction));
            }
        }

        Long categoryId = eventDto.getCategoryId();
        if (categoryId != null) {
            initialEvent.setCategory(getCategoryById(categoryId));
        }

        RequestLocationDto locationDto = eventDto.getLocation();
        if (locationDto != null) {
            initialEvent.setLocation(LocationMapper.toLocation(locationDto));
        }

        initialEvent.setInitiator(initiator);

        Boolean paid = eventDto.getPaid();
        if (paid != null) {
            initialEvent.setPaid(paid);
        }

        Integer participantLimit = eventDto.getParticipantLimit();
        if (participantLimit != null) {
            initialEvent.setParticipantLimit(participantLimit);
        }

        Boolean requestModeration = eventDto.getRequestModeration();
        if (requestModeration != null) {
            initialEvent.setRequestModeration(requestModeration);
        }
    }

    private void adminUpdateInitialEvent(Event initialEvent, UpdateRequestEventDto eventDto) {
        LocalDateTime eventDate = eventDto.getEventDate();
        if (eventDate != null) {
            isStartNotBeforeNHours(LocalDateTime.now(), eventDate, 1);
            initialEvent.setEventDate(eventDate);
        }

        String title = eventDto.getTitle();
        if (title != null) {
            initialEvent.setTitle(title);
        }

        String annotation = eventDto.getAnnotation();
        if (!annotation.isBlank()) {
            initialEvent.setAnnotation(annotation);
        }

        String description = eventDto.getDescription();
        if (description != null) {
            initialEvent.setDescription(description);
        }

        StateAction stateAction = eventDto.getStateAction();
        if (stateAction != null) {
            switch (stateAction) {
                case PUBLISH_EVENT:
                    initialEvent.setState(EventState.PUBLISHED);
                    initialEvent.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    initialEvent.setState(EventState.CANCELED);
                    break;
                default: throw new ValidationException(String.format("Unknown stateAction: %s", stateAction));
            }
        }

        Long categoryId = eventDto.getCategoryId();
        if (categoryId != null) {
            initialEvent.setCategory(getCategoryById(categoryId));
        }

        RequestLocationDto locationDto = eventDto.getLocation();
        if (locationDto != null) {
            initialEvent.setLocation(LocationMapper.toLocation(locationDto));
        }

        Boolean paid = eventDto.getPaid();
        if (paid != null) {
            initialEvent.setPaid(paid);
        }

        Integer participantLimit = eventDto.getParticipantLimit();
        if (participantLimit != null) {
            initialEvent.setParticipantLimit(participantLimit);
        }

        Boolean requestModeration = eventDto.getRequestModeration();
        if (requestModeration != null) {
            initialEvent.setRequestModeration(requestModeration);
        }

    }

    private void saveHitStatistic(HttpServletRequest request) {
        String app = "main-service";
        statClient.saveHit(EndpointHitDto.builder()
                .app(app)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build());
    }

    private void addViews(List<Event> events) {
        events.forEach(event -> event.setViews(+1));
        eventRepository.saveAll(events);
    }

}