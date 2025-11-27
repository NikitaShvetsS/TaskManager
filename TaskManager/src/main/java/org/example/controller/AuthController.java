package org.example.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.AuthRequestDTO;
import org.example.dto.AuthResponseDTO;
import org.example.dto.RegisterRequestDto;
import org.example.exception.EmailAlreadyRegisteredException;
import org.example.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "APIs for user authentication and registration")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        log.info("Running method login");
        try {
            AuthResponseDTO response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponseDTO(null, "Login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDto request) {
        log.info("Running method register");
        try {
            AuthResponseDTO response = authService.register(request);
            return ResponseEntity.status(201).body(response);
        } catch (EmailAlreadyRegisteredException e) {
            return ResponseEntity.status(409)
                    .body(new AuthResponseDTO(null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponseDTO(null, "Registration failed: " + e.getMessage()));
        }
    }

}
