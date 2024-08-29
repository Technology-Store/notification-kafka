package com.winnguyen1905.notification.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DLXNotificationProcessingConfiguration {
    
    public static final String NOTI_DL_QUEUE_NAME = "DLQueue";
    public static final String NOTI_DL_EXCHANGE = "DLExchange";
    public static final String NOTI_DL_ROUTING_KEY = "routingkey";
    
    @Bean
    Binding dlBinding() {
        return BindingBuilder.bind(dlQueue()).to(deadLetterExchange()).with(NOTI_DL_ROUTING_KEY);
    }

    @Bean
    DirectExchange deadLetterExchange() {
        return new DirectExchange(NOTI_DL_EXCHANGE, false, false);
    }

    @Bean
    Queue dlQueue() {
        return QueueBuilder.durable(NOTI_DL_QUEUE_NAME).build();
    }

}
