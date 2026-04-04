package com.feeins.catalogue.dto;

import com.feeins.catalogue.entity.RessourcePedagogique;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechercheRequestDTO {

    private String keyword;
    private Long niveauId;
    private Long thematiqueId;
    private RessourcePedagogique.TypeSupport typeSupport;
    private RessourcePedagogique.Difficulte difficulte;
    private Integer dureeMax;
    private String tag;
    // ✅ Ajout du filtre usage pédagogique
    private RessourcePedagogique.UsagePedagogique usagePedagogique;
}