package com.feeins.catalogue.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Le Contributeur (consultant pédagogique) est celui qui CRÉE et PROPOSE des
 * ressources.
 * Ces ressources sont ensuite validées ou refusées par
 * l'AdministrateurPedagogique.
 */
@Entity
@Table(name = "contributeurs")
@DiscriminatorValue("CONTRIBUTEUR")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Contributeur extends Utilisateur {

    @Column
    private String organisation; // ex : hôpital, université partenaire, etc.

    // Le contributeur propose des ressources pédagogiques
    @OneToMany(mappedBy = "contributeur", cascade = CascadeType.ALL)
    private List<RessourcePedagogique> ressourcesProposees = new ArrayList<>();
}
