package com.example.oauth2.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "oauth2_clients")
public class OAuth2Client {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    @Column(name = "client_id", unique = true)
    private String clientId;
    
    @Size(max = 200)
    @Column(name = "client_secret")
    private String clientSecret;
    
    @NotBlank
    @Size(max = 100)
    @Column(name = "client_name")
    private String clientName;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "oauth2_client_redirect_uris", joinColumns = @JoinColumn(name = "client_id"))
    @Column(name = "redirect_uri")
    private Set<String> redirectUris;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "oauth2_client_scopes", joinColumns = @JoinColumn(name = "client_id"))
    @Column(name = "scope")
    private Set<String> scopes;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "oauth2_client_grant_types", joinColumns = @JoinColumn(name = "client_id"))
    @Column(name = "grant_type")
    private Set<String> grantTypes;
    
    @Column(name = "access_token_validity")
    private Integer accessTokenValidity = 3600; // 1 hour
    
    @Column(name = "refresh_token_validity")
    private Integer refreshTokenValidity = 86400; // 24 hours
    
    @Column(name = "require_consent")
    private Boolean requireConsent = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public OAuth2Client() {}
    
    public OAuth2Client(String clientId, String clientSecret, String clientName) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.clientName = clientName;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public String getClientSecret() {
        return clientSecret;
    }
    
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
    
    public String getClientName() {
        return clientName;
    }
    
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    
    public Set<String> getRedirectUris() {
        return redirectUris;
    }
    
    public void setRedirectUris(Set<String> redirectUris) {
        this.redirectUris = redirectUris;
    }
    
    public Set<String> getScopes() {
        return scopes;
    }
    
    public void setScopes(Set<String> scopes) {
        this.scopes = scopes;
    }
    
    public Set<String> getGrantTypes() {
        return grantTypes;
    }
    
    public void setGrantTypes(Set<String> grantTypes) {
        this.grantTypes = grantTypes;
    }
    
    public Integer getAccessTokenValidity() {
        return accessTokenValidity;
    }
    
    public void setAccessTokenValidity(Integer accessTokenValidity) {
        this.accessTokenValidity = accessTokenValidity;
    }
    
    public Integer getRefreshTokenValidity() {
        return refreshTokenValidity;
    }
    
    public void setRefreshTokenValidity(Integer refreshTokenValidity) {
        this.refreshTokenValidity = refreshTokenValidity;
    }
    
    public Boolean getRequireConsent() {
        return requireConsent;
    }
    
    public void setRequireConsent(Boolean requireConsent) {
        this.requireConsent = requireConsent;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}