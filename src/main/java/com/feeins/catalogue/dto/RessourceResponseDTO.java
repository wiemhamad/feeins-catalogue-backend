package com.feeins.catalogue.dto;

import com.feeins.catalogue.entity.RessourcePedagogique;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RessourceResponseDTO {
    private Long id;
    private String titre;
    private String description;
    private Integer dureeMinutes;
    private RessourcePedagogique.TypeSupport typeSupport;
    private String urlAcces;
    private RessourcePedagogique.Difficulte difficulte;
    private String objectifsPedagogiques;
    private String competencesVisees;
    private String nomenclature;
    private LocalDateTime dateCreation;
    private RessourcePedagogique.StatutRessource statut;
    private Boolean visible;

    // Relations simplifiées
    private String niveauNom;
    private String thematiqueNom;
    private List<String> tags;
    private String templateNom;

    // Créateur = Contributeur (consultant pédagogique)
    private String contributeurNom;

    // Métadonnées
    private RessourcePedagogique.UsagePedagogique usagePedagogique;
    private RessourcePedagogique.Droits droits;
    private String usageMoodle;
    private String auteurPartenaire;
    private LocalDate derniereVerification;
}
