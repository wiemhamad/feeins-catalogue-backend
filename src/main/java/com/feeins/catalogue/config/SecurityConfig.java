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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // Liste complète des URLs Swagger à autoriser
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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                // ✅ Désactiver la page de login Spring Security par défaut
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ✅ Swagger - DOIT être en premier
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()

                        // Auth publique
                        .requestMatchers("/api/auth/**").permitAll()

                        // Console H2
                        .requestMatchers("/h2-console/**").permitAll()

                        // Routes publiques API
                        .requestMatchers("/api/ressources").permitAll()
                        .requestMatchers("/api/ressources/rechercher").permitAll()
                        .requestMatchers("/api/ressources/{id}").permitAll()
                        .requestMatchers("/api/niveaux/**").permitAll()
                        .requestMatchers("/api/thematiques/**").permitAll()
                        .requestMatchers("/api/tags/**").permitAll()

                        // Routes réservées aux ENSEIGNANTS et ADMINS
                        .requestMatchers("/api/ressources/creer").hasAnyRole("ENSEIGNANT", "ADMINISTRATEUR_PEDAGOGIQUE")
                        .requestMatchers("/api/ressources/*/modifier")
                        .hasAnyRole("ENSEIGNANT", "ADMINISTRATEUR_PEDAGOGIQUE")
                        .requestMatchers("/api/ressources/*/supprimer").hasRole("ADMINISTRATEUR_PEDAGOGIQUE")
                        .requestMatchers("/api/ressources/*/valider").hasRole("ADMINISTRATEUR_PEDAGOGIQUE")
                        .requestMatchers("/api/templates/**").hasAnyRole("ENSEIGNANT", "ADMINISTRATEUR_PEDAGOGIQUE")
                        .requestMatchers("/api/admin/**").hasRole("ADMINISTRATEUR_PEDAGOGIQUE")

                        // Toutes les autres routes nécessitent une authentification
                        .anyRequest().authenticated());

        // Désactiver le frameOptions pour la console H2
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}