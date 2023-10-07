package ru.practicum.mainservice.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.mainservice.category.dto.ResponseCategoryDto;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.event.dto.RequestEventDto;
import ru.practicum.mainservice.event.dto.ResponseEventDto;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.states.State;
import ru.practicum.mainservice.location.dto.ResponseLocationDto;
import ru.practicum.mainservice.location.model.Location;
import ru.practicum.mainservice.user.dto.ShortResponseUserDto;
import ru.practicum.mainservice.user.model.User;
import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {

    public Event toNewEvent(RequestEventDto eventDto, Category category, Location location, User initiator,
                            LocalDateTime currentMoment) {
        return Event.builder()
                .title(eventDto.getTitle())
                .annotation(eventDto.getAnnotation())
                .description(eventDto.getDescription())
                .state(State.PENDING)
                .paid(eventDto.getPaid())
                .category(category)
                .location(location)
                .initiator(initiator)
                .createdOn(currentMoment)
                .eventDate(eventDto.getEventDate())
                .participantLimit(eventDto.getParticipantLimit())
                .requestModeration(eventDto.getRequestModeration())
                .confirmedRequests(0)
                .views(0)
                .build();
    }

    public ResponseEventDto toResponseEventDto(Event event, ResponseCategoryDto categoryDto,
                                               ResponseLocationDto locationDto, ShortResponseUserDto initiatorDto) {
        return ResponseEventDto.builder()
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .state(event.getState())
                .paid(event.getPaid())
                .category(categoryDto)
                .location(locationDto)
                .initiator(initiatorDto)
                .createdOn(event.getCreatedOn())
                .publishedOn(event.getPublishedOn())
                .eventDate(event.getEventDate())
                .participantLimit(event.getParticipantLimit())
                .confirmedRequests(event.getConfirmedRequests())
                .requestModeration(event.getRequestModeration())
                .views(event.getViews())
                .build();
    }

}