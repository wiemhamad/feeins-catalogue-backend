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

        Optional<RessourcePedagogique> findByNomenclature(String nomenclature);

        List<RessourcePedagogique> findByContributeurId(Long contributeurId);

        @Query("SELECT COUNT(r) FROM RessourcePedagogique r WHERE r.typeSupport = :type")
        Long countByTypeSupport(@Param("type") RessourcePedagogique.TypeSupport type);

        // ===== CATALOGUE PUBLIC — tout chargé en 1 requête =====
        @Query("SELECT DISTINCT r FROM RessourcePedagogique r " +
                        "LEFT JOIN FETCH r.tags " +
                        "LEFT JOIN FETCH r.niveau " +
                        "LEFT JOIN FETCH r.thematique " +
                        "WHERE r.statut = 'VALIDEE' AND r.visible = true " +
                        "ORDER BY r.dateCreation DESC")
        List<RessourcePedagogique> findValideesFetch();

        @Query("SELECT DISTINCT r FROM RessourcePedagogique r " +
                        "LEFT JOIN FETCH r.tags " +
                        "LEFT JOIN FETCH r.niveau " +
                        "LEFT JOIN FETCH r.thematique " +
                        "LEFT JOIN FETCH r.contributeur " +
                        "ORDER BY r.dateCreation DESC")
        List<RessourcePedagogique> findAllFetch();

        @Query("SELECT DISTINCT r FROM RessourcePedagogique r " +
                        "LEFT JOIN FETCH r.tags " +
                        "LEFT JOIN FETCH r.niveau " +
                        "LEFT JOIN FETCH r.thematique " +
                        "WHERE r.contributeur.email = :email " +
                        "ORDER BY r.dateCreation DESC")
        List<RessourcePedagogique> findByContributeurEmailFetch(@Param("email") String email);

        @Query("SELECT DISTINCT r FROM RessourcePedagogique r " +
                        "LEFT JOIN FETCH r.tags " +
                        "LEFT JOIN FETCH r.niveau " +
                        "LEFT JOIN FETCH r.thematique " +
                        "WHERE r.template.id = :templateId")
        List<RessourcePedagogique> findByTemplateId(@Param("templateId") Long templateId);

        // ===== ÉTAPE 1 : filtrer les IDs (requête simple, pas de FETCH) =====
        // Séparer le filtrage du chargement évite le conflit double-JOIN sur tags
        @Query("SELECT DISTINCT r.id FROM RessourcePedagogique r " +
                        "LEFT JOIN r.tags t " +
                        "WHERE (:niveauId IS NULL OR r.niveau.id = :niveauId) " +
                        "AND (:thematiqueId IS NULL OR r.thematique.id = :thematiqueId) " +
                        "AND (:typeSupport IS NULL OR r.typeSupport = :typeSupport) " +
                        "AND (:difficulte IS NULL OR r.difficulte = :difficulte) " +
                        "AND (:dureeMax IS NULL OR r.dureeMinutes <= :dureeMax) " +
                        "AND (:keyword IS NULL OR :keyword = '' OR " +
                        "     LOWER(r.titre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "     LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                        "AND (:tag IS NULL OR :tag = '' OR LOWER(t.libelle) = LOWER(:tag)) " +
                        "AND r.statut = 'VALIDEE' AND r.visible = true")
        List<Long> filtrerIds(
                        @Param("niveauId") Long niveauId,
                        @Param("thematiqueId") Long thematiqueId,
                        @Param("typeSupport") RessourcePedagogique.TypeSupport typeSupport,
                        @Param("difficulte") RessourcePedagogique.Difficulte difficulte,
                        @Param("dureeMax") Integer dureeMax,
                        @Param("keyword") String keyword,
                        @Param("tag") String tag);

        // ===== ÉTAPE 2 : charger par IDs avec FETCH (toutes relations en 1 requête)
        // =====
        @Query("SELECT DISTINCT r FROM RessourcePedagogique r " +
                        "LEFT JOIN FETCH r.tags " +
                        "LEFT JOIN FETCH r.niveau " +
                        "LEFT JOIN FETCH r.thematique " +
                        "WHERE r.id IN :ids " +
                        "ORDER BY r.dateCreation DESC")
        List<RessourcePedagogique> findByIdsFetch(@Param("ids") List<Long> ids);
}