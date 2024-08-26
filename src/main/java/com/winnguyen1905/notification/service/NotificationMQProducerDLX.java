package com.winnguyen1905.notification.service;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import com.winnguyen1905.notification.configuration.NotificationProcessingConfiguration;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationMQProducerDLX {

    private final AmqpTemplate rabbitTemplate;

	public void send(String company) {
		this.rabbitTemplate.convertAndSend(
			NotificationProcessingConfiguration.NOTI_DIRECT_EXCHANGE, 
			NotificationProcessingConfiguration.NOTI_ROUTING_KEY, company);
		System.out.println(">> Send msg = " + company);
	}

}