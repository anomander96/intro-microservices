package com.example.resourceservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.example.resourceservice.dto.SongMetadataDto;
import com.example.resourceservice.model.ResourceLocation;
import com.example.resourceservice.repository.ResourceLocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.resourceservice.model.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.example.resourceservice.repository.ResourceRepository;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import jakarta.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    private final ResourceLocationRepository resourceLocationRepository;

    private final WebClient.Builder webClientBuilder;

    private final AmazonS3 s3client;

    private final StorageService storageService;

    private final RabbitTemplate rabbitTemplate;

    @Value("${S3_BUCKET_NAME}")
    private String bucketName;

    @Value("${RESOURCE_EXCHANGE_NAME}")
    private String exchangeName;

    private final static String SONG_SERVICE_URL = "http://song-service:8082/songs";

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

        // Send message to RabbitMQ for the Resource Processor to start processing
        sendResourceToProcessor(savedResource.getId());

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

    @Transactional
    public Integer uploadFileToS3Bucket(MultipartFile multipartFile) {
        // this method upload file to s3 bucket and save the location into our db
        storageService.uploadFileToS3(multipartFile);

        // save to db
        ResourceLocation resourceLocation = new ResourceLocation();
        String s3FileLocation = String.valueOf(s3client.getUrl(bucketName, multipartFile.getOriginalFilename()));

        log.info("File location is: " + s3FileLocation);
        resourceLocation.setKey(multipartFile.getOriginalFilename());
        resourceLocation.setFileLocation(s3FileLocation);
        ResourceLocation savedResourceLocation = resourceLocationRepository.save(resourceLocation);

        // Send message to RabbitMQ for the Resource Processor to start processing
        sendResourceToProcessor(savedResourceLocation.getId());

        return savedResourceLocation.getId();
    }

    @Transactional
    public Resource getResourceFromS3(Integer resourceLocationId) {
        ResourceLocation resourceLocation = resourceLocationRepository.getReferenceById(resourceLocationId);
        File fileData = storageService.getFileFromS3(resourceLocation.getKey());
        Resource resource = null;
        try {
            resource = extractMetadata(FileUtils.readFileToByteArray(fileData));
        } catch (IOException e) {
            log.error("Error during extracting fileMetadata");
        }
        return resource;
    }

    private Resource extractMetadata(byte[] fileData) {
        Metadata metadata = new Metadata();
        AutoDetectParser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        ParseContext parseContext = new ParseContext();

        try (InputStream stream = new ByteArrayInputStream(fileData)) {
            parser.parse(stream, handler, metadata, parseContext);

            log.info("Parsed Metadata: " + metadata);

            Resource resource = new Resource();
            resource.setFile(fileData);
            resource.setName(metadata.get("dc:title"));
            resource.setArtist(metadata.get("xmpDM:artist"));
            resource.setAlbum(metadata.get("xmpDM:album"));
            resource.setGenre(metadata.get("xmpDM:audioCompressor"));

            Optional.ofNullable(metadata.get("xmpDM:releaseDate"))
                    .map(s -> s.split("-")[0])
                    .map(Integer::parseInt)
                    .ifPresent(resource::setYear);

            Optional.ofNullable(metadata.get("xmpDM:duration"))
                    .map(s -> s.split("\\.")[0])
                    .ifPresent(resource::setDuration);

            return resource;
        } catch (Exception e) {
            log.error("Error while parsing file.", e);
        }

        return null;
    }

    private void sendMetadataToSongService(Resource resource) {
        SongMetadataDto songMetadataDto = new SongMetadataDto();
        songMetadataDto.setName(resource.getName());
        songMetadataDto.setArtist(resource.getArtist());
        songMetadataDto.setAlbum(resource.getAlbum());
        songMetadataDto.setGenre(resource.getGenre());
        songMetadataDto.setYear(resource.getYear());
        songMetadataDto.setDuration(resource.getDuration());

        log.info("Sending to Song Service: " + songMetadataDto);

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
            // add correct handling in future
        } catch (Exception ex) {
            log.error("Unexpected error occurred while calling Song Service: " + ex.getMessage());
            // add correct handling in future
        }
    }

    private void sendResourceToProcessor(Integer resourceId) {
        String routingKey = "resource.upload";

        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, resourceId);
            log.info("Resource sent to the processor: " + resourceId);
        } catch (AmqpException ex) {
            log.error("Error occurred while sending resource to the processor: " + ex.getMessage());
            // add correct handling in future
        }
    }
}
