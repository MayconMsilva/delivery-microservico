package com.delivery.curstomer_service.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "customers" )
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    protected Customer(){}

    public Customer(Long userId, String name, String phone, String address){
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }


    public Long getId(){return id;}
    public Long getUserId(){return userId;}
    public String getName(){return name;}
    public String getPhone(){return phone;}
    public String getAddress(){return address;}


    public void update(String name, String phone, String address){
        this.name = name;
        this.phone = phone;
        this.address = address;
    }
}
