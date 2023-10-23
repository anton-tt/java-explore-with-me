package ru.practicum.mainservice.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.comment.dto.RequestCommentDto;
import ru.practicum.mainservice.comment.dto.ResponseCommentDto;
import ru.practicum.mainservice.comment.dto.UpdateRequestCommentDto;
import ru.practicum.mainservice.comment.service.CommentService;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/users/{userId}/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateCommentsController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseCommentDto createComment(@PathVariable @Positive Long userId,
                                            @RequestParam @Positive Long eventId,
                                            @RequestBody @Valid RequestCommentDto commentDto) {
        log.info("");
        log.info("Добавление нового комментария к событию с id = {} пользователем с id = {}: {}", eventId, userId,
                commentDto);
        return commentService.create(userId, eventId, commentDto);
    }

    @PatchMapping("/{commentId}")
    public ResponseCommentDto updateComment(@PathVariable @Positive Long userId,
                                            @PathVariable @Positive Long commentId,
                                            @RequestBody UpdateRequestCommentDto commentDto) {
        log.info("");
        log.info("Обновление пользователем с id = {} комментария с id = {}", userId, commentId);
        return commentService.update(userId, commentId, commentDto);
    }

    @DeleteMapping("{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive Long userId,
                              @PathVariable @Positive Long commentId) {
        log.info("");
        log.info("Удаление пользователем с id = {} всех данных комментария c id = {}", userId, commentId);
        commentService.delete(userId, commentId);
    }

}