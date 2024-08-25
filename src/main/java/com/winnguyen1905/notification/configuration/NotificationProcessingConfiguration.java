package com.winnguyen1905.notification.configuration;
 
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationProcessingConfiguration {

	public static final String NOTI_QUEUE_NAME = "notification-queue";
	public static final String NOTI_DIRECT_EXCHANGE = "notification-exchange";
	public static final String NOTI_ROUTING_KEY = "notification-routing-key";

	@Bean
	Queue queue() {
		return new Queue(NOTI_QUEUE_NAME, false);
	}

	@Bean
	DirectExchange exchange() {
		return new DirectExchange(NOTI_DIRECT_EXCHANGE);
	}

	@Bean
	Binding binding(Queue queue, DirectExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(NOTI_ROUTING_KEY);
	}

	@Bean
	AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(jsonMessageConverter);
		return rabbitTemplate;
	}

}