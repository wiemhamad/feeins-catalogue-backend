package com.feeins.catalogue.controller;

import com.feeins.catalogue.entity.Tag;
import com.feeins.catalogue.repository.TagRepository;
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
class TagControllerTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagController tagController;

    private Tag tag;

    @BeforeEach
    void setUp() {
        tag = new Tag();
        tag.setId(1L);
        tag.setLibelle("Mathématiques");
    }

    @Test
    void listerTags_ReturnsList() {
        // Arrange
        List<Tag> tags = Arrays.asList(tag);
        when(tagRepository.findAll()).thenReturn(tags);

        // Act
        List<Tag> result = tagController.listerTags();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Mathématiques", result.get(0).getLibelle());
    }

    @Test
    void getTag_Existing_ReturnsOk() {
        // Arrange
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        // Act
        ResponseEntity<Tag> response = tagController.getTag(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tag, response.getBody());
    }

    @Test
    void getTag_NotFound_ReturnsNotFound() {
        // Arrange
        when(tagRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Tag> response = tagController.getTag(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void creerTag_NewTag_ReturnsCreated() {
        // Arrange
        when(tagRepository.existsByLibelle("Mathématiques")).thenReturn(false);
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        // Act
        ResponseEntity<Tag> response = tagController.creerTag(tag);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(tag, response.getBody());
    }

    @Test
    void creerTag_ExistingLibelle_ReturnsConflict() {
        // Arrange
        when(tagRepository.existsByLibelle("Mathématiques")).thenReturn(true);

        // Act
        ResponseEntity<Tag> response = tagController.creerTag(tag);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void modifierTag_Existing_ReturnsOk() {
        // Arrange
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        Tag updatedTag = new Tag();
        updatedTag.setLibelle("Géométrie");

        // Act
        ResponseEntity<Tag> response = tagController.modifierTag(1L, updatedTag);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Géométrie", response.getBody().getLibelle());
    }

    @Test
    void modifierTag_NotFound_ReturnsNotFound() {
        // Arrange
        when(tagRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Tag> response = tagController.modifierTag(1L, tag);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void supprimerTag_ReturnsNoContent() {
        // Act
        ResponseEntity<Void> response = tagController.supprimerTag(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}