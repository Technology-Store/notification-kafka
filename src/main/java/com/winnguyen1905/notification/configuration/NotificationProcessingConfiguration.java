package com.winnguyen1905.notification.configuration;
 
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

import org.springframework.util.ErrorHandler;

@Configuration
public class NotificationProcessingConfiguration {

	public static final String NOTI_QUEUE_NAME = "notification-queue";
	public static final String NOTI_DIRECT_EXCHANGE = "notification-exchange";
	public static final String NOTI_ROUTING_KEY = "notification-routing-key";

    @Bean
	Queue queue() {
        return QueueBuilder
                .durable(NOTI_QUEUE_NAME)
                .deadLetterExchange(DLXNotificationProcessingConfiguration.NOTI_DL_EXCHANGE)
                .deadLetterRoutingKey(DLXNotificationProcessingConfiguration.NOTI_DL_ROUTING_KEY)
                .expires(10000)
                .build();
	}

	@Bean
    DirectExchange exchange() {
        return new DirectExchange(NOTI_DIRECT_EXCHANGE, false, false);
    }
    
	@Bean
	Binding binding() {
		return BindingBuilder.bind(queue()).to(exchange()).with(NOTI_ROUTING_KEY);
	}

	@Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

	@Bean
	AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setDefaultReceiveQueue(NOTI_QUEUE_NAME);
        rabbitTemplate.setRoutingKey(NOTI_ROUTING_KEY);
		rabbitTemplate.setReplyAddress(queue().getName());
		rabbitTemplate.setUseDirectReplyToContainer(false);
		return rabbitTemplate;
	}

	@Bean("operationsInterceptor")
    RetryOperationsInterceptor setRetries() {
        return RetryInterceptorBuilder.stateless()
                .maxAttempts(2)
                .backOffOptions(1000,
                        2,
                        10000)
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build();
    }

	@Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        final SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMaxConcurrentConsumers(Integer.valueOf(1000000));
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setAdviceChain(setRetries());
        return factory;
    }

	@Bean
    ErrorHandler errorHandler() {
        return new ConditionalRejectingErrorHandler(new MyFatalExceptionStrategy());
    }

    public static class MyFatalExceptionStrategy extends ConditionalRejectingErrorHandler.DefaultExceptionStrategy {
        @Override
        public boolean isFatal(Throwable t) {
            if (t instanceof ListenerExecutionFailedException) {
                ListenerExecutionFailedException lefe = (ListenerExecutionFailedException) t;
                logger.error("Failed to process inbound message from queue "
                        + lefe.getFailedMessage().getMessageProperties().getConsumerQueue()
                        + "; failed message: " + lefe.getFailedMessage(), t);
            }
            return super.isFatal(t);
        }
    }

}