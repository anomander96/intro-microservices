package com.example.resourceservice.component;

import com.example.resourceservice.model.Resource;
import com.example.resourceservice.repository.ResourceRepository;
import com.example.resourceservice.service.ResourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ResourceServiceComponentTest {

    @Autowired
    private ResourceService resourceService;

    @MockBean
    private ResourceRepository resourceRepository;

    @Test
    void whenSaveResource_thenResourceShouldBeSaved() {
        Resource resource = new Resource();
        when(resourceRepository.save(resource)).thenReturn(resource);

        Resource createdResource = resourceRepository.save(resource);

        assertEquals(resource, createdResource);
    }

    // add another related tests
}
