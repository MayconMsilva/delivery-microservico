package com.delivery.order_service.config;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;



@Configuration
public class RabbiqConfig {

    public static final String EXCHANGE_NAME = "delivery.exchange";
    public static final String QUEUE_NAME = "order.created.queue";
    public static final String ROUTING_KEY = "order.created";

    @Bean
    public Queue orderCreatedQueue(){
        return new Queue(QUEUE_NAME, true) {
        };
    }

    @Bean
    public DirectExchange deliveryExchange(){
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue orderCreatedQueue, DirectExchange deliveryExchange){
        return BindingBuilder.bind(orderCreatedQueue)
                .to(deliveryExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new JacksonJsonMessageConverter();
    };
}
