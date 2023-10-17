package ru.practicum.mainservice.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.event.states.EventState;
import ru.practicum.mainservice.exception.DataConflictException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.request.dto.ResponseRequestDto;
import ru.practicum.mainservice.request.mapper.RequestMapper;
import ru.practicum.mainservice.request.model.Request;
import ru.practicum.mainservice.request.repository.RequestRepository;
import ru.practicum.mainservice.request.status.RequestStatus;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    private Request getRequestById(long id) {
        return requestRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Запрос " +
                "с id = %s отсутствует в БД. Выполнить операцию невозможно!", id)));
    }

    private User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Пользователь " +
                "с id = %s отсутствует в БД. Выполнить операцию невозможно!", id)));
    }

    private Event getEventById(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(String.format("Событие " +
                "с id = %s отсутствует в БД. Выполнить операцию невозможно!", eventId)));
    }

    @Override
    public ResponseRequestDto create(Long requesterId, Long eventId) {
        User requester = getUserById(requesterId);
        Event event = getEventById(eventId);

        isNotRepeatUsersRequest(eventId, requesterId);
        isNotInitiatorRequester(event.getInitiator().getId(), requesterId);
        isEventPublished(event.getState());
        isNotExceededLimitRequests(event.getParticipantLimit(), event.getConfirmedRequests());

        RequestStatus requestStatus = generateRequestStatus(event.isRequestModeration());
        if (!event.isRequestModeration() || (event.getParticipantLimit() == 0)) {
            requestStatus = RequestStatus.CONFIRMED;
        }
        LocalDateTime currentMoment = LocalDateTime.now();
        Request requestData = RequestMapper.toRequest(requester, event, currentMoment, requestStatus);
        System.out.println("requestData " + requestData);
        Request request = requestRepository.save(requestData);
        System.out.println("request " + request);

        if (requestStatus.equals(RequestStatus.CONFIRMED)) {
            int confirmedRequests = event.getConfirmedRequests() + 1;
            event.setConfirmedRequests(confirmedRequests);
            eventRepository.save(event);
        }
        log.info("Данные запроса на участие в событии добавлены в БД: {}.", request);

        ResponseRequestDto requestDto = RequestMapper.toResponseRequestDto(request);
        System.out.println("requestDto " + requestDto);
        log.info("Запрос на участие в событии создан: {}.", requestDto);
        return requestDto;
    }

    @Override
    public List<ResponseRequestDto> getAllOneRequester(Long requesterId, Integer from, Integer size) {
        User requester = getUserById(requesterId);
        List<ResponseRequestDto> requestDtoList;
        Pageable pageable = PageRequest.of(from / size, size);
        List<Request> requestsOneRequester = requestRepository.findAllByRequesterId(requesterId, pageable);

        if (requestsOneRequester == null) {
            requestDtoList = new ArrayList<>();
        } else {
            requestDtoList = requestsOneRequester.stream()
                    .map(RequestMapper::toResponseRequestDto)
                    .collect(Collectors.toList());
        }
        log.info("Сформирован список событий пользователя с id = {} в количестве {} в соответствии " +
                "с поставленным запросом.", requesterId, requestDtoList.size());
        return requestDtoList;
    }

    @Override
    public ResponseRequestDto cancelRequest(Long userId, Long requestId) {
        isExistsUser(userId);
        Request request = getRequestById(requestId);
        isRequester(userId, request.getRequester().getId());

        RequestStatus oldStatus = request.getStatus();
        request.setStatus(RequestStatus.CANCELED);
        requestRepository.save(request);

        if (oldStatus.equals(RequestStatus.CONFIRMED)) {
            Event event = getEventById(request.getEvent().getId());
            int confirmedRequests = event.getConfirmedRequests() - 1;
            event.setConfirmedRequests(confirmedRequests);
            eventRepository.save(event);
        }
        log.info("Статус запроса на участие в событии изменён в БД на canceled: {}.", request);

        ResponseRequestDto requestDto = RequestMapper.toResponseRequestDto(request);
        log.info("Запрос на участие в событии отменён пользователем, его создавшим: {}.", requestDto);
        return requestDto;

    }

    private void isNotRepeatUsersRequest(Long eventId, Long requesterId) {
        if (requestRepository.existsByEventIdAndRequesterId(eventId, requesterId)) {
            throw new DataConflictException("Запрос на участие в данном событии ранее уже был отправлен " +
                    "пользователем с таким же id. Выполнить операцию невозможно!");
        }
    }

    private void isNotInitiatorRequester(Long initiatorId, Long requesterId) {
        if (requesterId.equals(initiatorId)) {
            throw new DataConflictException("Пользователь, отправивший запрос на участие в событии, является " +
                    "его инициатором. Выполнить операцию невозможно!");
        }
    }

    private void isEventPublished(EventState state) {
        if (!state.equals(EventState.PUBLISHED)) {
            throw new DataConflictException("Событие, на участие в котором отправлен запрос, пока не опубликовано. " +
                    "Выполнить операцию невозможно!");
        }
    }

    private void isNotExceededLimitRequests(Integer participantLimit, Integer confirmedRequests) {
        if ((participantLimit != 0) && (confirmedRequests >= participantLimit)) {
                throw new DataConflictException("В событии, на участие в котором отправлен запрос, " +
                        "превышен лимит участников. Выполнить операцию невозможно!");
        }
    }

    private RequestStatus generateRequestStatus(Boolean requestModeration) {
        if (requestModeration) {
            return RequestStatus.PENDING;
        } else {
            return RequestStatus.CONFIRMED;
        }
    }

    private void isExistsUser(long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(String.format("Пользователь с id = %s отсутствует в БД. " +
                    "Выполнить операцию невозможно!", id));
        }
    }

    private void isRequester(Long userId, Long requesterId) {
        if (!requesterId.equals(userId)) {
            throw new DataConflictException("Пользователь, отправивший запрос на отмену участия в событии, " +
                    "не создавал этот запрос. Выполнить операцию невозможно!");
        }
    }

}