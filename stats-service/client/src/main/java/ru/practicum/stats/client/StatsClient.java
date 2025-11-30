package ru.practicum.stats.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stats.dto.EndpointHitDto;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class StatsClient {

    private final RestTemplate statsRestTemplate;

    @Value("${stats-server.url}")
    private String statsServerUrl;

    public void hit(String app, String uri, String ip) {
        EndpointHitDto dto = new EndpointHitDto(
                null,
                app,
                uri,
                ip,
                LocalDateTime.now()
        );
        statsRestTemplate.postForEntity(
                statsServerUrl + "/hit",
                dto,
                Void.class
        );
    }
}
