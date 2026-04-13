package com.feeins.catalogue.controller;

import com.feeins.catalogue.dto.AssocierRessourcesDTO;
import com.feeins.catalogue.dto.TemplatePublicDTO;
import com.feeins.catalogue.dto.RessourceResponseDTO;
import com.feeins.catalogue.entity.*;
import com.feeins.catalogue.repository.*;
import com.feeins.catalogue.service.RessourcePedagogiqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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

    // =========================================================
    // ACCÈS PUBLIC — étudiants et visiteurs, sans token
    // =========================================================

    @Operation(summary = "Lister tous les templates — public")
    @GetMapping("/public")
    @Transactional(readOnly = true)
    public List<TemplatePublicDTO> listerPublic() {
        return templateRepo.findAll().stream().map(t -> {
            List<RessourcePedagogique> ressources = ressourceRepo.findByTemplateId(t.getId());
            List<TemplatePublicDTO.RessourceResumeeDTO> resumes = ressources.stream()
                    .filter(r -> r.getStatut() == RessourcePedagogique.StatutRessource.VALIDEE
                            && Boolean.TRUE.equals(r.getVisible()))
                    .map(r -> TemplatePublicDTO.RessourceResumeeDTO.builder()
                            .id(r.getId())
                            .titre(r.getTitre())
                            .typeSupport(r.getTypeSupport() != null ? r.getTypeSupport().name() : null)
                            .difficulte(r.getDifficulte() != null ? r.getDifficulte().name() : null)
                            .dureeMinutes(r.getDureeMinutes())
                            .build())
                    .collect(Collectors.toList());
            return TemplatePublicDTO.builder()
                    .id(t.getId())
                    .nom(t.getNom())
                    .description(t.getDescription())
                    .modifiable(t.getModifiable())
                    .createurNom(t.getCreateurTemplate() != null ? t.getCreateurTemplate().getNom() : null)
                    .nbRessources(resumes.size())
                    .ressources(resumes)
                    .build();
        }).collect(Collectors.toList());
    }

    @Operation(summary = "Détail d'un template — public")
    @GetMapping("/public/{id}")
    public ResponseEntity<TemplatePedagogique> detailPublic(@PathVariable Long id) {
        return templateRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Ressources d'un template — public")
    @GetMapping("/public/{id}/ressources")
    @Transactional(readOnly = true)
    public ResponseEntity<List<RessourceResponseDTO>> ressourcesPublic(@PathVariable Long id) {
        return templateRepo.findById(id).map(t -> {
            List<RessourceResponseDTO> dtos = ressourceRepo.findByTemplateId(id)
                    .stream()
                    .filter(r -> r.getStatut() == RessourcePedagogique.StatutRessource.VALIDEE
                            && Boolean.TRUE.equals(r.getVisible()))
                    .map(ressourceService::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        }).orElse(ResponseEntity.notFound().build());
    }

    // =========================================================
    // ACCÈS AUTHENTIFIÉ — enseignant et admin
    // =========================================================

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

    @Operation(summary = "Lister les templates clé en main", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/cle-en-main")
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMINISTRATEUR_PEDAGOGIQUE')")
    public List<TemplatePedagogique> listerCleEnMain() {
        return templateRepo.findByModifiable(false);
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
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<Void> supprimerTemplate(@PathVariable Long id) {
        return templateRepo.findById(id).map(template -> {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMINISTRATEUR_PEDAGOGIQUE".equals(a.getAuthority()));
            // L'enseignant ne peut supprimer que ses propres templates
            if (!isAdmin && template.getCreateurTemplate() != null &&
                    !email.equalsIgnoreCase(template.getCreateurTemplate().getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).<Void>build();
            }
            // Détacher les ressources avant de supprimer
            ressourceRepo.findByTemplateId(id).forEach(r -> {
                r.setTemplate(null);
                ressourceRepo.save(r);
            });
            templateRepo.deleteById(id);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Associer des ressources à un template", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}/ressources")
    @Transactional
    @PreAuthorize("hasAnyRole('ENSEIGNANT', 'ADMINISTRATEUR_PEDAGOGIQUE')")
    public ResponseEntity<?> associerRessources(@PathVariable Long id,
            @RequestBody AssocierRessourcesDTO body) {
        return templateRepo.findById(id).map(template -> {
            List<Long> ids = body.getRessourceIds() != null ? body.getRessourceIds() : List.of();
            // Détacher les anciennes ressources
            ressourceRepo.findByTemplateId(id).forEach(r -> {
                r.setTemplate(null);
                ressourceRepo.save(r);
            });
            // Attacher les nouvelles
            ids.forEach(rid -> ressourceRepo.findById(rid).ifPresent(r -> {
                r.setTemplate(template);
                ressourceRepo.save(r);
            }));
            List<RessourceResponseDTO> dtos = ressourceRepo.findByTemplateId(id)
                    .stream().map(ressourceService::toDTO).collect(Collectors.toList());
            return ResponseEntity.ok(Map.of("nbRessources", dtos.size(), "ressources", dtos));
        }).orElse(ResponseEntity.notFound().build());
    }
}