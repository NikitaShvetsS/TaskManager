package org.example.dto;

import lombok.*;
import org.example.util.Role;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserResponseDto {

    private UUID id;
    private String username;
    private String email;
    private Role role;

}