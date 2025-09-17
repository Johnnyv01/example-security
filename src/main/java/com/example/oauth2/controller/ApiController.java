package com.example.oauth2.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/public/info")
    public ResponseEntity<Map<String, Object>> getPublicInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Este é um endpoint público - não requer autenticação");
        response.put("timestamp", LocalDateTime.now());
        response.put("access", "public");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/profile")
    @PreAuthorize("hasAuthority('read')")
    public ResponseEntity<Map<String, Object>> getUserProfile(Authentication authentication) {
        System.out.println("profile Entrou aqui!!!!!!");
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Perfil do usuário - requer scope 'read'");
        response.put("timestamp", LocalDateTime.now());
        response.put("access", "user");
        
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            response.put("user", jwt.getClaimAsString("sub"));
            response.put("scopes", jwt.getClaimAsStringList("scope"));
            response.put("client_id", jwt.getClaimAsString("client_id"));
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/data")
    @PreAuthorize("hasAuthority('read')")
    public ResponseEntity<Map<String, Object>> getUserData(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Dados do usuário - requer scope 'read'");
        response.put("timestamp", LocalDateTime.now());
        response.put("access", "user");
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", 1);
        userData.put("name", "João Silva");
        userData.put("email", "joao@example.com");
        userData.put("role", "USER");
        
        response.put("data", userData);
        
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            response.put("token_info", Map.of(
                "subject", jwt.getClaimAsString("sub"),
                "issued_at", Objects.requireNonNull(jwt.getIssuedAt()),
                "expires_at", Objects.requireNonNull(jwt.getExpiresAt())
            ));
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasAuthority('write')")
    public ResponseEntity<Map<String, Object>> getAdminUsers(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Lista de usuários - requer scope 'write' (admin)");
        response.put("timestamp", LocalDateTime.now());
        response.put("access", "admin");
        
        response.put("users", java.util.List.of(
            Map.of("id", 1, "username", "user", "role", "USER"),
            Map.of("id", 2, "username", "admin", "role", "ADMIN")
        ));
        
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            response.put("admin_info", Map.of(
                "admin_user", jwt.getClaimAsString("sub"),
                "scopes", jwt.getClaimAsStringList("scope"),
                "client_id", jwt.getClaimAsString("client_id")
            ));
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/system")
    @PreAuthorize("hasAuthority('write')")
    public ResponseEntity<Map<String, Object>> getSystemInfo(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Informações do sistema - requer scope 'write' (admin)");
        response.put("timestamp", LocalDateTime.now());
        response.put("access", "admin");
        
        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("version", "1.0.0");
        systemInfo.put("environment", "development");
        systemInfo.put("database", "H2");
        systemInfo.put("oauth2_enabled", true);
        
        response.put("system", systemInfo);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/protected/info")
    public ResponseEntity<Map<String, Object>> getProtectedInfo(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Endpoint protegido - requer token válido");
        response.put("timestamp", LocalDateTime.now());
        response.put("access", "protected");
        
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            response.put("token_details", Map.of(
                "subject", jwt.getClaimAsString("sub"),
                "audience", jwt.getAudience(),
                "issuer", jwt.getIssuer(),
                "scopes", jwt.getClaimAsStringList("scope"),
                "client_id", jwt.getClaimAsString("client_id"),
                "issued_at", jwt.getIssuedAt(),
                "expires_at", jwt.getExpiresAt()
            ));
        }
        
        return ResponseEntity.ok(response);
    }
}
