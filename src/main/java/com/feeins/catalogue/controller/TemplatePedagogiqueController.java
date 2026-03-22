package com.feeins.catalogue.controller;

import com.feeins.catalogue.entity.TemplatePedagogique;
import com.feeins.catalogue.repository.TemplatePedagogiqueRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
@CrossOrigin(origins = "*")
@Tag(name = "📋 Templates Pédagogiques", description = "Modèles de scénarios pédagogiques (modifiable / clé en main)")
public class TemplatePedagogiqueController {

    @Autowired
    private TemplatePedagogiqueRepository templateRepo;

    @Operation(summary = "Lister tous les templates", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMINISTRATEUR_PEDAGOGIQUE')")
    public List<TemplatePedagogique> listerTemplates() {
        return templateRepo.findAll();
    }

    @Operation(summary = "Récupérer un template par ID", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<TemplatePedagogique> getTemplate(@PathVariable Long id) {
        return templateRepo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Lister les templates modifiables", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/modifiables")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMINISTRATEUR_PEDAGOGIQUE')")
    public List<TemplatePedagogique> listerModifiables() {
        return templateRepo.findByModifiable(true);
    }

    @Operation(summary = "Lister les templates clé en main (non modifiables)", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/cle-en-main")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMINISTRATEUR_PEDAGOGIQUE')")
    public List<TemplatePedagogique> listerCleEnMain() {
        return templateRepo.findByModifiable(false);
    }

    @Operation(summary = "Créer un template", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<TemplatePedagogique> creerTemplate(@RequestBody TemplatePedagogique template) {
        return ResponseEntity.status(HttpStatus.CREATED).body(templateRepo.save(template));
    }

    @Operation(summary = "Modifier un template", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<TemplatePedagogique> modifierTemplate(@PathVariable Long id,
            @RequestBody TemplatePedagogique template) {
        return templateRepo.findById(id).map(existing -> {
            existing.setNom(template.getNom());
            existing.setDescription(template.getDescription());
            existing.setModifiable(template.getModifiable());
            return ResponseEntity.ok(templateRepo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Supprimer un template", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<Void> supprimerTemplate(@PathVariable Long id) {
        templateRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}