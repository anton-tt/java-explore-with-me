package ru.practicum.clie;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import java.util.Map;

@Component
public class StatClient {

    private final RestTemplate rest;
    private final String serverUrl;


    public StatClient(@Value("${stat-server.url}") String serverUrl) {
        this.rest = new RestTemplate();
        this.serverUrl = serverUrl;
    }

    public void saveHit(EndpointHitDto hitDto) {
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(hitDto);
        rest.exchange(serverUrl + "/hit", HttpMethod.POST, requestEntity, Object.class);
    }

    public ResponseEntity<ViewStatsDto[]> getStats(String start, String end, String[] uris, boolean unique) {
        String path;
        Map<String, Object> parameters;
        if (uris != null) {
            parameters = Map.of(
                    "start", start,
                    "end", end,
                    "uris", uris,
                    "unique", unique
            );
            path = serverUrl + "/stats/?start={start}&end={end}&uris={uris}&unique={unique}";
        } else {
            parameters = Map.of(
                    "start", start,
                    "end", end,
                    "unique", unique
            );
            path = serverUrl + "/stats/?start={start}&end={end}&unique={unique}";
        }
        ResponseEntity<ViewStatsDto[]> serverResponse = rest.getForEntity(path, ViewStatsDto[].class, parameters);
        if (serverResponse.getStatusCode().is2xxSuccessful()) {
            return serverResponse;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(serverResponse.getStatusCode());
        if (serverResponse.hasBody()) {
            return responseBuilder.body(serverResponse.getBody());
        }
        return responseBuilder.build();

    }

}