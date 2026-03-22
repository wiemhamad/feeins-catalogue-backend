package com.feeins.catalogue.controller;

import com.feeins.catalogue.entity.Niveau;
import com.feeins.catalogue.repository.NiveauRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/niveaux")
@CrossOrigin(origins = "*")
@Tag(name = "🎓 Niveaux", description = "Référentiel des niveaux pédagogiques (L3, Master, Ingénieur...)")
public class NiveauController {

    @Autowired
    private NiveauRepository niveauRepo;

    @Operation(summary = "Lister tous les niveaux", description = "Accessible sans authentification.")
    @GetMapping
    public List<Niveau> listerNiveaux() {
        return niveauRepo.findAll();
    }

    @Operation(summary = "Créer un niveau", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<Niveau> creerNiveau(@RequestBody Niveau niveau) {
        return ResponseEntity.status(HttpStatus.CREATED).body(niveauRepo.save(niveau));
    }

    @Operation(summary = "Supprimer un niveau", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<Void> supprimerNiveau(@PathVariable Long id) {
        niveauRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}