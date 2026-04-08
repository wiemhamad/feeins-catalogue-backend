package com.feeins.catalogue.controller;

import com.feeins.catalogue.dto.*;
import com.feeins.catalogue.entity.RessourcePedagogique;
import com.feeins.catalogue.repository.RessourcePedagogiqueRepository;
import com.feeins.catalogue.service.RessourcePedagogiqueService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ressources")
@CrossOrigin(origins = "*")
@Tag(name = "📖 Ressources Pédagogiques", description = "Gestion des grains pédagogiques (CRUD, recherche, validation)")
public class RessourcePedagogiqueController {

        @Autowired
        private RessourcePedagogiqueService ressourceService;
        @Autowired
        private RessourcePedagogiqueRepository ressourceRepo;

        // =====================================================================
        // ACCÈS PUBLIC — sans authentification
        // =====================================================================

        @Operation(summary = "Lister toutes les ressources validées", description = "Accessible sans authentification. Retourne uniquement les ressources VALIDEES et visibles.")
        @GetMapping
        public ResponseEntity<List<RessourceResponseDTO>> listerRessourcesValidees() {
                return ResponseEntity.ok(ressourceService.listerRessourcesValidees());
        }

        @Operation(summary = "Consulter une ressource par ID", description = "Accessible sans authentification.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Ressource trouvée"),
                        @ApiResponse(responseCode = "404", description = "Ressource introuvable")
        })
        @GetMapping("/{id}")
        public ResponseEntity<?> consulterRessource(@PathVariable Long id) {
                try {
                        return ResponseEntity.ok(ressourceService.consulterRessource(id));
                } catch (RuntimeException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
        }

        @Operation(summary = "Recherche avancée de ressources", description = "Accessible sans authentification. Multicritères : keyword, niveauId, thematiqueId, typeSupport, difficulte, dureeMax, tag, usagePedagogique.")
        @PostMapping("/rechercher")
        public ResponseEntity<List<RessourceResponseDTO>> rechercherRessources(
                        @RequestBody RechercheRequestDTO criteres) {
                return ResponseEntity.ok(ressourceService.rechercherRessources(criteres));
        }

        // =====================================================================
        // ACCÈS ADMIN — AdministrateurPedagogique uniquement
        // =====================================================================

        @Operation(summary = "Lister TOUTES les ressources (tous statuts)", description = "Administrateur pédagogique uniquement.", security = @SecurityRequirement(name = "bearerAuth"))
        @GetMapping("/toutes")
        @PreAuthorize("hasRole('ADMINISTRATEUR_PEDAGOGIQUE')")
        public ResponseEntity<List<RessourceResponseDTO>> listerToutesRessources() {
                return ResponseEntity.ok(ressourceService.listerToutesRessources());
        }

        @Operation(summary = "Valider une ressource (EN_ATTENTE → VALIDEE)", security = @SecurityRequirement(name = "bearerAuth"))
        @PostMapping("/{id}/valider")
        @PreAuthorize("hasRole('ADMINISTRATEUR_PEDAGOGIQUE')")
        public ResponseEntity<?> validerRessource(@PathVariable Long id) {
                try {
                        return ResponseEntity.ok(ressourceService.validerRessource(id));
                } catch (RuntimeException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
        }

        @Operation(summary = "Refuser une ressource (EN_ATTENTE → REFUSEE)", security = @SecurityRequirement(name = "bearerAuth"))
        @PostMapping("/{id}/refuser")
        @PreAuthorize("hasRole('ADMINISTRATEUR_PEDAGOGIQUE')")
        public ResponseEntity<?> refuserRessource(@PathVariable Long id) {
                try {
                        return ResponseEntity.ok(ressourceService.refuserRessource(id));
                } catch (RuntimeException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
        }

        @Operation(summary = "Supprimer une ressource", security = @SecurityRequirement(name = "bearerAuth"))
        @DeleteMapping("/{id}/supprimer")
        @PreAuthorize("hasRole('ADMINISTRATEUR_PEDAGOGIQUE')")
        public ResponseEntity<?> supprimerRessource(@PathVariable Long id) {
                try {
                        ressourceService.supprimerRessource(id);
                        return ResponseEntity.ok("Ressource supprimée avec succès");
                } catch (RuntimeException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
        }

        @Operation(summary = "Marquer une ressource comme vérifiée (maintenance annuelle)", security = @SecurityRequirement(name = "bearerAuth"))
        @PutMapping("/{id}/verifier")
        @PreAuthorize("hasRole('ADMINISTRATEUR_PEDAGOGIQUE')")
        public ResponseEntity<?> marquerVerifiee(@PathVariable Long id) {
                return ressourceRepo.findById(id).map(r -> {
                        r.setDerniereVerification(java.time.LocalDate.now());
                        ressourceRepo.save(r);
                        return ResponseEntity.ok(Map.of(
                                        "message", "Ressource marquée comme vérifiée",
                                        "date", r.getDerniereVerification()));
                }).orElse(ResponseEntity.notFound().build());
        }

        @Operation(summary = "Alertes : évaluations sommatives (risque de doublon)", description = "Permet de détecter les ressources utilisées à la fois en évaluation formative et sommative.", security = @SecurityRequirement(name = "bearerAuth"))
        @GetMapping("/alertes/evaluations-sommatives")
        @PreAuthorize("hasRole('ADMINISTRATEUR_PEDAGOGIQUE')")
        public ResponseEntity<?> alertesEvaluationsSommatives() {
                List<RessourcePedagogique> risques = ressourceRepo.findAll().stream()
                                .filter(r -> r.getUsagePedagogique() == RessourcePedagogique.UsagePedagogique.EVALUATION_SOMMATIVE)
                                .collect(java.util.stream.Collectors.toList());
                return ResponseEntity.ok(risques);
        }

        // =====================================================================
        // ACCÈS CONTRIBUTEUR — crée et gère ses propres ressources
        // =====================================================================

        @Operation(summary = "Mes ressources (contributeur connecté)", description = "Retourne les ressources proposées par le contributeur actuellement connecté.", security = @SecurityRequirement(name = "bearerAuth"))
        @GetMapping("/mes-ressources")
        @PreAuthorize("hasAnyRole('CONTRIBUTEUR', 'ADMINISTRATEUR_PEDAGOGIQUE')")
        public ResponseEntity<List<RessourceResponseDTO>> listerMesRessources() {
                return ResponseEntity.ok(ressourceService.listerRessourcesDuContributeurConnecte());
        }

        @Operation(summary = "Créer une nouvelle ressource pédagogique", description = "Réservé au CONTRIBUTEUR (consultant pédagogique). La ressource est créée avec statut EN_ATTENTE et doit être validée par l'administrateur pédagogique.", security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Ressource créée"),
                        @ApiResponse(responseCode = "403", description = "Rôle CONTRIBUTEUR requis")
        })
        @PostMapping("/creer")
        @PreAuthorize("hasAnyRole('CONTRIBUTEUR', 'ADMINISTRATEUR_PEDAGOGIQUE')")
        public ResponseEntity<?> creerRessource(@Valid @RequestBody RessourceRequestDTO dto) {
                try {
                        return ResponseEntity.status(HttpStatus.CREATED)
                                        .body(ressourceService.creerRessource(dto));
                } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
                }
        }

        @Operation(summary = "Modifier une ressource existante", description = "Le contributeur ne peut modifier que ses propres ressources. L'admin peut tout modifier.", security = @SecurityRequirement(name = "bearerAuth"))
        @PutMapping("/{id}/modifier")
        @PreAuthorize("hasAnyRole('CONTRIBUTEUR', 'ADMINISTRATEUR_PEDAGOGIQUE')")
        public ResponseEntity<?> modifierRessource(@PathVariable Long id,
                        @Valid @RequestBody RessourceRequestDTO dto) {
                try {
                        return ResponseEntity.ok(ressourceService.modifierRessource(id, dto));
                } catch (RuntimeException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
        }

        @Operation(summary = "Modifier la visibilité d'une ressource", security = @SecurityRequirement(name = "bearerAuth"))
        @PutMapping("/{id}/visibilite")
        @PreAuthorize("hasAnyRole('CONTRIBUTEUR', 'ADMINISTRATEUR_PEDAGOGIQUE')")
        public ResponseEntity<?> modifierVisibilite(@PathVariable Long id,
                        @RequestBody Map<String, Boolean> body) {
                try {
                        boolean visible = Boolean.TRUE.equals(body.get("visible"));
                        return ResponseEntity.ok(ressourceService.modifierVisibilite(id, visible));
                } catch (RuntimeException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
        }

        // =====================================================================
        // GESTION DES TAGS — Contributeur et Admin
        // =====================================================================

        @Operation(summary = "Ajouter un tag à une ressource", security = @SecurityRequirement(name = "bearerAuth"))
        @PostMapping("/{id}/tags/{tagId}")
        @PreAuthorize("hasAnyRole('CONTRIBUTEUR', 'ADMINISTRATEUR_PEDAGOGIQUE')")
        public ResponseEntity<?> ajouterTag(@PathVariable Long id, @PathVariable Long tagId) {
                try {
                        return ResponseEntity.ok(ressourceService.ajouterTag(id, tagId));
                } catch (RuntimeException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
        }

        @Operation(summary = "Retirer un tag d'une ressource", security = @SecurityRequirement(name = "bearerAuth"))
        @DeleteMapping("/{id}/tags/{tagId}")
        @PreAuthorize("hasAnyRole('CONTRIBUTEUR', 'ADMINISTRATEUR_PEDAGOGIQUE')")
        public ResponseEntity<?> supprimerTag(@PathVariable Long id, @PathVariable Long tagId) {
                try {
                        return ResponseEntity.ok(ressourceService.supprimerTag(id, tagId));
                } catch (RuntimeException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
                }
        }
}
