package com.proj.taskbackend.core.service;


import com.proj.taskbackend.core.model.User;
import com.proj.taskbackend.core.repository.UserRepository;
import com.proj.taskbackend.dto.AuthenticationResponse;
import com.proj.taskbackend.dto.LoginRequest;
import com.proj.taskbackend.dto.RegisterRequest;
import com.proj.taskbackend.infra.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        // Create user with hashed password
        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        // Generate token immediately so they don't have to login again
        var token = jwtUtils.generateToken(user.getEmail());
        return AuthenticationResponse.builder().token(token).build();
    }

    public AuthenticationResponse login(LoginRequest request) {
        // This line does the heavy lifting: checks email & password against DB
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // If we get here, the user is valid. Generate token.
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        var token = jwtUtils.generateToken(user.getEmail());
        return AuthenticationResponse.builder().token(token).build();
    }
}