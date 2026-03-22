package com.feeins.catalogue.repository;

import com.feeins.catalogue.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByLibelle(String libelle);

    boolean existsByLibelle(String libelle);
}
