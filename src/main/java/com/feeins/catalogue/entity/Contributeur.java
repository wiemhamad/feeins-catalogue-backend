package com.feeins.catalogue.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contributeurs")
@DiscriminatorValue("CONTRIBUTEUR")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Contributeur extends Utilisateur {

    @OneToMany(mappedBy = "contributeur", cascade = CascadeType.ALL)
    private List<RessourcePedagogique> ressourcesProposees = new ArrayList<>();
}