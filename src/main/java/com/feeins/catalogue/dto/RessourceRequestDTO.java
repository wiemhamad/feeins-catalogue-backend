package com.feeins.catalogue.dto;

import com.feeins.catalogue.entity.RessourcePedagogique;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RessourceRequestDTO {

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    private String description;

    @Min(value = 1, message = "La durée doit être positive")
    private Integer dureeMinutes;

    @NotNull(message = "Le type de support est obligatoire")
    private RessourcePedagogique.TypeSupport typeSupport;

    private String urlAcces;
    private RessourcePedagogique.Difficulte difficulte;
    private String objectifsPedagogiques;
    private String competencesVisees;
    private Long niveauId;
    private Long thematiqueId;
    private List<Long> tagIds;
    private Long templateId;
}