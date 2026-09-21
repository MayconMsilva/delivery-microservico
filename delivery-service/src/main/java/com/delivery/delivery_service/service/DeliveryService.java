package com.delivery.delivery_service.service;

import com.delivery.delivery_service.entity.model.Delivery;
import com.delivery.delivery_service.exception.DeliveryNotFoundException;
import com.delivery.delivery_service.exception.ForbiddenAccessException;
import com.delivery.delivery_service.repository.DeliveryRepository;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;


    public DeliveryService(DeliveryRepository deliveryRepository){
        this.deliveryRepository = deliveryRepository;
    }

    public Delivery findByOrderId(Long orderId, Long requesterUserId, String requesterRole){
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(DeliveryNotFoundException::new);

        if (!canAccess(delivery, requesterUserId, requesterRole)){
            throw new ForbiddenAccessException();
        }
        return delivery;
    }

    private boolean canAccess(Delivery delivery, Long requesterUserId, String requesterRole){
        boolean isOwner = delivery.getCustomerId().equals(requesterUserId);
        boolean isAdmin = "ADMIN".equals(requesterRole);
        return isOwner || isAdmin;
    }
}
