package com.delivery.curstomer_service.service;

import com.delivery.curstomer_service.entity.Customer;
import com.delivery.curstomer_service.exception.CustomerAlreadyExistsException;
import com.delivery.curstomer_service.exception.CustomerNotFoundException;
import com.delivery.curstomer_service.exception.ForbiddenAccessException;
import com.delivery.curstomer_service.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }

    public Customer create(Long userId, String name, String phone, String address){

        if (customerRepository.findByUserId(userId).isPresent()){
            throw new CustomerAlreadyExistsException();
        }

        Customer customer = new Customer(userId, name, phone, address);
        return customerRepository.save(customer);
    }

    public Customer findById(Long customerId, Long requesterUserId, String requesterRole){
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        if(!canAccess(customer, requesterUserId, requesterRole)){
            throw new ForbiddenAccessException();
        }
        return customer;
    }

    public Customer update(Long customerId, Long requesterUserId, String requesterRole, String name,
                           String phone, String address){
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        if (!canAccess(customer, requesterUserId, requesterRole)){
            throw new ForbiddenAccessException();
        }

        customer.update(name, phone, address);
        return customerRepository.save(customer);
    }

    private boolean canAccess(Customer customer, Long requesterUserId, String requesterRole){
        boolean isOwner = customer.getUserId().equals(requesterUserId);
        boolean isAdmin = "ADMIN".equals(requesterRole);
        return isOwner || isAdmin;
    }
}
