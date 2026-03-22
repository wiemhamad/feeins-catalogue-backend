package com.feeins.catalogue.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "consultants_externes")
@DiscriminatorValue("CONSULTANT_EXTERNE")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ConsultantExterne extends Utilisateur {
    // Accès en lecture seule au catalogue
}