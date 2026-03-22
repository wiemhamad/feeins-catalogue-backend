package com.feeins.catalogue.dto;

import com.feeins.catalogue.entity.Utilisateur;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @Email(message = "Email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @NotBlank
    @Size(min = 6, message = "Mot de passe minimum 6 caractères")
    private String motDePasse;

    private Utilisateur.TypeUtilisateur typeUtilisateur = Utilisateur.TypeUtilisateur.ETUDIANT;

    // Code requis si typeUtilisateur = ENSEIGNANT ou ADMINISTRATEUR_PEDAGOGIQUE
    private String codeAccesEnseignant;

    // Champs spécifiques Enseignant
    private String role;
    private String specialite;
}