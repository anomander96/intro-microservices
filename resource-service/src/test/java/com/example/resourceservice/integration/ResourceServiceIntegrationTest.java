package com.example.resourceservice.integration;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"AWS_ACCESS_KEY_ID=testAccessKey",
        "AWS_SECRET_ACCESS_KEY=testSecretKey",
        "AWS_REGION=test-region",
        "S3_BUCKET_NAME=test",
        "RESOURCE_EXCHANGE_NAME=test"
})
@AutoConfigureMockMvc
public class ResourceServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String BASE_URL = "/resources";

    @MockBean
    private AmazonS3 amazonS3;

    @BeforeEach
    public void setUp() {
        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration("http://localhost:8001", "us-west-2")
                )
                .build();


        when(amazonS3.putObject(anyString(), anyString(), any(File.class))).thenReturn(new PutObjectResult());
    }

    @Test
    void whenCallUploadResource_thenReturnsStatus() throws Exception {
        String uri = BASE_URL + "/upload";

        mockMvc.perform(MockMvcRequestBuilders.get(uri))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.multipart(uri)
                        .file("file", "test data".getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }
}
