package com.feeins.catalogue.service;

import com.feeins.catalogue.dto.*;
import com.feeins.catalogue.entity.*;
import com.feeins.catalogue.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RessourcePedagogiqueService {

    @Autowired
    private RessourcePedagogiqueRepository ressourceRepo;
    @Autowired
    private NiveauRepository niveauRepo;
    @Autowired
    private ThematiqueRepository thematiqueRepo;
    @Autowired
    private TagRepository tagRepo;
    @Autowired
    private TemplatePedagogiqueRepository templateRepo;
    @Autowired
    private EnseignantRepository enseignantRepo;

    // ===== CRÉER =====
    public RessourceResponseDTO creerRessource(RessourceRequestDTO dto) {
        RessourcePedagogique ressource = new RessourcePedagogique();
        mapDtoToEntity(dto, ressource);

        // Récupérer l'enseignant connecté
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        enseignantRepo.findByEmail(email).ifPresent(ressource::setCreateur);

        // Générer la nomenclature automatiquement
        ressource.setNomenclature(genererNomenclature(ressource));
        ressource.setStatut(RessourcePedagogique.StatutRessource.EN_ATTENTE);
        if (dto.getUsagePedagogique() != null)
            ressource.setUsagePedagogique(dto.getUsagePedagogique());

        ressource.setDroits(
                dto.getDroits() != null ? dto.getDroits() : RessourcePedagogique.Droits.FEEINS_INTERNE);

        if (dto.getUsageMoodle() != null)
            ressource.setUsageMoodle(dto.getUsageMoodle());

        if (dto.getAuteurPartenaire() != null)
            ressource.setAuteurPartenaire(dto.getAuteurPartenaire());

        return toDTO(ressourceRepo.save(ressource));
    }

    // ===== MODIFIER =====
    public RessourceResponseDTO modifierRessource(Long id, RessourceRequestDTO dto) {
        RessourcePedagogique ressource = ressourceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ressource introuvable : " + id));
        mapDtoToEntity(dto, ressource);
        return toDTO(ressourceRepo.save(ressource));
    }

    // ===== VALIDER (Admin Péda) =====
    public RessourceResponseDTO validerRessource(Long id) {
        RessourcePedagogique ressource = ressourceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ressource introuvable : " + id));
        ressource.setStatut(RessourcePedagogique.StatutRessource.VALIDEE);
        return toDTO(ressourceRepo.save(ressource));
    }

    // ===== REFUSER =====
    public RessourceResponseDTO refuserRessource(Long id) {
        RessourcePedagogique ressource = ressourceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ressource introuvable : " + id));
        ressource.setStatut(RessourcePedagogique.StatutRessource.REFUSEE);
        return toDTO(ressourceRepo.save(ressource));
    }

    // ===== SUPPRIMER =====
    public void supprimerRessource(Long id) {
        ressourceRepo.deleteById(id);
    }

    // ===== CONSULTER =====
    @Transactional(readOnly = true)
    public RessourceResponseDTO consulterRessource(Long id) {
        return ressourceRepo.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Ressource introuvable : " + id));
    }

    // ===== LISTE TOUTES (validées) =====
    @Transactional(readOnly = true)
    public List<RessourceResponseDTO> listerRessourcesValidees() {
        return ressourceRepo.findByStatut(RessourcePedagogique.StatutRessource.VALIDEE)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ===== TOUTES (admin) =====
    @Transactional(readOnly = true)
    public List<RessourceResponseDTO> listerToutesRessources() {
        return ressourceRepo.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ===== RECHERCHE AVANCÉE =====
    @Transactional(readOnly = true)
    public List<RessourceResponseDTO> rechercherRessources(RechercheRequestDTO criteres) {
        List<RessourcePedagogique> resultats = ressourceRepo.rechercherAvecCriteres(
                criteres.getNiveauId(),
                criteres.getThematiqueId(),
                criteres.getTypeSupport(),
                criteres.getDifficulte(),
                criteres.getDureeMax(),
                criteres.getKeyword(),
                criteres.getTag());

        return resultats.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ===== AJOUTER TAG =====
    public RessourceResponseDTO ajouterTag(Long ressourceId, Long tagId) {
        RessourcePedagogique ressource = ressourceRepo.findById(ressourceId)
                .orElseThrow(() -> new RuntimeException("Ressource introuvable"));
        Tag tag = tagRepo.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag introuvable"));
        ressource.getTags().add(tag);
        return toDTO(ressourceRepo.save(ressource));
    }

    // ===== SUPPRIMER TAG =====
    public RessourceResponseDTO supprimerTag(Long ressourceId, Long tagId) {
        RessourcePedagogique ressource = ressourceRepo.findById(ressourceId)
                .orElseThrow(() -> new RuntimeException("Ressource introuvable"));
        ressource.getTags().removeIf(t -> t.getId().equals(tagId));
        return toDTO(ressourceRepo.save(ressource));
    }

    // ===== GÉNÉRATION NOMENCLATURE =====
    // Format : FEEINS-{TYPE}-{NIVEAU}-{SEQUENCE}
    // Exemple : FEEINS-VIDEO-L3-0042
    private String genererNomenclature(RessourcePedagogique ressource) {
        String type = ressource.getTypeSupport() != null
                ? ressource.getTypeSupport().name()
                : "AUTRE";
        String niveau = ressource.getNiveau() != null
                ? ressource.getNiveau().getNom().toUpperCase().replace(" ", "_")
                : "GENERAL";
        long count = ressourceRepo.countByTypeSupport(ressource.getTypeSupport()) + 1;
        return String.format("FEEINS-%s-%s-%04d", type, niveau, count);
    }

    // ===== MAPPING DTO -> ENTITÉ =====
    private void mapDtoToEntity(RessourceRequestDTO dto, RessourcePedagogique entity) {
        entity.setTitre(dto.getTitre());
        entity.setDescription(dto.getDescription());
        entity.setDureeMinutes(dto.getDureeMinutes());
        entity.setTypeSupport(dto.getTypeSupport());
        entity.setUrlAcces(dto.getUrlAcces());
        entity.setDifficulte(dto.getDifficulte());
        entity.setObjectifsPedagogiques(dto.getObjectifsPedagogiques());
        entity.setCompetencesVisees(dto.getCompetencesVisees());

        // ✅ NOUVEAUX CHAMPS
        if (dto.getUsagePedagogique() != null)
            entity.setUsagePedagogique(dto.getUsagePedagogique());
        if (dto.getDroits() != null)
            entity.setDroits(dto.getDroits());
        if (dto.getUsageMoodle() != null)
            entity.setUsageMoodle(dto.getUsageMoodle());
        if (dto.getAuteurPartenaire() != null)
            entity.setAuteurPartenaire(dto.getAuteurPartenaire());

        if (dto.getNiveauId() != null)
            niveauRepo.findById(dto.getNiveauId()).ifPresent(entity::setNiveau);
        if (dto.getThematiqueId() != null)
            thematiqueRepo.findById(dto.getThematiqueId()).ifPresent(entity::setThematique);
        if (dto.getTemplateId() != null)
            templateRepo.findById(dto.getTemplateId()).ifPresent(entity::setTemplate);
        if (dto.getTagIds() != null) {
            List<Tag> tags = tagRepo.findAllById(dto.getTagIds());
            entity.setTags(tags);
        }
    }

    // ===== MAPPING ENTITÉ -> DTO =====
    public RessourceResponseDTO toDTO(RessourcePedagogique r) {
        return RessourceResponseDTO.builder()
                .id(r.getId())
                .titre(r.getTitre())
                .description(r.getDescription())
                .dureeMinutes(r.getDureeMinutes())
                .typeSupport(r.getTypeSupport())
                .urlAcces(r.getUrlAcces())
                .difficulte(r.getDifficulte())
                .objectifsPedagogiques(r.getObjectifsPedagogiques())
                .competencesVisees(r.getCompetencesVisees())
                .nomenclature(r.getNomenclature())
                .dateCreation(r.getDateCreation())
                .statut(r.getStatut())
                .niveauNom(r.getNiveau() != null ? r.getNiveau().getNom() : null)
                .thematiqueNom(r.getThematique() != null ? r.getThematique().getNom() : null)
                .tags(r.getTags().stream().map(Tag::getLibelle).collect(Collectors.toList()))
                .templateNom(r.getTemplate() != null ? r.getTemplate().getNom() : null)
                .createurNom(r.getCreateur() != null ? r.getCreateur().getNom() : null)
                // ✅ NOUVEAUX CHAMPS
                .usagePedagogique(r.getUsagePedagogique())
                .droits(r.getDroits())
                .usageMoodle(r.getUsageMoodle())
                .auteurPartenaire(r.getAuteurPartenaire())
                .derniereVerification(r.getDerniereVerification())
                .build();
    }
}
