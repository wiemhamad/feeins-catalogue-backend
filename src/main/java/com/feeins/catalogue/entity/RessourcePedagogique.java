package com.feeins.catalogue.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ressources_pedagogiques")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RessourcePedagogique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Durée en minutes
    @Column(name = "duree_minutes")
    private Integer dureeMinutes;

    // Type de support : VIDEO, H5P, PDF, QUIZ, HTML, LIEN
    @Column(name = "type_support", nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeSupport typeSupport;

    // URL d'accès Moodle ou externe
    @Column(name = "url_acces")
    private String urlAcces;

    // Difficulté : DEBUTANT, INTERMEDIAIRE, AVANCE
    @Enumerated(EnumType.STRING)
    private Difficulte difficulte;

    // Objectifs pédagogiques
    @Column(name = "objectifs_pedagogiques", columnDefinition = "TEXT")
    private String objectifsPedagogiques;

    // Compétences visées
    @Column(name = "competences_visees", columnDefinition = "TEXT")
    private String competencesVisees;

    // Nomenclature générée automatiquement (ex: FEEINS-VIDEO-L3-SANTE-001)
    @Column(unique = true)
    private String nomenclature;

    // Statut de validation
    @Enumerated(EnumType.STRING)
    private StatutRessource statut = StatutRessource.EN_ATTENTE;

    // ===== RELATIONS =====

    // Niveau (appartient à)
    @ManyToOne
    @JoinColumn(name = "niveau_id")
    private Niveau niveau;

    // Thématique (concerne)
    @ManyToOne
    @JoinColumn(name = "thematique_id")
    private Thematique thematique;

    // Tags (est décrit par)
    @ManyToMany
    @JoinTable(name = "ressource_tags", joinColumns = @JoinColumn(name = "ressource_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags = new ArrayList<>();

    // Template pédagogique (utilise)
    @ManyToOne
    @JoinColumn(name = "template_id")
    private TemplatePedagogique template;

    // Créateur (Enseignant)
    @ManyToOne
    @JoinColumn(name = "createur_id")
    private Enseignant createur;

    // Contributeur (proposé par)
    @ManyToOne
    @JoinColumn(name = "contributeur_id")
    private Contributeur contributeur;
    // ===== NOUVEAUX CHAMPS =====

    @Enumerated(EnumType.STRING)
    @Column(name = "usage_pedagogique")
    private UsagePedagogique usagePedagogique;

    @Column(name = "usage_moodle", columnDefinition = "TEXT")
    private String usageMoodle; // ex: "SIS-L3-CM1, Master-IA-TD2"

    @Enumerated(EnumType.STRING)
    @Column(name = "droits")
    private Droits droits = Droits.FEEINS_INTERNE;

    @Column(name = "derniere_verification")
    private LocalDate derniereVerification;

    @Column(name = "auteur_partenaire")
    private String auteurPartenaire;

    // ===== NOUVEAUX ENUMS (à ajouter dans la classe) =====

    public enum UsagePedagogique {
        COURS,
        ACTIVITE,
        EVALUATION_FORMATIVE,
        EVALUATION_SOMMATIVE,
        RESSOURCE_COMPLEMENTAIRE,
        QUIZ_POSITIONNEMENT
    }

    public enum Droits {
        LIBRE,
        FEEINS_INTERNE,
        PARTENAIRE,
        SOUS_LICENCE
    }
    // ===== ENUMS =====

    public enum TypeSupport {
        VIDEO, H5P, PDF, QUIZ, HTML, LIEN, AUTRE
    }

    public enum Difficulte {
        DEBUTANT, INTERMEDIAIRE, AVANCE
    }

    public enum StatutRessource {
        EN_ATTENTE, VALIDEE, REFUSEE, ARCHIVEE
    }
}
