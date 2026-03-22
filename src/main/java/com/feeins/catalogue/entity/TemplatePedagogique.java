package com.feeins.catalogue.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "template")
    private List<RessourcePedagogique> ressources = new ArrayList<>();
}
