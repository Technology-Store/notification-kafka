package com.winnguyen1905.notification.service;

import lombok.RequiredArgsConstructor;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.stereotype.Service;

import com.winnguyen1905.notification.configuration.NotificationProcessingConfiguration;


@Service
@RequiredArgsConstructor
public class NotificationMQProducer {

	private final AmqpTemplate rabbitTemplate;

	public void send(String company) {
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setContentType("application/json");
		messageProperties.setExpiration(String.valueOf(10000));

		Message payload = MessageBuilder.withBody(company.getBytes()).andProperties(messageProperties).build();
		this.rabbitTemplate.convertAndSend(
			NotificationProcessingConfiguration.NOTI_DIRECT_EXCHANGE,
			NotificationProcessingConfiguration.NOTI_ROUTING_KEY,
			payload);
		System.out.println(">> Send msg = " + company);
	}

}