package com.feeins.catalogue.repository;

import com.feeins.catalogue.entity.TemplatePedagogique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplatePedagogiqueRepository extends JpaRepository<TemplatePedagogique, Long> {
    List<TemplatePedagogique> findByModifiable(Boolean modifiable);
}
