package com.feeins.catalogue.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplatePublicDTO {
    private Long id;
    private String nom;
    private String description;
    private Boolean modifiable;
    private String createurNom;
    private int nbRessources;
    private List<RessourceResumeeDTO> ressources;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RessourceResumeeDTO {
        private Long id;
        private String titre;
        private String typeSupport;
        private String difficulte;
        private Integer dureeMinutes;
    }
}