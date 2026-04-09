package com.feeins.catalogue.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssocierRessourcesDTO {
    private List<Long> ressourceIds;
}