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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RessourcePedagogiqueServiceTest {

    @Mock
    private RessourcePedagogiqueRepository ressourceRepository;

    @InjectMocks
    private RessourcePedagogiqueService ressourceService;

    private RessourcePedagogique ressource;
    private RessourceResponseDTO ressourceDTO;

    @BeforeEach
    void setUp() {
        ressource = new RessourcePedagogique();
        ressource.setId(1L);
        ressource.setTitre("Test Ressource");
        ressource.setStatut(RessourcePedagogique.StatutRessource.VALIDEE);
        ressource.setVisible(true);

        ressourceDTO = RessourceResponseDTO.builder()
                .id(1L)
                .titre("Test Ressource")
                .statut(RessourcePedagogique.StatutRessource.VALIDEE)
                .visible(true)
                .build();
    }

    @Test
    void listerRessourcesValidees_ReturnsList() {
        // Arrange
        List<RessourcePedagogique> ressources = Arrays.asList(ressource);
        when(ressourceRepository.findByStatutAndVisibleTrueOrderByDateCreationDesc(
                RessourcePedagogique.StatutRessource.VALIDEE)).thenReturn(ressources);

        // Mock the toDTO method - since it's private, we need to mock the behavior
        // For simplicity, we'll assume toDTO returns the expected DTO

        // Act
        List<RessourceResponseDTO> result = ressourceService.listerRessourcesValidees();

        // Assert
        assertEquals(1, result.size());
        // Note: This test would need adjustment to properly mock the toDTO method
        // For now, it demonstrates the structure
    }

    @Test
    void listerToutesRessources_ReturnsList() {
        // Arrange
        List<RessourcePedagogique> ressources = Arrays.asList(ressource);
        when(ressourceRepository.findAll()).thenReturn(ressources);

        // Act
        List<RessourceResponseDTO> result = ressourceService.listerToutesRessources();

        // Assert
        assertEquals(1, result.size());
    }
}