package com.example.resourceservice.unit;

import com.amazonaws.services.s3.AmazonS3;
import com.example.resourceservice.repository.ResourceLocationRepository;
import com.example.resourceservice.repository.ResourceRepository;
import com.example.resourceservice.service.ResourceService;
import com.example.resourceservice.service.StorageService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ResourceServiceUnitTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private AmazonS3 s3client;

    @Mock
    private StorageService storageService;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    ResourceLocationRepository resourceLocationRepository;

    @InjectMocks
    ResourceService resourceService;

    @Test
    void whenValidUploadResource_thenSuccess() throws IOException {
        // given
        MultipartFile file = mock(MultipartFile.class);
        byte[] bytes = new byte[0];
        when(file.getBytes()).thenReturn(bytes);
        when(resourceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        resourceService.uploadResource(file);

        // then
        verify(resourceRepository, times(2)).save(any());
    }

    @Test
    public void whenResourceDeleted_thenSuccess() {
        // given
        Integer resourceId = 1;

        // when
        resourceService.deleteResource(Collections.singletonList(resourceId));

        // then
        verify(resourceRepository, times(1)).deleteAllByIdInBatch(Collections.singletonList(resourceId));
    }

    @Test
    public void whenValidUploadFileToS3Bucket_thenSuccess() {
        // given
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("name");

        // when
        resourceService.uploadFileToS3Bucket(multipartFile);

        // then
        verify(storageService, times(1)).uploadFileToS3(multipartFile);
    }
}
