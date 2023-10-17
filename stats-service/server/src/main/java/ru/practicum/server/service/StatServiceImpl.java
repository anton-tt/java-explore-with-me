package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.mapper.StatMapper;
import ru.practicum.server.model.EndpointHit;
import ru.practicum.server.model.ViewStats;
import ru.practicum.server.repository.StatRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImpl implements StatService {

   private final StatRepository statRepository;

   @Override
   public void saveHit(EndpointHitDto endpointHitDto) {
      EndpointHit endpointHit = StatMapper.toEndpointHit(endpointHitDto);
      log.info("Сохранение в БД информации о запросе пользователя: {}.", endpointHit);
      statRepository.save(endpointHit);
   }

    @Transactional(readOnly = true)
    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
       List<ViewStats> viewStatsList;
       boolean isNotEmptyUris = !(uris == null || uris.isEmpty());
       if (unique && isNotEmptyUris) {
           viewStatsList = statRepository.getAllByUrisAndUniqueIp(start, end, uris);
       } else if (unique) {
           viewStatsList = statRepository.getAllByUniqueIp(start, end);
       } else if (isNotEmptyUris) {
           viewStatsList = statRepository.getAllByUris(start, end, uris);
       } else {
           viewStatsList = statRepository.getAll(start, end);
       }

        List<ViewStatsDto> viewStatsListDto;
        if (viewStatsList == null) {
            viewStatsListDto = new ArrayList<>();
        } else {
            viewStatsListDto = viewStatsList
                    .stream()
                    .map(StatMapper::toViewStatsDto)
                    .collect(Collectors.toList());
        }
        return viewStatsListDto;
   }

}