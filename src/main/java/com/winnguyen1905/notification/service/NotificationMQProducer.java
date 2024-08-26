package com.winnguyen1905.notification.service;

import lombok.RequiredArgsConstructor;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import com.winnguyen1905.notification.configuration.NotificationProcessingConfiguration;

@Service
@RequiredArgsConstructor
public class NotificationMQProducer {

	private final AmqpTemplate rabbitTemplate;

	public void send(String company) {
		this.rabbitTemplate.convertAndSend(
			NotificationProcessingConfiguration.NOTI_DIRECT_EXCHANGE, 
			NotificationProcessingConfiguration.NOTI_ROUTING_KEY, company);
		System.out.println(">> Send msg = " + company);
	}

}