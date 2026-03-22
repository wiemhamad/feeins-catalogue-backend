package com.feeins.catalogue.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "administrateurs_pedagogiques")
@DiscriminatorValue("ADMINISTRATEUR_PEDAGOGIQUE")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AdministrateurPedagogique extends Utilisateur {
    // Peut valider les ressources proposées par les contributeurs
}
