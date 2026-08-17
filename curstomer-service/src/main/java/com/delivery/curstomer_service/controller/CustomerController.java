package com.delivery.curstomer_service.controller;


import com.delivery.curstomer_service.dto.request.CreateCustomerRequest;
import com.delivery.curstomer_service.dto.request.UpdateCustomerRequest;
import com.delivery.curstomer_service.dto.response.CustomerResponse;
import com.delivery.curstomer_service.entity.Customer;
import com.delivery.curstomer_service.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;


    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CreateCustomerRequest request){
        Long requesterUserId = currentUserId();
        Customer customer = customerService.create(
                requesterUserId, request.name(), request.phone(), request.address());
        return ResponseEntity.ok(toResponse(customer));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> findById(@PathVariable Long id){
        Customer customer = customerService.findById(id, currentUserId(), currentRole());
        return ResponseEntity.ok(toResponse(customer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateCustomerRequest request){

        Customer customer = customerService.update(id, currentUserId(), currentRole(),
                request.name(), request.phone(), request.address());
        return ResponseEntity.ok(toResponse(customer));
    }

    private Long currentUserId(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getPrincipal();
    }

    private String currentRole(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
    }

    private CustomerResponse toResponse(Customer customer){
        return new CustomerResponse(customer.getId(), customer.getName(),
                customer.getPhone(), customer.getAddress());
    }
}
