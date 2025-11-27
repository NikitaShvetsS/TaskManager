package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test", description = "Test endpoints for debugging")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Health check",
            description = "Simple health check endpoint that doesn't require authentication")
    public String health() {
        return "Application is running! Authentication is working correctly.";
    }


}
