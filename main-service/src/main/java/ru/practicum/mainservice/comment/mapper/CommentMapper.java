package ru.practicum.mainservice.comment.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.mainservice.comment.dto.RequestCommentDto;
import ru.practicum.mainservice.comment.dto.ResponseCommentDto;
import ru.practicum.mainservice.comment.dto.UpdateRequestCommentDto;
import ru.practicum.mainservice.comment.model.Comment;
import ru.practicum.mainservice.comment.states.CommentState;
import ru.practicum.mainservice.event.dto.ShortResponseEventDto;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.user.dto.ShortResponseUserDto;
import ru.practicum.mainservice.user.model.User;
import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {

    public Comment toNewComment(RequestCommentDto commentDto, User author, Event event, LocalDateTime currentTime) {
        return Comment.builder()
                .text(commentDto.getText())
                .state(CommentState.PENDING)
                .author(author)
                .event(event)
                .createdOn(currentTime)
                .build();
    }

    public ResponseCommentDto toResponseCommentDto(Comment comment, ShortResponseUserDto authorDto,
                                                   ShortResponseEventDto eventDto) {
        return ResponseCommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(authorDto)
                .event(eventDto)
                .state(comment.getState().toString())
                .createdOn(comment.getCreatedOn())
                .updatedOn(comment.getUpdatedOn() != null ? comment.getUpdatedOn() : null)
                .publishedOn(comment.getPublishedOn() != null ? comment.getPublishedOn() : null)
                .build();
    }

    public Comment toUpdatedComment(Comment initialComment, UpdateRequestCommentDto commentDto,
                                    LocalDateTime currentTime, User author, Event event) {
        return Comment.builder()
                .id(initialComment.getId())
                .text(commentDto.getText())
                .author(author)
                .event(event)
                .state(CommentState.PENDING)
                .createdOn(initialComment.getCreatedOn())
                .updatedOn(currentTime)
                .publishedOn(initialComment.getPublishedOn() != null ? initialComment.getPublishedOn() : null)
                .build();
    }

}