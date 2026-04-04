package com.feeins.catalogue.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Template pédagogique créé par un Enseignant,
 * à partir de ressources existantes déjà validées.
 */
@Entity
@Table(name = "templates_pedagogiques")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplatePedagogique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom; // ex: "Capsule vidéo seule", "Vidéo + Quiz", "Cours 30 min"

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean modifiable = true; // true = modifiable, false = clé en main

    // L'enseignant qui a créé ce template
    @ManyToOne
    @JoinColumn(name = "createur_template_id")
    private Enseignant createurTemplate;

    // Ressources associées à ce template
    @OneToMany(mappedBy = "template")
    private List<RessourcePedagogique> ressources = new ArrayList<>();
}
