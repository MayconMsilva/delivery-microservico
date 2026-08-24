package com.delivery.order_service.controller;

import com.delivery.order_service.dto.request.CreateOrderRequest;
import com.delivery.order_service.dto.response.OrderResponse;
import com.delivery.order_service.entity.Order;
import com.delivery.order_service.entity.OrderItem;
import com.delivery.order_service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request){
        List<OrderItem> items = request.items().stream()
                .map(i -> new OrderItem(i.productName(), i.quantity(), i.price()))
                .toList();

        Order order = orderService.create(currentUserId(), items);
        return ResponseEntity.ok(toResponse(order));
    }

    @GetMapping
    public ResponseEntity<OrderResponse> findById(@PathVariable Long id){
        Order order = orderService.findById(id, currentUserId(), currentRole());
        return ResponseEntity.ok(toResponse(order));
    }

    private Long currentUserId(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getPrincipal();
    }

    private String currentRole(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
    }

    private OrderResponse toResponse(Order order){
        return new OrderResponse(order.getId(), order.getStatus(), order.getTotal(), order.getCreatedAt());
    }
}
