package com.feeins.catalogue.controller;

import com.feeins.catalogue.entity.FiltreConfig;
import com.feeins.catalogue.repository.FiltreConfigRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/filtres-config")
@CrossOrigin(origins = "*")
@Tag(name = "⚙️ Configuration des filtres")
public class FiltreConfigController {

    @Autowired
    private FiltreConfigRepository filtreRepo;

    // PUBLIC — catalogue charge les filtres actifs
    @Operation(summary = "Filtres actifs par catégorie — public")
    @GetMapping("/actifs/{categorie}")
    public List<FiltreConfig> filtresActifs(@PathVariable String categorie) {
        return filtreRepo.findByCategorieAndActifTrueOrderByOrdreAsc(categorie.toUpperCase());
    }

    // PUBLIC — tous les filtres d'une catégorie
    @GetMapping("/{categorie}")
    public List<FiltreConfig> tousLesFiltres(@PathVariable String categorie) {
        return filtreRepo.findByCategorieOrderByOrdreAsc(categorie.toUpperCase());
    }

    // ADMIN — voir tout
    @Operation(summary = "Tous les filtres (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR_PEDAGOGIQUE')")
    public List<FiltreConfig> tousLesFiltresAdmin() {
        return filtreRepo.findAll();
    }

    // ADMIN — activer/désactiver
    @Operation(summary = "Activer ou désactiver un filtre", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<FiltreConfig> toggleFiltre(@PathVariable Long id) {
        return filtreRepo.findById(id).map(f -> {
            f.setActif(!f.getActif());
            return ResponseEntity.ok(filtreRepo.save(f));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ADMIN — modifier le libellé
    @Operation(summary = "Modifier le libellé d'un filtre", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<FiltreConfig> modifierFiltre(@PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return filtreRepo.findById(id).map(f -> {
            if (body.containsKey("libelle"))
                f.setLibelle(body.get("libelle"));
            return ResponseEntity.ok(filtreRepo.save(f));
        }).orElse(ResponseEntity.notFound().build());
    }
}