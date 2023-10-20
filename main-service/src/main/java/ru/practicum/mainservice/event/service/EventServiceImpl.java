package ru.practicum.mainservice.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.clie.StatClient;
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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
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
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Категория " +
                "с id = %s отсутствует в БД. Выполнить операцию невозможно!", id)));
    }

    @Override
    public ResponseEventDto create(Long userId, RequestEventDto eventDto) {
        User initiator = getUserById(userId);
        ShortResponseUserDto initiatorDto = UserMapper.toShortResponseUserDto(initiator);

        Category category = getCategoryById(eventDto.getCategory());
        ResponseCategoryDto categoryDto = CategoryMapper.toResponseCategoryDto(category);

        Location locationData = LocationMapper.toLocation(eventDto.getLocation());
        Location location = locationRepository.save(locationData);
        ResponseLocationDto locationDto = LocationMapper.toResponseLocationDto(location);

        LocalDateTime currentMoment = LocalDateTime.now();
        LocalDateTime eventDate = eventDto.getEventDate();
        isStartNotBeforeNHours(currentMoment, eventDate, 2);

        Event eventData = EventMapper.toNewEvent(eventDto, category, location, initiator, currentMoment);
        Event event = eventRepository.save(eventData);
        log.info("Данные события добавлены в БД: {}.", event);

        ResponseEventDto responseEventDto = EventMapper.toResponseEventDto(event, categoryDto, locationDto, initiatorDto);
        log.info("Новое событие создано: {}.", responseEventDto);
        return responseEventDto;
    }

    @Override
    public ResponseEventDto getById(Long id, HttpServletRequest requestData) {
        Event event = getEventById(id);
        isStatePublished(event.getState());
        log.info("Данные события получены из БД: {}.", event);
        addViews(List.of(event));

        ResponseCategoryDto categoryDto = CategoryMapper.toResponseCategoryDto(event.getCategory());
        ResponseLocationDto locationDto = LocationMapper.toResponseLocationDto(event.getLocation());
        ShortResponseUserDto initiatorDto = UserMapper.toShortResponseUserDto(event.getInitiator());

        ResponseEventDto responseEventDto = EventMapper.toResponseEventDto(event, categoryDto, locationDto, initiatorDto);
        log.info("Событие получено: {}.", responseEventDto);
        saveHitStatistic(requestData);
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
                    .map((Event event) -> EventMapper.toShortResponseEventDto(event,
                            CategoryMapper.toResponseCategoryDto(event.getCategory()),
                            UserMapper.toShortResponseUserDto(event.getInitiator())))
                    .collect(Collectors.toList());
        }
        log.info("Сформирован список событий пользователя с id = {} в количестве {} в соответствии " +
                "с поставленным запросом.", userId, eventDtoList.size());
        return eventDtoList;
    }

    @Override
    public List<ResponseEventDto> getFullEvents(List<Long> users, List<String> states, List<Long> categories,
            LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        isStartBeforeEnd(rangeStart, rangeEnd);
        Specification<Event> specification = Specification.where(null);
        if (users != null && !users.isEmpty()) {
            specification = specification.and((Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                    root.get("initiator").get("id").in(users));
        }

        if (states != null && !states.isEmpty()) {
            specification = specification.and((Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                    root.get("state").as(String.class).in(states));
        }

        if (categories != null && !categories.isEmpty()) {
            specification = specification.and((Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }

        if (rangeStart != null) {
            specification = specification.and((Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                            criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }

        if (rangeEnd != null) {
            specification = specification.and((Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        List<ResponseEventDto> eventDtoList;
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAll(specification, pageable);

        if (events == null) {
            eventDtoList = new ArrayList<>();
        } else {
            addViews(events);
            eventDtoList = events.stream()
                    .map((Event event) -> EventMapper.toResponseEventDto(event,
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
        Specification<Event> specification = Specification.where(null);

        if (text != null) {
            specification = specification.and((Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")),
                                    "%" + text.toLowerCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                                    "%" + text.toLowerCase() + "%")
                    ));

        if (categories != null && !categories.isEmpty()) {
            specification = specification.and((Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        LocalDateTime finalRangeStart = rangeStart;
        specification = specification.and((Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), finalRangeStart));
        }

        if (rangeEnd != null) {
            specification = specification.and((Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        if (onlyAvailable != null && onlyAvailable) {
            specification = specification.and((Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0));
        }

        specification = specification.and((Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("state"), EventState.PUBLISHED));

        List<ShortResponseEventDto> eventDtoList;
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAll(specification, pageable);

        if (events == null) {
            eventDtoList = new ArrayList<>();
        } else {
            addViews(events);
            eventDtoList = events.stream()
                    .map((Event event) -> EventMapper.toShortResponseEventDto(event,
                            CategoryMapper.toResponseCategoryDto(event.getCategory()),
                            UserMapper.toShortResponseUserDto(event.getInitiator())))
                    .collect(Collectors.toList());
        }
        log.info("Сформирован список событий в соответствии с поставленным запросом в количестве {}.",
                eventDtoList.size());
        saveHitStatistic(requestData);
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
        isStatePending(initialEvent.getState());
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
        isEventRequireConfirmation(eventsParticipantLimit, event.isRequestModeration());
        RequestStatus requestStatus = initialRequestListDto.getStatus();

        List<Request> requests = requestRepository.findAllByIdInAndStatusOrderByCreatedAsc(
        initialRequestListDto.getRequestIds(), RequestStatus.PENDING);
        List<ResponseRequestDto> confirmedRequestsList = new ArrayList<>();
        List<ResponseRequestDto> rejectedRequestsList = new ArrayList<>();

        switch (requestStatus) {
            case CONFIRMED:
                for (Request request : requests) {
                    if (confirmedRequests < eventsParticipantLimit) {
                        updateConfirmedRequestsStatus(request, confirmedRequestsList);
                        confirmedRequests = confirmedRequests + 1;
                    } else {
                        updateRejectedRequestsStatus(request, rejectedRequestsList);
                    }
                }
                event.setConfirmedRequests(confirmedRequests);
                eventRepository.save(event);
                break;

            case REJECTED:
                for (Request request : requests) {
                    updateRejectedRequestsStatus(request, rejectedRequestsList);
                }
                break;

            default:
                throw new ValidationException(String.format("Unknown requestStatus: %s", requestStatus));
        }

        ResultRequestListDto resultRequestListDto = ResultRequestListDto.builder()
                .confirmedRequests(confirmedRequestsList)
                .rejectedRequests(rejectedRequestsList)
                .build();
        log.info("Статусы запросов пользователей на участие в событии обновлены: {}.", resultRequestListDto);
        return resultRequestListDto;
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

    private void updateConfirmedRequestsStatus(Request request, List<ResponseRequestDto> confirmedRequestsList) {
        request.setStatus(RequestStatus.CONFIRMED);
        requestRepository.save(request);
        confirmedRequestsList.add(RequestMapper.toResponseRequestDto(request));
    }

    private void updateRejectedRequestsStatus(Request request, List<ResponseRequestDto> rejectedRequestsList) {
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

    private void isStatePublished(EventState state) {
        if (!state.equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Запрашиваемое событие не имеет статус PUBLISHED и не является общедоступным. " +
                    "Выполнить операцию невозможно!");
        }
    }

    private void isStateNotPublished(EventState state) {
        if (state.equals(EventState.PUBLISHED)) {
            throw new DataConflictException("Событие имеет статус PUBLISHED. Выполнить операцию невозможно!");
        }
    }

    private void isStatePending(EventState state) {
        if (!state.equals(EventState.PENDING)) {
            throw new DataConflictException("Событие не имеет статус PENDING. Выполнить операцию невозможно!");
        }
    }

    private void isEventRequireConfirmation(Integer participantLimit, boolean requestModeration) {
        if ((participantLimit == 0) || (!requestModeration)) {
            throw new DataConflictException("Заявкам пользователей на участие в событии не требуется подтверждение, " +
                    "т.к. на данное событие заявки подтверждаются ещё при их создании. Выполнить операцию невозможно!");
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
            throw new ValidationException("Некорректно задан временной интервал, в котором " +
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
        if (title != null && !title.isBlank()) {
            initialEvent.setTitle(title);
        }

        String annotation = eventDto.getAnnotation();
        if (annotation != null && !annotation.isBlank()) {
            initialEvent.setAnnotation(annotation);
        }

        String description = eventDto.getDescription();
        if (description != null && !description.isBlank()) {
            initialEvent.setDescription(description);
        }

        StateAction stateAction = eventDto.getStateAction();
        if (stateAction != null) {
            setStateEventPrivate(stateAction, initialEvent);
        }

        Long categoryId = eventDto.getCategory();
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
        if (title != null && !title.isBlank()) {
            initialEvent.setTitle(title);
        }

        String annotation = eventDto.getAnnotation();
        if (annotation != null && !annotation.isBlank()) {
            initialEvent.setAnnotation(annotation);
        }

        String description = eventDto.getDescription();
        if (description != null && !description.isBlank()) {
            initialEvent.setDescription(description);
        }

        StateAction stateAction = eventDto.getStateAction();
        if (stateAction != null) {
            setStateEventAdmin(stateAction, initialEvent);
        }

        Long categoryId = eventDto.getCategory();
        if (categoryId != null) {
            initialEvent.setCategory(getCategoryById(categoryId));
        }

        RequestLocationDto locationDto = eventDto.getLocation();
        if (locationDto != null) {
            setEventsLocationAdmin(locationDto, initialEvent);
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

    private void setStateEventPrivate(StateAction stateAction, Event initialEvent) {
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

    private void setStateEventAdmin(StateAction stateAction, Event initialEvent) {
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

    private void setEventsLocationAdmin(RequestLocationDto locationDto, Event initialEvent) {
        Location  locationData = LocationMapper.toLocation(locationDto);
        Location  location = locationRepository.save(locationData);
        initialEvent.setLocation(location);
    }

    private void saveHitStatistic(HttpServletRequest request) {
        String app = "main-service";
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app(app)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
        statClient.saveHit(endpointHitDto);
    }

    private void addViews(List<Event> events) {
        events.forEach(event -> event.setViews(+1));
        eventRepository.saveAll(events);
    }
}