package com.feeins.catalogue.controller;

import com.feeins.catalogue.entity.Tag;
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
@io.swagger.v3.oas.annotations.tags.Tag(name = "🏷️ Tags")
public class TagController {

    @Autowired
    private TagRepository tagRepo;

    @GetMapping
    public List<Tag> listerTags() {
        return tagRepo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tag> getTag(@PathVariable Long id) {
        return tagRepo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Créer un tag", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    @PreAuthorize("hasAnyRole('CONTRIBUTEUR', 'ENSEIGNANT', 'ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<Tag> creerTag(@RequestBody Tag tag) {
        if (tagRepo.existsByLibelle(tag.getLibelle()))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(tagRepo.save(tag));
    }

    @Operation(summary = "Modifier un tag", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<Tag> modifierTag(@PathVariable Long id, @RequestBody Tag tag) {
        return tagRepo.findById(id).map(existing -> {
            existing.setLibelle(tag.getLibelle());
            return ResponseEntity.ok(tagRepo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Supprimer un tag", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<Void> supprimerTag(@PathVariable Long id) {
        tagRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}