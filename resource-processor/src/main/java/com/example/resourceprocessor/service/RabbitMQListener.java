package com.example.resourceprocessor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQListener {

    
    private final ResourceProcessingService resourceProcessingService;

    @RabbitListener(queues = "${RESOURCE_QUEUE_NAME}")
    public void onResourceAdded(Integer resourceId) {
        resourceProcessingService.processResource(resourceId);
        log.info("Received message from queue: " + resourceId);
    }
}
