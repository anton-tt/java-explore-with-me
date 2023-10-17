package ru.practicum.mainservice.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.category.mapper.CategoryMapper;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.repository.CategoryRepository;
import ru.practicum.mainservice.compilation.dto.RequestCompilationDto;
import ru.practicum.mainservice.compilation.dto.ResponseCompilationDto;
import ru.practicum.mainservice.compilation.dto.UpdateRequestCompilationDto;
import ru.practicum.mainservice.compilation.mapper.CompilationMapper;
import ru.practicum.mainservice.compilation.model.Compilation;
import ru.practicum.mainservice.compilation.repository.CompilationRepository;
import ru.practicum.mainservice.event.dto.ShortResponseEventDto;
import ru.practicum.mainservice.event.mapper.EventMapper;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.user.mapper.UserMapper;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    private Compilation getCompilationById(long id) {
        return compilationRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Подборка " +
                "событий с id = %s отсутствует в БД. Выполнить операцию невозможно!", id)));
    }

    private Category getCategoryById(long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Категория " +
                "с id = %s отсутствует в БД. Выполнить операцию невозможно!", id)));
    }

    private User getUserById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Пользователь " +
                "с id = %s отсутствует в БД. Выполнить операцию невозможно!", id)));
    }

    @Override
    public ResponseCompilationDto create(RequestCompilationDto compilationDto) {
        System.out.println("compilationDto " + compilationDto);
        if (compilationDto.getPinned() == null) {
            compilationDto.setPinned(false);
        }
        System.out.println("compilationDto " + compilationDto);
        List<Event> events = new ArrayList<>();
        List<Long> eventIds = compilationDto.getEvents();
        List<ShortResponseEventDto> eventDtoList = new ArrayList<>();
        if (eventIds != null) {
            events = eventRepository.findAllByIdIn(eventIds);
            eventDtoList = toEventDtoList(events);
        }

        Compilation compilationData = CompilationMapper.toCompilation(compilationDto, events);
        System.out.println("compilationData " + compilationData);
        Compilation compilation = compilationRepository.save(compilationData);
        System.out.println("compilation " + compilation);
        log.info("Данные новой подборки событий добавлены в БД: {}.", compilation);

        ResponseCompilationDto responseCompilationDto = CompilationMapper.toResponseCompilationDto(compilation,
                eventDtoList);
        log.info("Новая подборка событий создана: {}.", responseCompilationDto);
        System.out.println("responseCompilationDto " + responseCompilationDto);
        return responseCompilationDto;
    }

    @Override
    public ResponseCompilationDto getById(Long compId) {
        Compilation compilation = getCompilationById(compId);
        List<Event> events = compilation.getEvents();;
        List<ShortResponseEventDto> eventDtoList = toEventDtoList(events);
        log.info("Подборка событий найдена в БД: {}.", compilation);
        ResponseCompilationDto responseCompilationDto = CompilationMapper.toResponseCompilationDto(compilation,
                eventDtoList);
        log.info("Подборка событий сформирована: {}.", responseCompilationDto);
        return responseCompilationDto;
    }

    @Override
    public List<ResponseCompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        System.out.println("pinned, from, size " + pinned + from + size);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findAllByPinned(true, pageable);
        } else {
            compilations = compilationRepository.findAll(pageable).getContent();
        }
        System.out.println("compilations " + compilations);
        if (compilations.isEmpty()) {
            log.info("По заданным условиям подборки событий отсутствуют.");
            return new ArrayList<>();
        } else {
            List<ResponseCompilationDto> resultList = compilations.stream()
                    .map(compilation -> {
                        List<ShortResponseEventDto> eventDtoList = toEventDtoList(compilation.getEvents());
                        return CompilationMapper.toResponseCompilationDto(compilation, eventDtoList);
                    })
                    .collect(toList());
            log.info("По заданным условиям сформирован список подборок событий.");
            return resultList;
        }
    }

    @Override
    public ResponseCompilationDto update(Long compId, UpdateRequestCompilationDto compilationDto) {
        System.out.println("4");
        Compilation oldCompilation = getCompilationById(compId);
        Boolean compilationPinned = compilationDto.getPinned();
        if (compilationPinned != null) {
            oldCompilation.setPinned(compilationPinned);
        }

        String compilationTitle = compilationDto.getTitle();
        if (compilationTitle != null) {
            oldCompilation.setTitle(compilationTitle);
        }

        List<Long> eventIds = compilationDto.getEvents();
        List<ShortResponseEventDto> eventDtoList;
        if (eventIds != null) {
            List<Event> newEvents = eventRepository.findAllByIdIn(eventIds);
            eventDtoList = toEventDtoList(newEvents);
            oldCompilation.setEvents(newEvents);
        } else {
            List<Event> oldEvents = oldCompilation.getEvents();
            eventDtoList = toEventDtoList(oldEvents);
        }

        Compilation compilation = compilationRepository.save(oldCompilation);
        log.info("Данные обновлённой подборки событий добавлены в БД: {}.", compilation);

        ResponseCompilationDto responseCompilationDto = CompilationMapper.toResponseCompilationDto(compilation,
                eventDtoList);
        log.info("Подборка событий обновлена: {}.", responseCompilationDto);
        return responseCompilationDto;
    }

    @Override
    public void delete(Long compId) {
        System.out.println("5");
        Compilation compilation = getCompilationById(compId);
        log.info("Подборка найдена в БД: {}.", compilation);
        compilationRepository.deleteById(compId);
        log.info("Все данные подборки событий удалены.");
    }

    private List<ShortResponseEventDto> toEventDtoList(List<Event> events) {
        return events.stream()
                .map(event -> {
                    Category eventsCategory = getCategoryById(event.getCategory().getId());
                    User eventsInitiator = getUserById(event.getInitiator().getId());
                    return EventMapper.toShortResponseEventDto(event,
                            CategoryMapper.toResponseCategoryDto(eventsCategory),
                            UserMapper.toShortResponseUserDto(eventsInitiator));
                })
                .collect(Collectors.toList());
    }

    private void isExistCompilation(long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException(String.format("Подборка с id = %s отсутствует в БД. " +
                    "Выполнить операцию невозможно!", id));
        }
    }

}