package com.delivery.order_service.service;


import com.delivery.order_service.config.RabbiqConfig;
import com.delivery.order_service.entity.Order;
import com.delivery.order_service.entity.OrderItem;
import com.delivery.order_service.event.OrderCreatedEvent;
import com.delivery.order_service.exception.ForbiddenAccessException;
import com.delivery.order_service.exception.OrderNotFoundException;
import com.delivery.order_service.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    public OrderService(OrderRepository orderRepository, RabbitTemplate rabbitTemplate){

        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public Order create(Long customerId, List<OrderItem> items){
        Order order = new Order(customerId, items);
        Order saved = orderRepository.save(order);

        publishOrderCreated(saved);

        return saved;
    }

    private void publishOrderCreated(Order order){
        try{
            OrderCreatedEvent event = new OrderCreatedEvent(
                    order.getId(), order.getCustomerId(), order.getCreatedAt());

            rabbitTemplate.convertAndSend(
                    RabbiqConfig.EXCHANGE_NAME, RabbiqConfig.ROUTING_KEY, event
            );
        } catch (Exception e){
            log.error("Falha ao Publicar OrderCreated para o pedido {}: {}", order.getId(), e.getMessage());
        }

    }








    public Order findById(Long orderId, Long requesterUserId, String requesterRole){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);


        if (!canAccess(order, requesterUserId, requesterRole)){
            throw  new ForbiddenAccessException();
        }
        return order;
    }


    private boolean canAccess(Order order, Long requesterUserId, String requesterRole){
        boolean isOwner = order.getCustomerId().equals(requesterUserId);
        boolean isAdmin = "ADMIN".equals(requesterRole);
        return isOwner || isAdmin;
    }
}
