package com.delivery.delivery_service.controller;

import com.delivery.delivery_service.dto.DeliveryResponse;
import com.delivery.delivery_service.entity.model.Delivery;
import com.delivery.delivery_service.service.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService){
        this.deliveryService = deliveryService;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<DeliveryResponse> findByOrderId(@PathVariable Long orderId){

        Delivery delivery = deliveryService.findByOrderId(orderId, currentUserId(), currentRole());
        return ResponseEntity.ok(toResponse(delivery));
    }

    private Long currentUserId(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getPrincipal();
    }

    private String currentRole(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
    }

    private DeliveryResponse toResponse(Delivery delivery){
        return new DeliveryResponse(delivery.getOrderId(), delivery.getStatus(), delivery.getCreatedAt());
    }
}
