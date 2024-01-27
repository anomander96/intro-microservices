package com.example.resourceservice.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ResourceServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenCallUploadResource_thenReturnsStatus() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/upload/resource")
                        .file("file", "test data".getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @Test
    void whenCallDeleteResource_thenReturnsStatus() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/delete/resource/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenCallUploadFileToS3Bucket_thenReturnsStatus() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/upload/s3")
                        .file("file", "test data".getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }
}
