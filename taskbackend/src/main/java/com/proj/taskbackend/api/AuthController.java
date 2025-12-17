package com.proj.taskbackend.api; // Check your package name

import com.proj.taskbackend.core.service.AuthService;
import com.proj.taskbackend.dto.AuthenticationResponse;
import com.proj.taskbackend.dto.LoginRequest;
import com.proj.taskbackend.dto.RegisterRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        // 1. Authenticate and get the token string
        AuthenticationResponse authResponse = authService.login(request);

    // 2. Create a cookie header with SameSite=None so cross-origin XHR requests send it
    // Use Secure; localhost is treated as a secure context in modern browsers.
    String token = authResponse.getToken();
    int maxAge = 24 * 60 * 60; // 1 day
    String cookieValue = String.format("jwt=%s; Path=/; HttpOnly; SameSite=None; Max-Age=%d", token, maxAge);
    response.addHeader("Set-Cookie", cookieValue);

        // 4. Return a simple success message (No token in body!)
        return ResponseEntity.ok("Login successful");
    }

    // Add this inside AuthController
    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUser(org.springframework.security.core.Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok(authentication.getName()); // Returns the email
        }
        return ResponseEntity.status(401).build(); // Returns 401 if not logged in
    }

    // Logout endpoint: clears the jwt cookie
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        // Expire cookie with SameSite and Secure attributes so browser removes it
        String cookieValue = String.format("jwt=%s; Path=/; HttpOnly; SameSite=None; Max-Age=%d", "", 0);
        response.addHeader("Set-Cookie", cookieValue);
        return ResponseEntity.ok("Logged out");
    }
}