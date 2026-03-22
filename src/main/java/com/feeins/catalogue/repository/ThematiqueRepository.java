package com.feeins.catalogue.repository;

import com.feeins.catalogue.entity.Thematique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThematiqueRepository extends JpaRepository<Thematique, Long> {
    Optional<Thematique> findByNom(String nom);

    boolean existsByNom(String nom);
}