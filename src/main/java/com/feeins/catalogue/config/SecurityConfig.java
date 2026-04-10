package com.feeins.catalogue.config;

import com.feeins.catalogue.security.JwtAuthenticationFilter;
import com.feeins.catalogue.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration de sécurité FEEINS.
 *
 * Règles d'accès par rôle :
 *
 * PUBLIC (sans authentification) :
 * - GET /api/ressources → lister les ressources validées
 * - GET /api/ressources/{id} → consulter une ressource
 * - POST /api/ressources/rechercher → recherche multicritères
 * - GET /api/niveaux, /api/thematiques, /api/tags
 *
 * CONTRIBUTEUR (consultant pédagogique) :
 * - POST /api/ressources/creer → proposer une ressource
 * - PUT /api/ressources/{id}/modifier → modifier ses ressources
 * - GET /api/ressources/mes-ressources → voir ses ressources
 * - POST/DELETE /api/ressources/{id}/tags/{tagId}
 *
 * ENSEIGNANT :
 * - GET/POST/PUT /api/templates/** → créer et gérer des templates
 * (à partir de ressources validées existantes)
 *
 * ADMINISTRATEUR_PEDAGOGIQUE :
 * - POST /api/ressources/{id}/valider → valider une ressource
 * - POST /api/ressources/{id}/refuser → refuser une ressource
 * - GET /api/ressources/toutes → voir toutes les ressources
 * - DELETE /api/ressources/{id}/supprimer
 * - PUT /api/ressources/{id}/verifier
 * - DELETE /api/templates/{id}
 * - Tout ce que font Contributeur et Enseignant
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

        @Autowired
        private UserDetailsServiceImpl userDetailsService;

        @Value("${app.cors.allowed-origins:*}")
        private String allowedOrigins;

        private static final String[] SWAGGER_WHITELIST = {
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/favicon.ico"
        };

        @Bean
        public JwtAuthenticationFilter jwtAuthenticationFilter() {
                return new JwtAuthenticationFilter();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration config = new CorsConfiguration();

                if ("*".equals(allowedOrigins.trim())) {
                        config.addAllowedOriginPattern("*");
                        config.setAllowCredentials(false);
                } else {
                        config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
                        config.setAllowCredentials(true);
                }

                config.setAllowedMethods(Arrays.asList(
                                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                config.setAllowedHeaders(List.of("*"));
                config.addExposedHeader("Authorization");

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);
                return source;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())
                                .formLogin(form -> form.disable())
                                .httpBasic(basic -> basic.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth

                                                // Swagger
                                                .requestMatchers(SWAGGER_WHITELIST).permitAll()

                                                // Auth publique (login / register)
                                                .requestMatchers("/api/auth/**").permitAll()

                                                // Console H2
                                                .requestMatchers("/h2-console/**").permitAll()

                                                // =====================================================
                                                // ACCÈS PUBLIC — l'étudiant (et tout visiteur) consulte
                                                // sans avoir besoin de s'authentifier
                                                // =====================================================
                                                .requestMatchers(org.springframework.http.HttpMethod.GET,
                                                                "/api/ressources")
                                                .permitAll()
                                                .requestMatchers(org.springframework.http.HttpMethod.GET,
                                                                "/api/ressources/{id}")
                                                .permitAll()
                                                .requestMatchers(org.springframework.http.HttpMethod.POST,
                                                                "/api/ressources/rechercher")
                                                .permitAll()
                                                .requestMatchers("/api/niveaux/**").permitAll()
                                                .requestMatchers("/api/thematiques/**").permitAll()
                                                .requestMatchers("/api/tags/**").permitAll()

                                                // Routes publiques templates (étudiants/visiteurs)
                                                // DOIT être avant la règle templates/** authentifiée
                                                .requestMatchers(org.springframework.http.HttpMethod.GET,
                                                                "/api/templates/public")
                                                .permitAll()
                                                .requestMatchers(org.springframework.http.HttpMethod.GET,
                                                                "/api/templates/public/**")
                                                .permitAll()

                                                // =====================================================
                                                // CONTRIBUTEUR — crée et gère ses ressources
                                                // =====================================================
                                                .requestMatchers("/api/ressources/creer")
                                                .hasAnyRole("CONTRIBUTEUR", "ADMINISTRATEUR_PEDAGOGIQUE")
                                                .requestMatchers("/api/ressources/mes-ressources")
                                                .hasAnyRole("CONTRIBUTEUR", "ADMINISTRATEUR_PEDAGOGIQUE")
                                                .requestMatchers("/api/ressources/*/modifier")
                                                .hasAnyRole("CONTRIBUTEUR", "ADMINISTRATEUR_PEDAGOGIQUE")
                                                .requestMatchers("/api/ressources/*/visibilite")
                                                .hasAnyRole("CONTRIBUTEUR", "ADMINISTRATEUR_PEDAGOGIQUE")
                                                .requestMatchers("/api/ressources/*/tags/**")
                                                .hasAnyRole("CONTRIBUTEUR", "ADMINISTRATEUR_PEDAGOGIQUE")

                                                // =====================================================
                                                // ENSEIGNANT — crée et gère des templates
                                                // =====================================================
                                                .requestMatchers("/api/templates/**")
                                                .hasAnyRole("ENSEIGNANT", "ADMINISTRATEUR_PEDAGOGIQUE")

                                                // =====================================================
                                                // ADMINISTRATEUR_PEDAGOGIQUE — valide/refuse/administre
                                                // =====================================================
                                                .requestMatchers("/api/ressources/toutes")
                                                .hasRole("ADMINISTRATEUR_PEDAGOGIQUE")
                                                .requestMatchers("/api/ressources/*/valider")
                                                .hasRole("ADMINISTRATEUR_PEDAGOGIQUE")
                                                .requestMatchers("/api/ressources/*/refuser")
                                                .hasRole("ADMINISTRATEUR_PEDAGOGIQUE")
                                                .requestMatchers("/api/ressources/*/supprimer")
                                                .hasRole("ADMINISTRATEUR_PEDAGOGIQUE")
                                                .requestMatchers("/api/ressources/*/verifier")
                                                .hasRole("ADMINISTRATEUR_PEDAGOGIQUE")
                                                .requestMatchers("/api/ressources/alertes/**")
                                                .hasRole("ADMINISTRATEUR_PEDAGOGIQUE")
                                                .requestMatchers("/api/admin/**")
                                                .hasRole("ADMINISTRATEUR_PEDAGOGIQUE")

                                                // Toutes les autres routes nécessitent une authentification
                                                .anyRequest().authenticated());

                http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
                http.addFilterBefore(jwtAuthenticationFilter(),
                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}