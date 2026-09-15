package com.delivery.delivery_service.service;

import com.delivery.delivery_service.config.RabbitConfig;
import com.delivery.delivery_service.entity.model.Delivery;
import com.delivery.delivery_service.event.OrderCreatedEvent;
import com.delivery.delivery_service.repository.DeliveryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class OrderCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);

    private final DeliveryRepository deliveryRepository;

    public OrderCreatedListener(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void handleOrderCreated(OrderCreatedEvent event) {
        if (deliveryRepository.findByOrderId(event.orderId()).isPresent()) {
            log.warn("Entrega já existe para o pedido {} - mensagem duplicada ignorada", event.orderId());
            return;
        }

        Delivery delivery = new Delivery(event.orderId(), event.customerId());
        deliveryRepository.save(delivery);

        log.info("Entrega criada para o pedido {}", event.orderId());
    }
}