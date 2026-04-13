package com.feeins.catalogue.repository;

import com.feeins.catalogue.entity.FiltreConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FiltreConfigRepository extends JpaRepository<FiltreConfig, Long> {
    List<FiltreConfig> findByCategorieOrderByOrdreAsc(String categorie);

    List<FiltreConfig> findByCategorieAndActifTrueOrderByOrdreAsc(String categorie);

    boolean existsByCategorie(String categorie);
}