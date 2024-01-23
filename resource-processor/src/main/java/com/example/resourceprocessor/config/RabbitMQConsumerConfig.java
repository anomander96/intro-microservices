package com.example.resourceprocessor.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConsumerConfig {

    @Value("${RESOURCE_QUEUE_NAME}")
    private String queueName;

    @Value("${RESOURCE_EXCHANGE_NAME}")
    private String exchangeName;

    @Bean
    Queue resourceQueue() {
        return new Queue(queueName, false);
    }

    @Bean
    TopicExchange resourceExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    Binding binding(Queue resourceQueue, TopicExchange resourceExchange) {
        return BindingBuilder.bind(resourceQueue).to(resourceExchange).with("resource.*");
    }
}
