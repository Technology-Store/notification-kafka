package com.winnguyen1905.notification.service;

import org.apache.logging.log4j.message.SimpleMessage;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;
import com.winnguyen1905.notification.configuration.NotificationProcessingConfiguration;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationMQConsumer {

    private final AmqpTemplate rabbitTemplate;

    @RabbitListener(queues = NotificationProcessingConfiguration.NOTI_QUEUE_NAME)
    public void receive(@Payload Message message, Channel channel) throws Exception {
        String messageContent = new String(message.getBody());

        System.out.println(">> Processing message: " + messageContent);

        // Simulate processing logic and error
        if (messageContent.contains("error")) {
            throw new Exception(">> Error processing message");
        }

        // Acknowledge the message
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}