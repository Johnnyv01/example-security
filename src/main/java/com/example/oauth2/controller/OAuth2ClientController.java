package com.example.oauth2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth2")
public class OAuth2ClientController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String authServerUrl = "http://localhost:8080";

    @PostMapping("/token")
    public ResponseEntity<Map<String, Object>> getToken(
            @RequestParam String grant_type,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String client_id,
            @RequestParam(required = false) String client_secret,
            @RequestParam(required = false) String scope,
            @RequestParam(required = false) String refresh_token) {
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            // Configurar autenticação do cliente
            if (client_id != null && client_secret != null) {
                String auth = client_id + ":" + client_secret;
                String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
                headers.set("Authorization", "Basic " + encodedAuth);
            }
            
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", grant_type);
            
            if ("password".equals(grant_type)) {
                body.add("username", username != null ? username : "user");
                body.add("password", password != null ? password : "password");
            } else if ("client_credentials".equals(grant_type)) {
                // Para client_credentials, não precisamos de username/password
            } else if ("refresh_token".equals(grant_type)) {
                body.add("refresh_token", refresh_token);
            }
            
            if (scope != null) {
                body.add("scope", scope);
            }
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                authServerUrl + "/oauth2/token", 
                request, 
                Map.class
            );
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("token_response", response.getBody());
            result.put("status", response.getStatusCode());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            error.put("message", "Erro ao obter token OAuth2");
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/client-credentials-example")
    public ResponseEntity<Map<String, Object>> getClientCredentialsToken() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            // Autenticação do cliente
            String auth = "client-app:secret";
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            headers.set("Authorization", "Basic " + encodedAuth);
            
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "client_credentials");
            body.add("scope", "read write");
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(
                authServerUrl + "/oauth2/token", 
                request, 
                Map.class
            );
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", "Token obtido com sucesso usando Client Credentials");
            result.put("token_info", tokenResponse.getBody());
            result.put("usage_example", Map.of(
                "header", "Authorization: Bearer " + ((Map<?, ?>) tokenResponse.getBody()).get("access_token"),
                "api_call", "GET /api/user/profile"
            ));
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("message", "Erro ao obter token com Client Credentials");
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/test-api")
    public ResponseEntity<Map<String, Object>> testApiWithToken(
            @RequestParam String access_token,
            @RequestParam(defaultValue = "/api/user/profile") String endpoint) {
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + access_token);
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            
            ResponseEntity<Map> apiResponse = restTemplate.exchange(
                authServerUrl + endpoint,
                HttpMethod.GET,
                request,
                Map.class
            );
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("endpoint_called", endpoint);
            result.put("api_response", apiResponse.getBody());
            result.put("status", apiResponse.getStatusCode());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            error.put("message", "Erro ao chamar API com token");
            
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/authorization-url")
    public ResponseEntity<Map<String, Object>> getAuthorizationUrl(
            @RequestParam(defaultValue = "client-app") String client_id,
            @RequestParam(defaultValue = "http://localhost:8080/authorized") String redirect_uri,
            @RequestParam(defaultValue = "read write") String scope,
            @RequestParam(defaultValue = "code") String response_type) {
        
        String authUrl = String.format(
            "%s/oauth2/authorize?response_type=%s&client_id=%s&redirect_uri=%s&scope=%s",
            authServerUrl, response_type, client_id, redirect_uri, scope.replace(" ", "%20")
        );
        
        Map<String, Object> result = new HashMap<>();
        result.put("authorization_url", authUrl);
        result.put("instructions", java.util.List.of(
            "1. Acesse a URL de autorização no navegador",
            "2. Faça login com user/password ou admin/admin",
            "3. Autorize o cliente",
            "4. Você será redirecionado com o código de autorização",
            "5. Use o código para obter o token de acesso"
        ));
        result.put("parameters", Map.of(
            "client_id", client_id,
            "redirect_uri", redirect_uri,
            "scope", scope,
            "response_type", response_type
        ));
        
        return ResponseEntity.ok(result);
    }
}