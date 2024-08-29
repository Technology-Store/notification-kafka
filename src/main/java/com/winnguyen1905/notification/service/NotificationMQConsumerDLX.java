package com.winnguyen1905.notification.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.winnguyen1905.notification.configuration.DLXNotificationProcessingConfiguration;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationMQConsumerDLX {

	@RabbitListener(queues = DLXNotificationProcessingConfiguration.NOTI_DL_QUEUE_NAME)
	public void receive(@Payload Object message) {
		System.out.println(">> Received dlx message: " + message.toString());
	}

}