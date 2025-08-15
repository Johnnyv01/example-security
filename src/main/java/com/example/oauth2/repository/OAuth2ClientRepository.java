package com.example.oauth2.repository;

import com.example.oauth2.entity.OAuth2Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuth2ClientRepository extends JpaRepository<OAuth2Client, Long> {
    Optional<OAuth2Client> findByClientId(String clientId);
    Boolean existsByClientId(String clientId);
}