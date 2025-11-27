package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.UserRequestDto;
import org.example.dto.UserResponseDto;
import org.example.service.impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody UserRequestDto dto){
        UUID userId = userService.addUser(dto);
        log.info("adding a new user");
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User successfully added");
        response.put("user_id", userId);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/update/{uuid}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable("uuid") UUID uuid,
                                                          @RequestBody UserRequestDto dto){
        UUID userId = userService.updateUser(uuid, dto);
        log.info("updating user");
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User successfully updated");
        response.put("user_id", userId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{uuid}")
    public UserResponseDto getUserById(@PathVariable("uuid") UUID uuid){
        log.info("getting user by id");
        return userService.getUserById(uuid);
    }
    @GetMapping("/all")
    public List<UserResponseDto> getAllUsers(){
        log.info("getting all users");
        return userService.getAllUsers();
    }
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Map<String, Object>> deleteUserById(@PathVariable("uuid") UUID uuid){
        userService.deleteUser(uuid);
        log.info("deleting user");
        Map<String, Object> response = new HashMap<>();
        response.put("user_id", uuid);
        response.put("message", "User successfully deleted");
        return ResponseEntity.ok(response);
    }

}
