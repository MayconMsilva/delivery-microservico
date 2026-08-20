package com.delivery.order_service.entity;

import jakarta.persistence.*;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "orders" )
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private BigDecimal total;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    protected Order(){}

    public Order(Long customerId, List<OrderItem> items){
        this.customerId = customerId;
        this.status = OrderStatus.CREATED;
        this.createdAt = Instant.now();
        this.items = new ArrayList<>();
        items.forEach(this::addItem);
        this.total = calculateTotal();
    }

    public void addItem(OrderItem item){
        item.setOrder(this);
        this.items.add(item);
    }

    private BigDecimal calculateTotal(){
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId(){return id;}
    public Long getCustomerId(){return customerId;}
    public OrderStatus getStatus(){return status;}
    public BigDecimal getTotal(){return total;}
    public Instant getCreatedAt(){return createdAt;}
    public List<OrderItem> getItems(){return items;}
}
