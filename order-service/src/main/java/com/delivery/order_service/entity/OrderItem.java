package com.delivery.order_service.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal price;

    protected OrderItem(){}

    public OrderItem(String productName, Integer quantity, BigDecimal price){
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    void setOrder(Order order){
        this.order = order;
    }

    public BigDecimal getSubtotal(){
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public Long getId(){return id;}
    public Order getOrder(){return order;}
    public String getProductName(){return productName;}
    public Integer getQuantity(){return quantity;}
    public BigDecimal getPrice(){return price;}
}
