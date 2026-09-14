package com.delivery.delivery_service.entity.model;

import com.delivery.delivery_service.entity.enums.DeliveryStatus;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Delivery() {}


    public Delivery(Long orderId, Long customerId){
        this.orderId = orderId;
        this.customerId = customerId;
        this.status = DeliveryStatus.PENDING;
        this.createdAt = Instant.now();
    }


    public Long getId(){return id;}
    public Long getOrderId(){return orderId;}
    public Long getCustomerId(){return customerId;}
    public DeliveryStatus getStatus(){return status;}
    public Instant getCreatedAt(){return createdAt;}


    public void updateStatus(DeliveryStatus newStatus){
        this.status = newStatus;
    }
}
