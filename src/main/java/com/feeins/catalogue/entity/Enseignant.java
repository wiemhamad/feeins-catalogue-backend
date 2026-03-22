package com.feeins.catalogue.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.*;

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

    @OneToMany(mappedBy = "createur", cascade = CascadeType.ALL)
    private List<RessourcePedagogique> ressourcesCreees = new ArrayList<>();
}
