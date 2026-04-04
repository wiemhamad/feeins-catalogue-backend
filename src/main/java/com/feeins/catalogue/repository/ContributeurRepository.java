package com.feeins.catalogue.repository;

import com.feeins.catalogue.entity.Contributeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContributeurRepository extends JpaRepository<Contributeur, Long> {
    Optional<Contributeur> findByEmail(String email);
}
