package ru.practicum.mainservice.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.category.mapper.CategoryMapper;
import ru.practicum.mainservice.comment.dto.RequestCommentDto;
import ru.practicum.mainservice.comment.dto.ResponseCommentDto;
import ru.practicum.mainservice.comment.dto.UpdateRequestCommentDto;
import ru.practicum.mainservice.comment.mapper.CommentMapper;
import ru.practicum.mainservice.comment.model.Comment;
import ru.practicum.mainservice.comment.repository.CommentRepository;
import ru.practicum.mainservice.comment.states.CommentState;
import ru.practicum.mainservice.event.dto.ShortResponseEventDto;
import ru.practicum.mainservice.event.mapper.EventMapper;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.DataConflictException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.user.dto.ShortResponseUserDto;
import ru.practicum.mainservice.user.mapper.UserMapper;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    private User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Пользователь " +
                "с id = %s отсутствует в БД. Выполнить операцию невозможно!", id)));
    }

    private Event getEventById(long id) {
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Событие " +
                "с id = %s отсутствует в БД. Выполнить операцию невозможно!", id)));
    }

    private Comment getCommentById(long id) {
        return commentRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Комментарий " +
                "с id = %s отсутствует в БД. Выполнить операцию невозможно!", id)));
    }

    @Override
    public ResponseCommentDto create(Long userId, Long eventId, RequestCommentDto commentDto) {
        User author = getUserById(userId);
        Event event = getEventById(eventId);
        Comment comment = commentRepository.save(CommentMapper.toNewComment(commentDto, author, event, LocalDateTime.now()));
        log.info("Данные комментария добавлены в БД: {}.", comment);

        ShortResponseEventDto responseEventDto = EventMapper.toShortResponseEventDto(event,
                CategoryMapper.toResponseCategoryDto(event.getCategory()),
                UserMapper.toShortResponseUserDto(event.getInitiator()));

        ShortResponseUserDto responseUserDto = UserMapper.toShortResponseUserDto(author);
        ResponseCommentDto responseCommentDto = CommentMapper.toResponseCommentDto(comment, responseUserDto, responseEventDto);
        log.info("Новый комментарий создан: {}.", responseCommentDto);
        return responseCommentDto;
    }

    @Override
    public ResponseCommentDto updateCommentsStateByAdmin(Long commentId, Boolean isConfirm) {
        Comment initialComment = getCommentById(commentId);
        setUpdateCommentsState(initialComment, isConfirm);
        Comment comment = commentRepository.save(initialComment);
        log.info("Новые данные комментария добавлены в БД: {}.", comment);

        Event event = getEventById(comment.getEvent().getId());
        User author = getUserById(comment.getAuthor().getId());

        ShortResponseEventDto responseEventDto = EventMapper.toShortResponseEventDto(event,
                CategoryMapper.toResponseCategoryDto(event.getCategory()),
                UserMapper.toShortResponseUserDto(event.getInitiator()));

        ShortResponseUserDto responseUserDto = UserMapper.toShortResponseUserDto(author);
        ResponseCommentDto responseCommentDto = CommentMapper.toResponseCommentDto(comment, responseUserDto, responseEventDto);
        log.info("Статус комментария обновлён: {}.", responseCommentDto);
        return responseCommentDto;
    }

    @Override
    public ResponseCommentDto getById(Long commentId) {
        Comment comment = getCommentById(commentId);
        log.info("Данные комментария получены из БД: {}.", comment);

        Event event = getEventById(comment.getEvent().getId());
        User author = getUserById(comment.getAuthor().getId());

        ShortResponseEventDto responseEventDto = EventMapper.toShortResponseEventDto(event,
                CategoryMapper.toResponseCategoryDto(event.getCategory()),
                UserMapper.toShortResponseUserDto(event.getInitiator()));

        ShortResponseUserDto responseUserDto = UserMapper.toShortResponseUserDto(author);
        ResponseCommentDto responseCommentDto = CommentMapper.toResponseCommentDto(comment, responseUserDto, responseEventDto);
        log.info("Статус комментария обновлён: {}.", responseCommentDto);
        return responseCommentDto;
    }

    @Override
    public List<ResponseCommentDto> getComments(Long eventId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Comment> comments = commentRepository.findAllByEventId(eventId, pageable);

        if (comments.isEmpty()) {
            log.info("По заданным условиям комментарии отсутствуют.");
            return new ArrayList<>();
        } else {
            List<ResponseCommentDto> resultList = comments.stream()
                .map((Comment comment) -> {
                    Event event = getEventById(comment.getEvent().getId());
                    User author = getUserById(comment.getAuthor().getId());

                    ShortResponseEventDto responseEventDto = EventMapper.toShortResponseEventDto(event,
                            CategoryMapper.toResponseCategoryDto(event.getCategory()),
                            UserMapper.toShortResponseUserDto(event.getInitiator()));

                    ShortResponseUserDto responseUserDto = UserMapper.toShortResponseUserDto(author);
                    ResponseCommentDto responseCommentDto = CommentMapper.toResponseCommentDto(comment,
                            responseUserDto, responseEventDto);
                    return responseCommentDto;
                    })
                .collect(toList());
            log.info("По заданным условиям сформирован список комментариев к событию.");
            return resultList;
        }
    }

    @Override
    public ResponseCommentDto update(Long userId, Long commentId, UpdateRequestCommentDto commentDto) {
        User user = getUserById(userId);
        Comment initialComment = getCommentById(commentId);
        isUserEqualsAuthor(initialComment.getAuthor().getId(), userId);
        isNotConfirmedComment(initialComment.getState());
        Event event = getEventById(initialComment.getEvent().getId());

        Comment comment = commentRepository.save(CommentMapper.toUpdatedComment(initialComment, commentDto,
                LocalDateTime.now(), user, event));
        ShortResponseUserDto responseUserDto = UserMapper.toShortResponseUserDto(user);
        ShortResponseEventDto responseEventDto = EventMapper.toShortResponseEventDto(event,
                CategoryMapper.toResponseCategoryDto(event.getCategory()),
                UserMapper.toShortResponseUserDto(event.getInitiator()));
        ResponseCommentDto responseCommentDto = CommentMapper.toResponseCommentDto(comment, responseUserDto, responseEventDto);
        log.info("Комментарий обновлён по запросу пользователя: {}.", responseCommentDto);
        return responseCommentDto;
    }

    @Override
    public void delete(Long userId, Long commentId) {
        isExistsUser(userId);
        Comment comment = getCommentById(commentId);
        log.info("Комментарий найден в БД: {}.", comment);
        commentRepository.deleteById(commentId);
        log.info("Все данные комментария удалены.");
    }

    private void isExistsUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id = %s отсутствует в БД. " +
                    "Выполнить операцию невозможно!", userId));
        }
    }

    private void isUserEqualsAuthor(long authorId, long userId) {
        if (authorId != userId) {
            throw new DataConflictException("Пользователь, запросивший обновление комментария, не является его автором. " +
                    "Выполнить операцию невозможно!");
        }
    }

    private void isNotConfirmedComment(CommentState state) {
        if (state == CommentState.CONFIRMED) {
            throw new DataConflictException("Комментарий, который хочет обновить пользователь, имеет состояние CONFIRMED. " +
                    "Выполнить операцию невозможно!");
        }
    }

    private void setUpdateCommentsState(Comment initialComment, Boolean isConfirm) {
        if (isConfirm != null && isConfirm) {
            initialComment.setState(CommentState.CONFIRMED);
            initialComment.setPublishedOn(LocalDateTime.now());
        } else {
            initialComment.setState(CommentState.REJECTED);
        }
    }

}