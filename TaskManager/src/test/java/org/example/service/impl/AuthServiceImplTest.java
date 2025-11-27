package org.example.service.impl;

import static org.hamcrest.Matchers.containsString;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.AuthRequestDTO;
import org.example.dto.RegisterRequestDto;
import org.example.entity.User;
import org.example.jwt.JwtUtil;
import org.example.repository.UserRepository;
import org.example.util.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class AuthServiceImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void healthEndpoint_shouldReturn200WithoutAuth() throws Exception {
        mockMvc.perform(get("/api/test/health"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Application is running")));
    }

    @Test
    void login() throws Exception{
        User user = User.builder()
                .username("loginuser")
                .email("login@example.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.USER)
                .build();
        userRepository.save(user);

        AuthRequestDTO req = new AuthRequestDTO("login@example.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.status").value("Login successful"));
    }

    @Test
    void register() throws Exception {
        RegisterRequestDto req = new RegisterRequestDto(
                "testuser",
                "test@example.com",
                "password123",
                Role.USER
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.status").value("Registration successful"));
    }
}