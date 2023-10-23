package ru.practicum.mainservice.comment.service;

import ru.practicum.mainservice.comment.dto.RequestCommentDto;
import ru.practicum.mainservice.comment.dto.ResponseCommentDto;
import ru.practicum.mainservice.comment.dto.UpdateRequestCommentDto;
import java.util.List;

public interface CommentService {

    ResponseCommentDto create(Long userId, Long eventId, RequestCommentDto commentDto);

    ResponseCommentDto getById(Long commentId);

    List<ResponseCommentDto> getComments(Long eventId, Integer from, Integer size);

    ResponseCommentDto updateCommentsStateByAdmin(Long commentId, Boolean isConfirm);

    ResponseCommentDto update(Long userId, Long commentId, UpdateRequestCommentDto commentDto);

    void delete(Long userId, Long commentId);

}