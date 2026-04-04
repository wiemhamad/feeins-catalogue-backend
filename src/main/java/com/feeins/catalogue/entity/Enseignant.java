package com.feeins.catalogue.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

/**
 * L'enseignant NE crée PAS de ressources.
 * Il crée des TEMPLATES à partir des ressources existantes.
 * Il peut aussi consulter et rechercher des ressources (comme tout
 * utilisateur).
 */
@Entity
@Table(name = "enseignants")
@DiscriminatorValue("ENSEIGNANT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Enseignant extends Utilisateur {

    @Column
    private String role;

    @Column
    private String specialite;

    // L'enseignant crée des templates à partir de ressources existantes
    @OneToMany(mappedBy = "createurTemplate", cascade = CascadeType.ALL)
    private List<TemplatePedagogique> templatesCreees = new ArrayList<>();
}
