package com.delivery.order_service.service;


import com.delivery.order_service.entity.Order;
import com.delivery.order_service.entity.OrderItem;
import com.delivery.order_service.exception.ForbiddenAccessException;
import com.delivery.order_service.exception.OrderNotFoundException;
import com.delivery.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    public Order create(Long customerId, List<OrderItem> items){
        Order order = new Order(customerId, items);
        return orderRepository.save(order);
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
