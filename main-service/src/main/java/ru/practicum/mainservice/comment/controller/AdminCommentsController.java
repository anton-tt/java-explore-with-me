package ru.practicum.mainservice.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.comment.dto.ResponseCommentDto;
import ru.practicum.mainservice.comment.service.CommentService;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/admin/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminCommentsController {

    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public ResponseCommentDto updateCommentsStateByAdmin(@PathVariable @Positive Long commentId,
                                                         @RequestParam Boolean isConfirm) {
        log.info("");
        log.info("Обновление в режиме admin статуса комментария с id = {}", commentId);
        return commentService.updateCommentsStateByAdmin(commentId, isConfirm);
    }

}