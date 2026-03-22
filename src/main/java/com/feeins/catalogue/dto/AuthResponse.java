package com.feeins.catalogue.dto;

import com.feeins.catalogue.entity.Utilisateur;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String nom;
    private String email;
    private Utilisateur.TypeUtilisateur typeUtilisateur;
}