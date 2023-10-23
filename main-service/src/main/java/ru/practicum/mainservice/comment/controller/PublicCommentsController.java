package ru.practicum.mainservice.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.comment.dto.ResponseCommentDto;
import ru.practicum.mainservice.comment.service.CommentService;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicCommentsController {

    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public ResponseCommentDto getCommentById(@PathVariable Long commentId) {
        log.info("");
        log.info("Получение данных комментария с id = {}", commentId);
        return commentService.getById(commentId);
    }

    @GetMapping
    public List<ResponseCommentDto> getComments(@RequestParam @Positive Long eventId,
                                                @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("");
        log.info("Поиск комментариев, оставленных пользователем, к событию с id = {} в соответствии с запросом", eventId);
        return commentService.getComments(eventId, from, size);
    }

}