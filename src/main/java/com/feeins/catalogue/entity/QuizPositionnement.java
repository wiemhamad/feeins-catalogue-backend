package com.feeins.catalogue.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz_positionnements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizPositionnement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false)
    private String niveauCible; // Le niveau visé par ce quiz

    @ManyToMany
    @JoinTable(name = "utilisateur_quiz", joinColumns = @JoinColumn(name = "quiz_id"), inverseJoinColumns = @JoinColumn(name = "utilisateur_id"))
    private List<Utilisateur> utilisateurs = new ArrayList<>();
}