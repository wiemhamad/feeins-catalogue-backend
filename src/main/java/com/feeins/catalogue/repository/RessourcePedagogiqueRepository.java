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

        // Recherche par durée maximale
        List<RessourcePedagogique> findByDureeMinutesLessThanEqual(Integer dureeMax);

        // Recherche full-text sur titre et description
        @Query("SELECT r FROM RessourcePedagogique r WHERE " +
                        "LOWER(r.titre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
        List<RessourcePedagogique> searchByKeyword(@Param("keyword") String keyword);

        // Recherche par tag
        @Query("SELECT r FROM RessourcePedagogique r JOIN r.tags t WHERE t.libelle = :tagLibelle")
        List<RessourcePedagogique> findByTag(@Param("tagLibelle") String tagLibelle);

        // Recherche combinée (critères multiples)
        @Query("SELECT DISTINCT r FROM RessourcePedagogique r " +
                        "LEFT JOIN r.tags t " +
                        "WHERE (:niveauId IS NULL OR r.niveau.id = :niveauId) " +
                        "AND (:thematiqueId IS NULL OR r.thematique.id = :thematiqueId) " +
                        "AND (:typeSupport IS NULL OR r.typeSupport = :typeSupport) " +
                        "AND (:difficulte IS NULL OR r.difficulte = :difficulte) " +
                        "AND (:dureeMax IS NULL OR r.dureeMinutes <= :dureeMax) " +
                        "AND r.statut = 'VALIDEE'")
        List<RessourcePedagogique> rechercherAvecCriteres(
                        @Param("niveauId") Long niveauId,
                        @Param("thematiqueId") Long thematiqueId,
                        @Param("typeSupport") RessourcePedagogique.TypeSupport typeSupport,
                        @Param("difficulte") RessourcePedagogique.Difficulte difficulte,
                        @Param("dureeMax") Integer dureeMax);

        // Par créateur
        List<RessourcePedagogique> findByCreateurId(Long createurId);

        // Par nomenclature
        Optional<RessourcePedagogique> findByNomenclature(String nomenclature);

        // Dernière ressource créée pour générer la nomenclature
        @Query("SELECT COUNT(r) FROM RessourcePedagogique r WHERE r.typeSupport = :type")
        Long countByTypeSupport(@Param("type") RessourcePedagogique.TypeSupport type);
}
