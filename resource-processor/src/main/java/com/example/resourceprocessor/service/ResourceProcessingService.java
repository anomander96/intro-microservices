package com.example.resourceprocessor.service;

import com.example.resourceprocessor.dto.ResourceDto;
import com.example.resourceprocessor.dto.SongDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class ResourceProcessingService {

    private final WebClient resourceServiceWebClient;
    private final WebClient songServiceWebClient;

    public ResourceProcessingService(WebClient.Builder webClientBuilder){
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(config -> config
                        .defaultCodecs()
                        .maxInMemorySize(100 * 1024 * 1024)) // 100 MB size is allowed without any Exceptions
                .build();

        //Create a WebClient for the Resource Service
        this.resourceServiceWebClient = webClientBuilder.baseUrl("http://resource-service:8081/resources")
                .exchangeStrategies(exchangeStrategies)
                .build();
        //Create a WebClient for the Song Service
        this.songServiceWebClient = webClientBuilder.baseUrl("http://song-service:8082")
                .exchangeStrategies(exchangeStrategies)
                .build();
    }

    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 2000, multiplier = 2))
    public void processResource(Integer resourceLocationId) {
        // Fetch the resource object from the Resource Service using a synchronous call
        ResourceDto resourceDto = resourceServiceWebClient.get()
                .uri("/resourceLocation/" + resourceLocationId)
                .retrieve()
                .bodyToMono(ResourceDto.class)
                .block();

        if (resourceDto == null) {
            log.error("Resource not found for id: " + resourceLocationId);
            return;
        }

        SongDto songDto = convertToSongDto(resourceDto);

        // Send the resource to the Song Service using a synchronous call
        songServiceWebClient.post()
                .uri("/songs")
                .bodyValue(songDto)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Recover
    public void recover(Exception e, Integer resourceId){
        // handle recovery logic here
        log.info("Recovering started...");
    }

    private SongDto convertToSongDto(ResourceDto resourceDto) {
        SongDto songDto = new SongDto();
        songDto.setName(resourceDto.getName());
        songDto.setArtist(resourceDto.getArtist());
        songDto.setAlbum(resourceDto.getAlbum());
        songDto.setGenre(resourceDto.getGenre());
        songDto.setYear(resourceDto.getYear());
        songDto.setDuration(resourceDto.getDuration());
        return songDto;
    }

}
