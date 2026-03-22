package com.feeins.catalogue.entity;

import jakarta.persistence.*;
import lombok.*;

// ========== ETUDIANT ==========
@Entity
@Table(name = "etudiants")
@DiscriminatorValue("ETUDIANT")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Etudiant extends Utilisateur {
    // Hérite de Utilisateur
    // Peut consulter et rechercher des ressources
}
