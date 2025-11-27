package org.example.service;

import org.example.dto.AuthRequestDTO;
import org.example.dto.AuthResponseDTO;
import org.example.dto.RegisterRequestDto;


public interface AuthService {
    AuthResponseDTO login(AuthRequestDTO request);
    AuthResponseDTO register(RegisterRequestDto request);

}
