package com.delivery.curstomer_service.repository;

import com.delivery.curstomer_service.entity.Customer;
import org.hibernate.internal.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;



public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUserId(Long userId);
}
