package com.delivery_beta.auth_service.model;

import com.delivery_beta.auth_service.model.enums.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;


    protected User(){}

    public User(String email, String password, String name, Role role){
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public Long getId(){return id;}
    public String getEmail(){return email;}
    public String getPassword() {return password;}
    public String getName() {return name;}
    public Role getRole() {return role;}
}
