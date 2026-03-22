package com.feeins.catalogue.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI openAPI() {
                return new OpenAPI()
                                // ===== INFOS GÉNÉRALES =====
                                .info(new Info()
                                                .title("📚 FEEINS Catalogue API")
                                                .description("""
                                                                ## Catalogue intelligent de grains pédagogiques FEEINS

                                                                ### 🔐 Authentification
                                                                1. Utilisez **POST /api/auth/login** pour obtenir un token JWT
                                                                2. Cliquez sur **Authorize** (🔓) en haut à droite
                                                                3. Entrez : `Bearer <votre_token>`

                                                                ### 👥 Comptes de test disponibles
                                                                | Email | Mot de passe | Rôle |
                                                                |-------|-------------|------|
                                                                | admin@feeins.fr | admin123 | ADMINISTRATEUR_PEDAGOGIQUE |
                                                                | marion@feeins.fr | prof123 | ENSEIGNANT |
                                                                | etudiant@feeins.fr | etudiant123 | ETUDIANT |

                                                                ### 🔑 Code d'accès enseignant
                                                                Pour s'inscrire comme enseignant : **FEEINS2025**
                                                                """)
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("FEEINS - ISIS")
                                                                .email("marion.collaro@univ-jfc.fr"))
                                                .license(new License()
                                                                .name("Projet académique FIE-3 2025-2026")))

                                // ===== SERVEUR =====
                                .servers(List.of(
                                                new Server()
                                                                .url("http://localhost:8080")
                                                                .description("Serveur de développement local")))

                                // ===== SÉCURITÉ JWT =====
                                // Définit le schéma "bearerAuth"
                                .components(new Components()
                                                .addSecuritySchemes("bearerAuth",
                                                                new SecurityScheme()
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")
                                                                                .description("Entrez votre token JWT obtenu via /api/auth/login")))

                                // Applique la sécurité globalement (tous les endpoints protégés)
                                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
        }
}
