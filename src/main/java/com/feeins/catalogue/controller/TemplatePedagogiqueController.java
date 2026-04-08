package com.feeins.catalogue.controller;

import com.feeins.catalogue.dto.RessourceResponseDTO;
import com.feeins.catalogue.entity.Enseignant;
import com.feeins.catalogue.entity.RessourcePedagogique;
import com.feeins.catalogue.entity.TemplatePedagogique;
import com.feeins.catalogue.repository.EnseignantRepository;
import com.feeins.catalogue.repository.RessourcePedagogiqueRepository;
import com.feeins.catalogue.repository.TemplatePedagogiqueRepository;
import com.feeins.catalogue.service.RessourcePedagogiqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/templates")
@CrossOrigin(origins = "*")
@Tag(name = "📋 Templates Pédagogiques", description = "Modèles de scénarios pédagogiques")
public class TemplatePedagogiqueController {

    @Autowired
    private TemplatePedagogiqueRepository templateRepo;
    @Autowired
    private EnseignantRepository enseignantRepo;
    @Autowired
    private RessourcePedagogiqueRepository ressourceRepo;
    @Autowired
    private RessourcePedagogiqueService ressourceService;

    // ===== ACCÈS PUBLIC =====

    @Operation(summary = "Lister les templates (accès public)")
    @GetMapping("/public")
    public List<TemplatePedagogique> listerTemplatesPublic() {
        return templateRepo.findAll();
    }

    /**
     * Ressources associées à un template — PUBLIC
     * Retourne uniquement les ressources VALIDÉES du template
     */
    @Operation(summary = "Ressources d'un template (public)")
    @GetMapping("/{id}/ressources")
    public ResponseEntity<List<RessourceResponseDTO>> getRessourcesTemplate(@PathVariable Long id) {
        return templateRepo.findById(id).map(template -> {
            List<RessourcePedagogique> ressources = ressourceRepo.findByTemplateId(id);
            List<RessourceResponseDTO> dtos = ressources.stream()
                    .filter(r -> r.getStatut() == RessourcePedagogique.StatutRessource.VALIDEE)
                    .map(ressourceService::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        }).orElse(ResponseEntity.notFound().build());
    }

    // ===== LISTER (authentifié) =====

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

    // ===== CRÉER =====

    @Operation(summary = "Créer un template", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<TemplatePedagogique> creerTemplate(@RequestBody TemplatePedagogique template) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        enseignantRepo.findByEmail(email).ifPresent(template::setCreateurTemplate);
        return ResponseEntity.status(HttpStatus.CREATED).body(templateRepo.save(template));
    }

    // ===== MODIFIER =====

    @Operation(summary = "Modifier un template", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<TemplatePedagogique> modifierTemplate(@PathVariable Long id,
            @RequestBody TemplatePedagogique template) {
        return templateRepo.findById(id).map(existing -> {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMINISTRATEUR_PEDAGOGIQUE".equals(a.getAuthority()));
            if (!isAdmin && existing.getCreateurTemplate() != null &&
                    !email.equalsIgnoreCase(existing.getCreateurTemplate().getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).<TemplatePedagogique>build();
            }
            existing.setNom(template.getNom());
            existing.setDescription(template.getDescription());
            existing.setModifiable(template.getModifiable());
            return ResponseEntity.ok(templateRepo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ===== SUPPRIMER =====

    @Operation(summary = "Supprimer un template", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<Void> supprimerTemplate(@PathVariable Long id) {
        templateRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}