package com.delivery_beta.auth_service.service;

import com.delivery_beta.auth_service.exception.EmailAlreadyInUseException;
import com.delivery_beta.auth_service.model.User;
import com.delivery_beta.auth_service.model.enums.Role;
import com.delivery_beta.auth_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public RegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String email, String rawPassword, String name){
        if(userRepository.findByEmail(email).isPresent()){
            throw new EmailAlreadyInUseException();
        }

        String hashedPassword = passwordEncoder.encode(rawPassword);
        User user = new User(email, hashedPassword, name, Role.CUSTOMER);
        return userRepository.save(user);
    }
}
