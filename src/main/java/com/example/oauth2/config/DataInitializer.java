package com.example.oauth2.config;

import com.example.oauth2.entity.ERole;
import com.example.oauth2.entity.OAuth2Client;
import com.example.oauth2.entity.Role;
import com.example.oauth2.entity.User;
import com.example.oauth2.repository.OAuth2ClientRepository;
import com.example.oauth2.repository.RoleRepository;
import com.example.oauth2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private OAuth2ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Inicializar roles
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(ERole.ROLE_USER));
            roleRepository.save(new Role(ERole.ROLE_ADMIN));
            roleRepository.save(new Role(ERole.ROLE_MODERATOR));
            System.out.println("✅ Roles inicializadas");
        }

        // Inicializar usuários
        if (userRepository.count() == 0) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Role USER não encontrada"));
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Role ADMIN não encontrada"));

            // Usuário comum
            User user = new User("user", "user@example.com", passwordEncoder.encode("password"));
            user.setRoles(Set.of(userRole));
            userRepository.save(user);

            // Usuário admin
            User admin = new User("admin", "admin@example.com", passwordEncoder.encode("admin"));
            admin.setRoles(Set.of(userRole, adminRole));
            userRepository.save(admin);

            System.out.println("✅ Usuários inicializados:");
            System.out.println("   - user/password (ROLE_USER)");
            System.out.println("   - admin/admin (ROLE_USER, ROLE_ADMIN)");
        }

        // Inicializar clientes OAuth2
        if (clientRepository.count() == 0) {
            // Cliente confidencial
            OAuth2Client confidentialClient = new OAuth2Client(
                    "client-app", 
                    passwordEncoder.encode("secret"), 
                    "Aplicação Cliente Confidencial"
            );
            confidentialClient.setRedirectUris(Set.of(
                    "http://localhost:8080/login/oauth2/code/client-app",
                    "http://localhost:8080/authorized"
            ));
            confidentialClient.setScopes(Set.of("openid", "profile", "read", "write"));
            confidentialClient.setGrantTypes(Set.of(
                    "authorization_code", 
                    "refresh_token", 
                    "client_credentials"
            ));
            confidentialClient.setRequireConsent(true);
            confidentialClient.setAccessTokenValidity(3600); // 1 hora
            confidentialClient.setRefreshTokenValidity(86400); // 24 horas
            clientRepository.save(confidentialClient);

            // Cliente público
            OAuth2Client publicClient = new OAuth2Client(
                    "public-client", 
                    null, // Sem secret para cliente público
                    "Aplicação Cliente Pública"
            );
            publicClient.setRedirectUris(Set.of(
                    "http://localhost:8080/login/oauth2/code/public-client"
            ));
            publicClient.setScopes(Set.of("openid", "profile", "read"));
            publicClient.setGrantTypes(Set.of("authorization_code", "refresh_token"));
            publicClient.setRequireConsent(true);
            publicClient.setAccessTokenValidity(1800); // 30 minutos
            publicClient.setRefreshTokenValidity(43200); // 12 horas
            clientRepository.save(publicClient);

            System.out.println("✅ Clientes OAuth2 inicializados:");
            System.out.println("   - client-app/secret (confidencial)");
            System.out.println("   - public-client (público)");
        }

        System.out.println("\n🚀 Aplicação OAuth2 inicializada com sucesso!");
        System.out.println("📍 Acesse: http://localhost:8080");
        System.out.println("🗄️  H2 Console: http://localhost:8080/h2-console");
        System.out.println("   - JDBC URL: jdbc:h2:mem:oauth2db");
        System.out.println("   - Username: sa");
        System.out.println("   - Password: password");
    }
}
