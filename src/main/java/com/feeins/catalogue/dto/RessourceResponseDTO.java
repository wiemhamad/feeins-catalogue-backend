package com.feeins.catalogue.dto;

import com.feeins.catalogue.entity.RessourcePedagogique;
import lombok.*;

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
    private RessourcePedagogique.StatutRessource statut;

    // Relations simplifiées
    private String niveauNom;
    private String thematiqueNom;
    private List<String> tags;
    private String templateNom;
    private String createurNom;
}