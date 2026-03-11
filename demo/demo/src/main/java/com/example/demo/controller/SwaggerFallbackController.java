package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SwaggerFallbackController {

    @GetMapping({"/api-docs", "/swagger-ui.html", "/swagger-ui/**"})
    public ResponseEntity<Object> swaggerDisabled() {
        return ResponseEntity.status(404).body(java.util.Map.of("error", "Swagger is disabled"));
    }
}
