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

        List<RessourcePedagogique> findByNiveauId(Long niveauId);

        List<RessourcePedagogique> findByThematiqueId(Long thematiqueId);

        List<RessourcePedagogique> findByTypeSupport(RessourcePedagogique.TypeSupport typeSupport);

        List<RessourcePedagogique> findByDifficulte(RessourcePedagogique.Difficulte difficulte);

        List<RessourcePedagogique> findByStatut(RessourcePedagogique.StatutRessource statut);

        // Ressources validées et visibles (accessibles sans authentification)
        List<RessourcePedagogique> findByStatutAndVisibleTrueOrderByDateCreationDesc(
                        RessourcePedagogique.StatutRessource statut);

        List<RessourcePedagogique> findByDureeMinutesLessThanEqual(Integer dureeMax);

        // Ressources du contributeur connecté (par son ID)
        List<RessourcePedagogique> findByContributeurId(Long contributeurId);

        // Ressources du contributeur connecté (par son email)
        List<RessourcePedagogique> findByContributeurEmailOrderByDateCreationDesc(String email);

        Optional<RessourcePedagogique> findByNomenclature(String nomenclature);

        @Query("SELECT COUNT(r) FROM RessourcePedagogique r WHERE r.typeSupport = :type")
        Long countByTypeSupport(@Param("type") RessourcePedagogique.TypeSupport type);

        /**
         * Recherche multicritères publique — accessible sans authentification.
         * Retourne uniquement les ressources VALIDEES et visibles.
         */
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
                        "AND (:usagePedagogique IS NULL OR r.usagePedagogique = :usagePedagogique) " +
                        "AND r.statut = 'VALIDEE' " +
                        "AND r.visible = true")
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
