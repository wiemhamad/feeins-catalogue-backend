package com.feeins.catalogue.service;

import com.feeins.catalogue.dto.*;
import com.feeins.catalogue.entity.*;
import com.feeins.catalogue.repository.*;
import com.feeins.catalogue.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UtilisateurRepository utilisateurRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;

    @Value("${app.teacher.access-code}")
    private String teacherAccessCode;

    // ===== CONNEXION =====
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getMotDePasse()));

        String jwt = jwtUtils.generateJwtToken(authentication);

        Utilisateur utilisateur = utilisateurRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Utilisateur.TypeUtilisateur type = detecterType(utilisateur);

        return AuthResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .email(utilisateur.getEmail())
                .typeUtilisateur(type)
                .build();
    }

    // ===== DÉTECTER LE TYPE RÉEL DE L'UTILISATEUR =====
    private Utilisateur.TypeUtilisateur detecterType(Utilisateur u) {
        if (u instanceof AdministrateurPedagogique)
            return Utilisateur.TypeUtilisateur.ADMINISTRATEUR_PEDAGOGIQUE;
        if (u instanceof Enseignant)
            return Utilisateur.TypeUtilisateur.ENSEIGNANT;
        if (u instanceof Contributeur)
            return Utilisateur.TypeUtilisateur.CONTRIBUTEUR;
        if (u instanceof Etudiant)
            return Utilisateur.TypeUtilisateur.ETUDIANT;
        if (u instanceof ConsultantExterne)
            return Utilisateur.TypeUtilisateur.CONSULTANT_EXTERNE;
        // Fallback sur le discriminateur
        if (u.getTypeUtilisateur() != null)
            return u.getTypeUtilisateur();
        return Utilisateur.TypeUtilisateur.ETUDIANT;
    }

    // ===== INSCRIPTION =====
    public AuthResponse register(RegisterRequest request) {
        if (utilisateurRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé : " + request.getEmail());
        }

        Utilisateur.TypeUtilisateur type = request.getTypeUtilisateur() != null
                ? request.getTypeUtilisateur()
                : Utilisateur.TypeUtilisateur.ETUDIANT;

        // Rôles nécessitant un code d'accès :
        // - ENSEIGNANT (crée des templates)
        // - CONTRIBUTEUR (crée des ressources / consultant pédagogique)
        // - ADMINISTRATEUR_PEDAGOGIQUE (valide les ressources)
        boolean rolePrivilegie = type == Utilisateur.TypeUtilisateur.ENSEIGNANT
                || type == Utilisateur.TypeUtilisateur.CONTRIBUTEUR
                || type == Utilisateur.TypeUtilisateur.ADMINISTRATEUR_PEDAGOGIQUE;

        if (rolePrivilegie) {
            if (request.getCodeAccesEnseignant() == null ||
                    !request.getCodeAccesEnseignant().equals(teacherAccessCode)) {
                throw new RuntimeException("Code d'accès invalide ou manquant");
            }
        }

        Utilisateur utilisateur = creerUtilisateur(request, type);
        utilisateur = utilisateurRepo.save(utilisateur);

        String jwt = jwtUtils.generateTokenFromEmail(utilisateur.getEmail());

        Utilisateur.TypeUtilisateur typeDetecte = detecterType(utilisateur);

        return AuthResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .email(utilisateur.getEmail())
                .typeUtilisateur(typeDetecte)
                .build();
    }

    // ===== CRÉATION ENTITÉ SELON TYPE =====
    private Utilisateur creerUtilisateur(RegisterRequest request,
            Utilisateur.TypeUtilisateur type) {
        String mdp = passwordEncoder.encode(request.getMotDePasse());

        return switch (type) {
            case ENSEIGNANT -> {
                Enseignant e = new Enseignant();
                e.setNom(request.getNom());
                e.setEmail(request.getEmail());
                e.setMotDePasse(mdp);
                e.setRole(request.getRole());
                e.setSpecialite(request.getSpecialite());
                yield e;
            }
            case CONTRIBUTEUR -> {
                // Le Contributeur = consultant pédagogique : crée et propose des ressources
                Contributeur c = new Contributeur();
                c.setNom(request.getNom());
                c.setEmail(request.getEmail());
                c.setMotDePasse(mdp);
                c.setOrganisation(request.getOrganisation());
                yield c;
            }
            case ADMINISTRATEUR_PEDAGOGIQUE -> {
                AdministrateurPedagogique a = new AdministrateurPedagogique();
                a.setNom(request.getNom());
                a.setEmail(request.getEmail());
                a.setMotDePasse(mdp);
                yield a;
            }
            // ETUDIANT et CONSULTANT_EXTERNE : pas besoin de s'authentifier pour consulter
            // mais peuvent créer un compte pour accéder à des fonctionnalités futures
            default -> {
                Etudiant et = new Etudiant();
                et.setNom(request.getNom());
                et.setEmail(request.getEmail());
                et.setMotDePasse(mdp);
                yield et;
            }
        };
    }
}