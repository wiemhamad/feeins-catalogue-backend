package com.feeins.catalogue.controller;

import com.feeins.catalogue.dto.*;
import com.feeins.catalogue.service.AuthService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "🔐 Authentification", description = "Connexion et inscription des utilisateurs")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "Se connecter", description = """
            Retourne un token JWT à utiliser dans le header : `Authorization: Bearer <token>`

            **Comptes de test :**
            - `admin@feeins.fr` / `admin123` → ADMINISTRATEUR_PEDAGOGIQUE
            - `marion@feeins.fr` / `prof123` → ENSEIGNANT
            - `etudiant@feeins.fr` / `etudiant123` → ETUDIANT
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Connexion réussie - token JWT retourné"),
            @ApiResponse(responseCode = "401", description = "Email ou mot de passe incorrect")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Email ou mot de passe incorrect"));
        }
    }

    @Operation(summary = "S'inscrire", description = """
            Crée un nouveau compte.

            **Pour s'inscrire comme ENSEIGNANT ou ADMINISTRATEUR_PEDAGOGIQUE :**
            le champ `codeAccesEnseignant` doit valoir `FEEINS2025`

            **Types disponibles :** `ETUDIANT`, `ENSEIGNANT`, `CONTRIBUTEUR`, `ADMINISTRATEUR_PEDAGOGIQUE`
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Compte créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Email déjà utilisé ou code d'accès invalide")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    record ErrorResponse(String message) {
    }
}