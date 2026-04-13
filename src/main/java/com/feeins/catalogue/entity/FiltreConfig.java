package com.feeins.catalogue.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Configuration des filtres visibles dans le catalogue.
 * Permet à l'admin d'activer/désactiver les types de contenu et difficultés.
 */
@Entity
@Table(name = "filtre_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiltreConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // "TYPE_SUPPORT" ou "DIFFICULTE"
    @Column(nullable = false)
    private String categorie;

    // La valeur : "VIDEO", "H5P", "DEBUTANT", etc.
    @Column(nullable = false)
    private String valeur;

    // Le libellé affiché : "🎥 Vidéo", "🟢 Débutant"
    @Column(nullable = false)
    private String libelle;

    // Actif = visible dans les filtres du catalogue
    @Column(nullable = false)
    private Boolean actif = true;

    // Ordre d'affichage
    @Column(nullable = false)
    private Integer ordre = 0;
}