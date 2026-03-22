package com.feeins.catalogue.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "utilisateurs")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type_utilisateur")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_utilisateur", insertable = false, updatable = false)
    private TypeUtilisateur typeUtilisateur;

    public enum TypeUtilisateur {
        ENSEIGNANT, ETUDIANT, CONTRIBUTEUR, ADMINISTRATEUR_PEDAGOGIQUE, CONSULTANT_EXTERNE
    }
}