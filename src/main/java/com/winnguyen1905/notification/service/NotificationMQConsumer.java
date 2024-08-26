package com.winnguyen1905.notification.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationMQConsumer {

    @RabbitListener(queues = "notification-queue")
    public void receive(@Payload Object message) {
        System.out.println(">> Received message: " + message.toString());
    }

}