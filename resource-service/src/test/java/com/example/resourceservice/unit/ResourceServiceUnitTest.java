package com.example.resourceservice.unit;

import com.example.resourceservice.repository.ResourceRepository;
import com.example.resourceservice.service.ResourceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceUnitTest {

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    ResourceService resourceService;

    private static final String BASE_URL = "/resources";

    @Test
    public void whenResourceDeleted_thenSuccess() throws Exception {
        List<Integer> resourceIds = new ArrayList<Integer>(Arrays.asList(1,2,4,5));

        resourceService.deleteResource(resourceIds);

        verify(resourceRepository, times(1)).deleteAllByIdInBatch(resourceIds);
    }

    // add other tests in future
}
