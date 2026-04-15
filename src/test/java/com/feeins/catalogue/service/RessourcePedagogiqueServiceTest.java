package com.feeins.catalogue.service;

import com.feeins.catalogue.dto.RessourceResponseDTO;
import com.feeins.catalogue.entity.RessourcePedagogique;
import com.feeins.catalogue.repository.RessourcePedagogiqueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RessourcePedagogiqueServiceTest {

    @Mock
    private RessourcePedagogiqueRepository ressourceRepository;

    @InjectMocks
    private RessourcePedagogiqueService ressourceService;

    private RessourcePedagogique ressource;

    @BeforeEach
    void setUp() {
        ressource = new RessourcePedagogique();
        ressource.setId(1L);
        ressource.setTitre("Test Ressource");
        ressource.setStatut(RessourcePedagogique.StatutRessource.VALIDEE);
        ressource.setVisible(true);
        ressource.setTags(new java.util.ArrayList<>());
    }

    @Test
    void listerRessourcesValidees_ReturnsList() {
        // Arrange - utilise findValideesFetch (nouvelle méthode optimisée)
        when(ressourceRepository.findValideesFetch())
                .thenReturn(Arrays.asList(ressource));

        // Act
        List<RessourceResponseDTO> result = ressourceService.listerRessourcesValidees();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Ressource", result.get(0).getTitre());
        assertEquals(RessourcePedagogique.StatutRessource.VALIDEE, result.get(0).getStatut());
    }

    @Test
    void listerToutesRessources_ReturnsList() {
        // Arrange - utilise findAllFetch (nouvelle méthode optimisée)
        when(ressourceRepository.findAllFetch())
                .thenReturn(Arrays.asList(ressource));

        // Act
        List<RessourceResponseDTO> result = ressourceService.listerToutesRessources();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void listerRessourcesValidees_EmptyList() {
        when(ressourceRepository.findValideesFetch())
                .thenReturn(List.of());

        List<RessourceResponseDTO> result = ressourceService.listerRessourcesValidees();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}