package com.feeins.catalogue.dto;

import com.feeins.catalogue.entity.Utilisateur;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO d'inscription.
 *
 * Rôles disponibles :
 * - ETUDIANT : aucune authentification requise pour consulter ; mais peut
 * s'inscrire pour un suivi
 * - CONTRIBUTEUR : crée et propose des ressources pédagogiques (consultant
 * pédagogique)
 * - ENSEIGNANT : crée des templates à partir de ressources validées existantes
 * - ADMINISTRATEUR_PEDAGOGIQUE : valide ou refuse les ressources proposées par
 * les contributeurs
 *
 * Accès privilégié (ENSEIGNANT, CONTRIBUTEUR, ADMINISTRATEUR_PEDAGOGIQUE) :
 * nécessite codeAccesEnseignant.
 */
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

    // Par défaut ETUDIANT (pas de compte nécessaire pour consulter, mais
    // disponible)
    private Utilisateur.TypeUtilisateur typeUtilisateur = Utilisateur.TypeUtilisateur.ETUDIANT;

    // Code requis si typeUtilisateur = ENSEIGNANT, CONTRIBUTEUR ou
    // ADMINISTRATEUR_PEDAGOGIQUE
    private String codeAccesEnseignant;

    // Champs spécifiques Enseignant
    private String role;
    private String specialite;

    // Champ spécifique Contributeur (organisation partenaire)
    private String organisation;
}
