package com.feeins.catalogue.repository;

import com.feeins.catalogue.entity.Niveau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NiveauRepository extends JpaRepository<Niveau, Long> {
    Optional<Niveau> findByNom(String nom);

    boolean existsByNom(String nom);
}
