package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.server.model.EndpointHit;
import ru.practicum.server.model.ViewStats;
import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<EndpointHit, Integer> {

    @Query("SELECT new ru.practicum.server.model.ViewStats(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM EndpointHit e " +
            "WHERE e.created BETWEEN :start AND :end AND e.uri IN :uris " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(DISTINCT e.ip) DESC")
    List<ViewStats> getAllByUrisAndUniqueIp(LocalDateTime start,
                                            LocalDateTime end,
                                            List<String> uris);

    @Query("SELECT new ru.practicum.server.model.ViewStats(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM EndpointHit e " +
            "WHERE e.created BETWEEN :start AND :end " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(DISTINCT e.ip) DESC")
    List<ViewStats> getAllByUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.server.model.ViewStats(e.app, e.uri, COUNT(e.ip)) " +
            "FROM EndpointHit e " +
            "WHERE e.created BETWEEN :start AND :end AND e.uri IN :uris " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC")
    List<ViewStats> getAllByUris(LocalDateTime start,
                                 LocalDateTime end,
                                 List<String> uris);

    @Query("SELECT new ru.practicum.server.model.ViewStats(e.app, e.uri, COUNT(e.ip)) " +
            "FROM EndpointHit e " +
            "WHERE e.created BETWEEN :start AND :end " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.ip) DESC")
    List<ViewStats> getAll(LocalDateTime start, LocalDateTime end);

}