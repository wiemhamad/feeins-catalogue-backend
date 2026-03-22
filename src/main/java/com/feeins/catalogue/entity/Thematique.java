package com.feeins.catalogue.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "thematiques")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Thematique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom; // ex: Santé numérique, IA, RGPD, ...
}
