package ru.practicum.stats.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class StatsClient {

    private final RestTemplate statsRestTemplate;

    @Value("${stats-server.url}")
    private String statsServerUrl;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void hit(String app, String uri, String ip) {
        String now = LocalDateTime.now().format(FORMATTER);

        EndpointHitDto dto = new EndpointHitDto(
                null,
                app,
                uri,
                ip,
                now
        );

        statsRestTemplate.postForEntity(
                statsServerUrl + "/hit",
                dto,
                Void.class
        );
    }

    public long getViews(LocalDateTime start, LocalDateTime end, String uri) {
        String startStr = start.format(FORMATTER);
        String endStr = end.format(FORMATTER);

        String url = UriComponentsBuilder
                .fromHttpUrl(statsServerUrl + "/stats")
                .queryParam("start", startStr)
                .queryParam("end", endStr)
                .queryParam("uris", uri)
                .queryParam("unique", true)
                .toUriString();

        ViewStatsDto[] response = statsRestTemplate.getForObject(url, ViewStatsDto[].class);

        if (response == null || response.length == 0) {
            return 0L;
        }
        return response[0].getHits() == null ? 0L : response[0].getHits();
    }
}
