package com.feeins.catalogue.repository;

import com.feeins.catalogue.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RessourcePedagogiqueRepository extends JpaRepository<RessourcePedagogique, Long> {

        // Recherche par niveau
        List<RessourcePedagogique> findByNiveauId(Long niveauId);

        // Recherche par thématique
        List<RessourcePedagogique> findByThematiqueId(Long thematiqueId);

        // Recherche par type de support
        List<RessourcePedagogique> findByTypeSupport(RessourcePedagogique.TypeSupport typeSupport);

        // Recherche par difficulté
        List<RessourcePedagogique> findByDifficulte(RessourcePedagogique.Difficulte difficulte);

        // Recherche par statut
        List<RessourcePedagogique> findByStatut(RessourcePedagogique.StatutRessource statut);

        List<RessourcePedagogique> findByStatutAndVisibleTrueOrderByDateCreationDesc(
                        RessourcePedagogique.StatutRessource statut);

        // Recherche par durée maximale
        List<RessourcePedagogique> findByDureeMinutesLessThanEqual(Integer dureeMax);

        // Recherche combinée (critères multiples)
        @Query("SELECT DISTINCT r FROM RessourcePedagogique r " +
                        "LEFT JOIN r.tags t " +
                        "WHERE (:niveauId IS NULL OR r.niveau.id = :niveauId) " +
                        "AND (:thematiqueId IS NULL OR r.thematique.id = :thematiqueId) " +
                        "AND (:typeSupport IS NULL OR r.typeSupport = :typeSupport) " +
                        "AND (:difficulte IS NULL OR r.difficulte = :difficulte) " +
                        "AND (:dureeMax IS NULL OR r.dureeMinutes <= :dureeMax) " +
                        "AND (:keyword IS NULL OR :keyword = '' OR " +
                        "LOWER(r.titre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                        "AND (:tag IS NULL OR :tag = '' OR LOWER(t.libelle) = LOWER(:tag)) " +
                        "AND r.statut = 'VALIDEE' " +
                        "AND r.visible = true")
        List<RessourcePedagogique> rechercherAvecCriteres(
                        @Param("niveauId") Long niveauId,
                        @Param("thematiqueId") Long thematiqueId,
                        @Param("typeSupport") RessourcePedagogique.TypeSupport typeSupport,
                        @Param("difficulte") RessourcePedagogique.Difficulte difficulte,
                        @Param("dureeMax") Integer dureeMax,
                        @Param("keyword") String keyword,
                        @Param("tag") String tag);

        // Par créateur
        List<RessourcePedagogique> findByCreateurId(Long createurId);

        List<RessourcePedagogique> findByCreateurEmailOrderByDateCreationDesc(String email);

        // Par nomenclature
        Optional<RessourcePedagogique> findByNomenclature(String nomenclature);

        // Dernière ressource créée pour générer la nomenclature
        @Query("SELECT COUNT(r) FROM RessourcePedagogique r WHERE r.typeSupport = :type")
        Long countByTypeSupport(@Param("type") RessourcePedagogique.TypeSupport type);

        @Query("SELECT DISTINCT r FROM RessourcePedagogique r " +
                        "LEFT JOIN r.tags t " +
                        "WHERE (:niveauId IS NULL OR r.niveau.id = :niveauId) " +
                        "AND (:thematiqueId IS NULL OR r.thematique.id = :thematiqueId) " +
                        "AND (:typeSupport IS NULL OR r.typeSupport = :typeSupport) " +
                        "AND (:difficulte IS NULL OR r.difficulte = :difficulte) " +
                        "AND (:dureeMax IS NULL OR r.dureeMinutes <= :dureeMax) " +
                        "AND (:keyword IS NULL OR LOWER(r.titre) LIKE LOWER(CONCAT('%',:keyword,'%')) " +
                        "     OR LOWER(r.description) LIKE LOWER(CONCAT('%',:keyword,'%'))) " +
                        "AND (:tag IS NULL OR t.libelle = :tag) " +
                        "AND (:usagePedagogique IS NULL OR r.usagePedagogique = :usagePedagogique) " +
                        "AND r.statut = 'VALIDEE'")
        List<RessourcePedagogique> rechercherAvecCriteres(
                        @Param("niveauId") Long niveauId,
                        @Param("thematiqueId") Long thematiqueId,
                        @Param("typeSupport") RessourcePedagogique.TypeSupport typeSupport,
                        @Param("difficulte") RessourcePedagogique.Difficulte difficulte,
                        @Param("dureeMax") Integer dureeMax,
                        @Param("keyword") String keyword,
                        @Param("tag") String tag,
                        @Param("usagePedagogique") RessourcePedagogique.UsagePedagogique usagePedagogique);
}
