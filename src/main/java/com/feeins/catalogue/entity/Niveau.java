package com.feeins.catalogue.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "niveaux")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Niveau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom; // ex: L3, Master, Ingénieur, Débutant, Avancé
}