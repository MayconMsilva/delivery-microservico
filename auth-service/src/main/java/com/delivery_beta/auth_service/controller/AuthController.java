package com.delivery_beta.auth_service.controller;


import com.delivery_beta.auth_service.dto.request.LoginRequest;
import com.delivery_beta.auth_service.dto.request.RegisterRequest;
import com.delivery_beta.auth_service.dto.response.TokenResponse;
import com.delivery_beta.auth_service.dto.response.UserResponse;
import com.delivery_beta.auth_service.model.User;
import com.delivery_beta.auth_service.security.JwtService;
import com.delivery_beta.auth_service.service.AuthService;
import com.delivery_beta.auth_service.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final RegistrationService registrationService;
    private final JwtService jwtService;


    public AuthController(AuthService authService, RegistrationService registrationService,
                          JwtService jwtService){
        this.authService = authService;
        this.registrationService = registrationService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request){

        User user = registrationService.register(request.email(), request.password(), request.name());
        return ResponseEntity.ok(new UserResponse(user.getId(), user.getEmail(), user.getName()));

    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request){

        User user = authService.authenticate(request.email(), request.password());
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new TokenResponse(token, jwtService.getExpirationSeconds()));
    }


}
