package com.feeins.catalogue.controller;

import com.feeins.catalogue.entity.Enseignant;
import com.feeins.catalogue.entity.TemplatePedagogique;
import com.feeins.catalogue.repository.EnseignantRepository;
import com.feeins.catalogue.repository.TemplatePedagogiqueRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Gestion des templates pédagogiques.
 *
 * Qui fait quoi :
 * - ENSEIGNANT : crée, modifie et consulte les templates (à partir de
 * ressources existantes validées)
 * - ADMINISTRATEUR_PEDAGOGIQUE : peut tout faire (créer, modifier, supprimer)
 * - Les autres rôles n'ont pas accès aux templates
 */
@RestController
@RequestMapping("/api/templates")
@CrossOrigin(origins = "*")
@Tag(name = "📋 Templates Pédagogiques", description = "Modèles de scénarios pédagogiques créés par les enseignants à partir de ressources existantes")
public class TemplatePedagogiqueController {

    @Autowired
    private TemplatePedagogiqueRepository templateRepo;

    @Autowired
    private EnseignantRepository enseignantRepo;

    // ===== ACCÈS PUBLIC — étudiants et visiteurs =====

    @Operation(summary = "Lister les templates (accès public)", description = "Accessible sans authentification. Retourne tous les templates avec leurs ressources associées.")
    @GetMapping("/public")
    public List<TemplatePedagogique> listerTemplatesPublic() {
        return templateRepo.findAll();
    }

    // ===== LISTER =====

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
        return templateRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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

    // ===== CRÉER (par l'Enseignant) =====

    @Operation(summary = "Créer un template pédagogique", description = "L'ENSEIGNANT crée un template à partir de ressources validées existantes. "
            +
            "Le template est automatiquement associé à l'enseignant connecté.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<TemplatePedagogique> creerTemplate(@RequestBody TemplatePedagogique template) {
        // Associer l'enseignant connecté comme créateur du template
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        enseignantRepo.findByEmail(email).ifPresent(template::setCreateurTemplate);

        return ResponseEntity.status(HttpStatus.CREATED).body(templateRepo.save(template));
    }

    // ===== MODIFIER =====

    @Operation(summary = "Modifier un template", description = "L'enseignant peut modifier ses propres templates. L'administrateur peut modifier tous les templates.", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<TemplatePedagogique> modifierTemplate(
            @PathVariable Long id,
            @RequestBody TemplatePedagogique template) {

        return templateRepo.findById(id).map(existing -> {
            // Vérifier que l'enseignant ne modifie que son propre template
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

    // ===== SUPPRIMER (Admin seulement) =====

    @Operation(summary = "Supprimer un template", description = "Réservé à l'administrateur pédagogique.", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<Void> supprimerTemplate(@PathVariable Long id) {
        templateRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}