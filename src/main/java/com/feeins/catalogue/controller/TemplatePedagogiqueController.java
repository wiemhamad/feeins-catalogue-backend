package com.feeins.catalogue.controller;

import com.feeins.catalogue.entity.*;
import com.feeins.catalogue.repository.*;
import com.feeins.catalogue.service.RessourcePedagogiqueService;
import com.feeins.catalogue.dto.RessourceResponseDTO;
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
@Tag(name = "📋 Templates Pédagogiques")
public class TemplatePedagogiqueController {

    @Autowired
    private TemplatePedagogiqueRepository templateRepo;
    @Autowired
    private EnseignantRepository enseignantRepo;
    @Autowired
    private RessourcePedagogiqueRepository ressourceRepo;
    @Autowired
    private RessourcePedagogiqueService ressourceService;

    // ===== ACCÈS PUBLIC (étudiants et visiteurs) =====

    @Operation(summary = "Lister tous les templates — public")
    @GetMapping("/public")
    public List<TemplatePedagogique> listerPublic() {
        return templateRepo.findAll();
    }

    @Operation(summary = "Détail d'un template — public")
    @GetMapping("/public/{id}")
    public ResponseEntity<TemplatePedagogique> detailPublic(@PathVariable Long id) {
        return templateRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Ressources d'un template — PUBLIC
     * Retourne uniquement les ressources VALIDÉES associées à ce template.
     * C'est ce que voit l'étudiant quand il clique sur un template.
     */
    @Operation(summary = "Ressources d'un template — public")
    @GetMapping("/public/{id}/ressources")
    public ResponseEntity<List<RessourceResponseDTO>> ressourcesPublic(@PathVariable Long id) {
        return templateRepo.findById(id).map(template -> {
            List<RessourceResponseDTO> dtos = ressourceRepo.findByTemplateId(id)
                    .stream()
                    .filter(r -> r.getStatut() == RessourcePedagogique.StatutRessource.VALIDEE
                            && Boolean.TRUE.equals(r.getVisible()))
                    .map(ressourceService::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        }).orElse(ResponseEntity.notFound().build());
    }

    // ===== ACCÈS AUTHENTIFIÉ =====

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

    @Operation(summary = "Créer un template", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<TemplatePedagogique> creerTemplate(@RequestBody TemplatePedagogique template) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        enseignantRepo.findByEmail(email).ifPresent(template::setCreateurTemplate);
        return ResponseEntity.status(HttpStatus.CREATED).body(templateRepo.save(template));
    }

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

    @Operation(summary = "Supprimer un template", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<Void> supprimerTemplate(@PathVariable Long id) {
        templateRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}