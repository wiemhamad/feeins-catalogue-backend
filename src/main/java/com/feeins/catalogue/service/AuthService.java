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
    private EnseignantRepository enseignantRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;

    @Value("${app.teacher.access-code}")
    private String teacherAccessCode;

    // ===== CONNEXION =====
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse()));

        String jwt = jwtUtils.generateJwtToken(authentication);

        Utilisateur utilisateur = utilisateurRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        return AuthResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .email(utilisateur.getEmail())
                .typeUtilisateur(utilisateur.getTypeUtilisateur())
                .build();
    }

    // ===== INSCRIPTION =====
    public AuthResponse register(RegisterRequest request) {
        // Vérifier email unique
        if (utilisateurRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé : " + request.getEmail());
        }

        Utilisateur.TypeUtilisateur type = request.getTypeUtilisateur() != null
                ? request.getTypeUtilisateur()
                : Utilisateur.TypeUtilisateur.ETUDIANT;

        // VÉRIFICATION DU CODE D'ACCÈS pour Enseignant et Admin Péda
        boolean rolePrivilegie = type == Utilisateur.TypeUtilisateur.ENSEIGNANT
                || type == Utilisateur.TypeUtilisateur.ADMINISTRATEUR_PEDAGOGIQUE;

        if (rolePrivilegie) {
            if (request.getCodeAccesEnseignant() == null ||
                    !request.getCodeAccesEnseignant().equals(teacherAccessCode)) {
                throw new RuntimeException("Code d'accès enseignant invalide ou manquant");
            }
        }

        Utilisateur utilisateur = creerUtilisateur(request, type);
        utilisateur = utilisateurRepo.save(utilisateur);

        String jwt = jwtUtils.generateTokenFromEmail(utilisateur.getEmail());

        return AuthResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .email(utilisateur.getEmail())
                .typeUtilisateur(utilisateur.getTypeUtilisateur())
                .build();
    }

    // ===== CRÉATION ENTITÉ SELON TYPE =====
    private Utilisateur creerUtilisateur(RegisterRequest request, Utilisateur.TypeUtilisateur type) {
        String motDePasseEncode = passwordEncoder.encode(request.getMotDePasse());

        return switch (type) {
            case ENSEIGNANT -> {
                Enseignant e = new Enseignant();
                e.setNom(request.getNom());
                e.setEmail(request.getEmail());
                e.setMotDePasse(motDePasseEncode);
                e.setRole(request.getRole());
                e.setSpecialite(request.getSpecialite());
                yield e;
            }
            case ADMINISTRATEUR_PEDAGOGIQUE -> {
                AdministrateurPedagogique a = new AdministrateurPedagogique();
                a.setNom(request.getNom());
                a.setEmail(request.getEmail());
                a.setMotDePasse(motDePasseEncode);
                yield a;
            }
            case CONTRIBUTEUR -> {
                Contributeur c = new Contributeur();
                c.setNom(request.getNom());
                c.setEmail(request.getEmail());
                c.setMotDePasse(motDePasseEncode);
                yield c;
            }
            default -> {
                Etudiant et = new Etudiant();
                et.setNom(request.getNom());
                et.setEmail(request.getEmail());
                et.setMotDePasse(motDePasseEncode);
                yield et;
            }
        };
    }
}