package com.example.resourceprocessor.service;

import com.example.resourceprocessor.dto.ResourceDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class ResourceProcessingService {

    private final WebClient resourceServiceWebClient;
    private final WebClient songServiceWebClient;

    public ResourceProcessingService(WebClient.Builder webClientBuilder){
        //Create a WebClient for the Resource Service
        this.resourceServiceWebClient = webClientBuilder.baseUrl("http://resource-service").build();
        //Create a WebClient for the Song Service
        this.songServiceWebClient = webClientBuilder.baseUrl("http://song-service").build();
    }

//    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 2000, multiplier = 2))
    public void processResource(Integer resourceLocationId) {
        // Fetch the resource object from the Resource Service using a synchronous call
        ResourceDTO resourceDto = resourceServiceWebClient.get()
                .uri("/resourceLocation/" + resourceLocationId)
                .retrieve()
                .bodyToMono(ResourceDTO.class)
                .block();

        if (resourceDto == null) {
            log.error("Resource not found for id: " + resourceLocationId);
            return;
        }

        // Send the resource to the Song Service using a synchronous call
        songServiceWebClient.post()
                .uri("/songs")
                .bodyValue(resourceDto)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Recover
    public void recover(Exception e, Integer resourceId){
        // handle recovery logic here
    }

}
