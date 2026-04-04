package com.feeins.catalogue.dto;

import com.feeins.catalogue.entity.RessourcePedagogique;
import lombok.*;

import java.util.List;

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

    /**
     * Recherche par un seul tag (libellé exact) — conservé pour compatibilité
     * ascendante.
     * 
     * @deprecated Utiliser {@link #tags} à la place.
     */
    @Deprecated
    private String tag;

    /**
     * Recherche multi-tags : retourne les ressources qui possèdent AU MOINS
     * un des tags listés (logique OR). Si null ou vide, aucun filtre sur les tags.
     */
    private List<String> tags;

    private RessourcePedagogique.UsagePedagogique usagePedagogique;
}