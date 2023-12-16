package com.example.resourceservice.service;

import com.example.resourceservice.dto.SongMetadataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.resourceservice.model.Resource;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Service;
import com.example.resourceservice.repository.ResourceRepository;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    private final WebClient.Builder webClientBuilder;

    private final static String SONG_SERVICE_URL = "http://localhost:8082/songs";

    @Transactional
    public Integer uploadResource(MultipartFile file) throws IOException {
        byte[] fileData;
        try {
            fileData = file.getBytes();
        } catch (IOException e) {
            log.error("Error reading file data", e);
            throw new FileUploadException("Could not read uploaded file!", e);
        }

        Resource resource = extractMetadata(fileData);
        resource.setFile(null);
        Resource savedResource;
        try {
            savedResource = resourceRepository.save(resource);
            savedResource.setFile(fileData);
            resourceRepository.save(savedResource);
        } catch (Exception e) {
            log.error("Error while saving file.", e);
            throw new FileUploadException("Could not upload file!", e);
        }

        // Send metadata to Song Service
        sendMetadataToSongService(savedResource);

        return savedResource.getId();
    }

    public Resource getResourceById(Integer resourceId) {
        log.info("Called getResourceById()");
        return resourceRepository.findById(resourceId).orElse(null);
    }

    public void deleteResource(List<Integer> resourceIds) {
        log.info("Called deleteResource()");
        resourceRepository.deleteAllByIdInBatch(resourceIds);
    }


    private Resource extractMetadata(byte[] fileData) {
        Tika tika = new Tika();
        Metadata metadata = new Metadata();

        try (InputStream stream = new ByteArrayInputStream(fileData)) {
            tika.parse(stream, metadata);
        } catch (Exception e) {
            log.error("Error while parsing file.", e);
        }

        Resource resource = new Resource();
        resource.setFile(fileData);
        resource.setName(metadata.get(TikaCoreProperties.TITLE));
        resource.setArtist(metadata.get(TikaCoreProperties.CREATOR));
        resource.setAlbum(metadata.get(TikaCoreProperties.DESCRIPTION));
        resource.setGenre(metadata.get(TikaCoreProperties.SUBJECT));

        Optional.ofNullable(metadata.get(TikaCoreProperties.MODIFIED))
                .map(Integer::valueOf)
                .ifPresent(resource::setYear);
        resource.setDuration(metadata.get(TikaCoreProperties.FORMAT));

        return resource;
    }

    private void sendMetadataToSongService(Resource resource) {
        SongMetadataDto songMetadataDto = new SongMetadataDto();
        songMetadataDto.setName(resource.getName());
        songMetadataDto.setArtist(resource.getArtist());
        songMetadataDto.setAlbum(resource.getAlbum());
        songMetadataDto.setGenre(resource.getGenre());
        songMetadataDto.setYear(resource.getYear());
        songMetadataDto.setDuration(resource.getDuration());

        try {
            webClientBuilder.build()
                    .post()
                    .uri(SONG_SERVICE_URL)
                    .body(BodyInserters.fromValue(songMetadataDto))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("Error occurred while calling Song Service: " + ex.getMessage());
            // Add more proper handling
        } catch (Exception ex) {
            log.error("Unexpected error occurred while calling Song Service: " + ex.getMessage());
            // Add more proper handling
        }
    }
}
