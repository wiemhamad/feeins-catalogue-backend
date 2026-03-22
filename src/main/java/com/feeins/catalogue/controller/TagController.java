package com.feeins.catalogue.controller;

import com.feeins.catalogue.repository.TagRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = "*")
@io.swagger.v3.oas.annotations.tags.Tag(name = "🏷️ Tags", description = "Référentiel des tags pour décrire les ressources")
public class TagController {

    @Autowired
    private TagRepository tagRepo;

    @Operation(summary = "Lister tous les tags", description = "Accessible sans authentification.")
    @GetMapping
    public List<com.feeins.catalogue.entity.Tag> listerTags() {
        return tagRepo.findAll();
    }

    @Operation(summary = "Récupérer un tag par ID")
    @GetMapping("/{id}")
    public ResponseEntity<com.feeins.catalogue.entity.Tag> getTag(@PathVariable Long id) {
        return tagRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Créer un tag", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<com.feeins.catalogue.entity.Tag> creerTag(
            @RequestBody com.feeins.catalogue.entity.Tag tag) {
        if (tagRepo.existsByLibelle(tag.getLibelle()))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(tagRepo.save(tag));
    }

    @Operation(summary = "Supprimer un tag", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<Void> supprimerTag(@PathVariable Long id) {
        tagRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}