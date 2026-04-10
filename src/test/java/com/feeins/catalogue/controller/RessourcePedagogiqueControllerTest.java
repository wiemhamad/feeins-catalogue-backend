package com.feeins.catalogue.controller;

import com.feeins.catalogue.dto.RechercheRequestDTO;
import com.feeins.catalogue.dto.RessourceRequestDTO;
import com.feeins.catalogue.dto.RessourceResponseDTO;
import com.feeins.catalogue.entity.RessourcePedagogique;
import com.feeins.catalogue.repository.RessourcePedagogiqueRepository;
import com.feeins.catalogue.service.RessourcePedagogiqueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RessourcePedagogiqueControllerTest {

    @Mock
    private RessourcePedagogiqueService ressourceService;

    @Mock
    private RessourcePedagogiqueRepository ressourceRepository;

    @InjectMocks
    private RessourcePedagogiqueController ressourceController;

    private RessourceResponseDTO ressourceResponse;
    private RessourceRequestDTO ressourceRequest;
    private RechercheRequestDTO rechercheRequest;

    @BeforeEach
    void setUp() {
        ressourceResponse = RessourceResponseDTO.builder()
                .id(1L)
                .titre("Test Ressource")
                .description("Description test")
                .statut(RessourcePedagogique.StatutRessource.VALIDEE)
                .visible(true)
                .build();

        ressourceRequest = new RessourceRequestDTO();
        ressourceRequest.setTitre("Test Ressource");
        ressourceRequest.setDescription("Description test");

        rechercheRequest = new RechercheRequestDTO();
        rechercheRequest.setKeyword("test");
    }

    @Test
    void listerRessourcesValidees_ReturnsList() {
        // Arrange
        List<RessourceResponseDTO> ressources = Arrays.asList(ressourceResponse);
        when(ressourceService.listerRessourcesValidees()).thenReturn(ressources);

        // Act
        ResponseEntity<List<RessourceResponseDTO>> response = ressourceController.listerRessourcesValidees();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Ressource", response.getBody().get(0).getTitre());
    }

    @Test
    void consulterRessource_Existing_ReturnsOk() {
        // Arrange
        when(ressourceService.consulterRessource(1L)).thenReturn(ressourceResponse);

        // Act
        ResponseEntity<?> response = ressourceController.consulterRessource(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ressourceResponse, response.getBody());
    }

    @Test
    void consulterRessource_NotFound_ReturnsNotFound() {
        // Arrange
        when(ressourceService.consulterRessource(1L)).thenThrow(new RuntimeException("Ressource introuvable"));

        // Act
        ResponseEntity<?> response = ressourceController.consulterRessource(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Ressource introuvable", response.getBody());
    }

    @Test
    void rechercherRessources_ReturnsList() {
        // Arrange
        List<RessourceResponseDTO> ressources = Arrays.asList(ressourceResponse);
        when(ressourceService.rechercherRessources(any(RechercheRequestDTO.class))).thenReturn(ressources);

        // Act
        ResponseEntity<List<RessourceResponseDTO>> response = ressourceController
                .rechercherRessources(rechercheRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void listerToutesRessources_ReturnsList() {
        // Arrange
        List<RessourceResponseDTO> ressources = Arrays.asList(ressourceResponse);
        when(ressourceService.listerToutesRessources()).thenReturn(ressources);

        // Act
        ResponseEntity<List<RessourceResponseDTO>> response = ressourceController.listerToutesRessources();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void validerRessource_Success_ReturnsOk() {
        // Arrange
        when(ressourceService.validerRessource(1L)).thenReturn(ressourceResponse);

        // Act
        ResponseEntity<?> response = ressourceController.validerRessource(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ressourceResponse, response.getBody());
    }

    @Test
    void validerRessource_NotFound_ReturnsNotFound() {
        // Arrange
        when(ressourceService.validerRessource(1L)).thenThrow(new RuntimeException("Ressource introuvable"));

        // Act
        ResponseEntity<?> response = ressourceController.validerRessource(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Ressource introuvable", response.getBody());
    }

    @Test
    void refuserRessource_Success_ReturnsOk() {
        // Arrange
        when(ressourceService.refuserRessource(1L)).thenReturn(ressourceResponse);

        // Act
        ResponseEntity<?> response = ressourceController.refuserRessource(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ressourceResponse, response.getBody());
    }

    @Test
    void creerRessource_Success_ReturnsCreated() {
        // Arrange
        when(ressourceService.creerRessource(any(RessourceRequestDTO.class))).thenReturn(ressourceResponse);

        // Act
        ResponseEntity<?> response = ressourceController.creerRessource(ressourceRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(ressourceResponse, response.getBody());
    }

    @Test
    void creerRessource_Failure_ReturnsBadRequest() {
        // Arrange
        when(ressourceService.creerRessource(any(RessourceRequestDTO.class)))
                .thenThrow(new RuntimeException("Erreur de création"));

        // Act
        ResponseEntity<?> response = ressourceController.creerRessource(ressourceRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Erreur de création", response.getBody());
    }
}