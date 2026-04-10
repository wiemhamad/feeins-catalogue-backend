package com.feeins.catalogue.controller;

import com.feeins.catalogue.entity.Niveau;
import com.feeins.catalogue.repository.NiveauRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NiveauControllerTest {

    @Mock
    private NiveauRepository niveauRepository;

    @InjectMocks
    private NiveauController niveauController;

    private Niveau niveau;

    @BeforeEach
    void setUp() {
        niveau = new Niveau();
        niveau.setId(1L);
        niveau.setNom("CP");
    }

    @Test
    void listerNiveaux_ReturnsList() {
        // Arrange
        List<Niveau> niveaux = Arrays.asList(niveau);
        when(niveauRepository.findAll()).thenReturn(niveaux);

        // Act
        List<Niveau> result = niveauController.listerNiveaux();

        // Assert
        assertEquals(1, result.size());
        assertEquals("CP", result.get(0).getNom());
    }

    @Test
    void creerNiveau_ReturnsCreated() {
        // Arrange
        when(niveauRepository.save(any(Niveau.class))).thenReturn(niveau);

        // Act
        ResponseEntity<Niveau> response = niveauController.creerNiveau(niveau);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(niveau, response.getBody());
    }

    @Test
    void modifierNiveau_Existing_ReturnsOk() {
        // Arrange
        when(niveauRepository.findById(1L)).thenReturn(Optional.of(niveau));
        when(niveauRepository.save(any(Niveau.class))).thenReturn(niveau);

        Niveau updatedNiveau = new Niveau();
        updatedNiveau.setNom("CE1");

        // Act
        ResponseEntity<Niveau> response = niveauController.modifierNiveau(1L, updatedNiveau);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("CE1", response.getBody().getNom());
    }

    @Test
    void modifierNiveau_NotFound_ReturnsNotFound() {
        // Arrange
        when(niveauRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Niveau> response = niveauController.modifierNiveau(1L, niveau);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void supprimerNiveau_ReturnsNoContent() {
        // Act
        ResponseEntity<Void> response = niveauController.supprimerNiveau(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}