package com.feeins.catalogue.controller;

import com.feeins.catalogue.entity.Thematique;
import com.feeins.catalogue.repository.ThematiqueRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/thematiques")
@CrossOrigin(origins = "*")
@Tag(name = "📂 Thématiques", description = "Référentiel des thématiques pédagogiques")
public class ThematiqueController {

    @Autowired
    private ThematiqueRepository thematiqueRepo;

    @Operation(summary = "Lister toutes les thématiques", description = "Accessible sans authentification.")
    @GetMapping
    public List<Thematique> listerThematiques() {
        return thematiqueRepo.findAll();
    }

    @Operation(summary = "Récupérer une thématique par ID")
    @GetMapping("/{id}")
    public ResponseEntity<Thematique> getThematique(@PathVariable Long id) {
        return thematiqueRepo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Créer une thématique", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<Thematique> creerThematique(@RequestBody Thematique thematique) {
        if (thematiqueRepo.existsByNom(thematique.getNom()))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(thematiqueRepo.save(thematique));
    }

    @Operation(summary = "Modifier une thématique", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<Thematique> modifierThematique(@PathVariable Long id,
            @RequestBody Thematique thematique) {
        return thematiqueRepo.findById(id).map(existing -> {
            existing.setNom(thematique.getNom());
            return ResponseEntity.ok(thematiqueRepo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Supprimer une thématique", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<Void> supprimerThematique(@PathVariable Long id) {
        thematiqueRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}