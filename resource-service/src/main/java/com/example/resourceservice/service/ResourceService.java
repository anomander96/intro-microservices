package com.example.resourceservice.service;

import com.example.resourceservice.dto.SongMetadataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.resourceservice.model.Resource;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    private final WebClient.Builder webClientBuilder;

    @Transactional
    public Integer uploadResource(MultipartFile file) throws FileUploadException {
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
        resource.setTitle(metadata.get("title"));
        resource.setArtist(metadata.get("xmpDM:artist"));
        resource.setAlbum(metadata.get("xmpDM:album"));
        resource.setGenre(metadata.get("xmpDM:genre"));

        return resource;
    }

    private void sendMetadataToSongService(Resource resource) {
        SongMetadataDto songMetadataDto = new SongMetadataDto();
        songMetadataDto.setTitle(resource.getTitle());
        songMetadataDto.setArtist(resource.getArtist());
        songMetadataDto.setAlbum(resource.getAlbum());

        String songServiceUrl = "http://localhost:8080/songs";

        try {
            webClientBuilder.build()
                    .post()
                    .uri(songServiceUrl)
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
