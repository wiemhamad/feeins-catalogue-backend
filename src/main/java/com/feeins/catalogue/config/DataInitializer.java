package com.feeins.catalogue.config;

import com.feeins.catalogue.entity.*;
import com.feeins.catalogue.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private NiveauRepository niveauRepo;
    @Autowired
    private ThematiqueRepository thematiqueRepo;
    @Autowired
    private TagRepository tagRepo;
    @Autowired
    private TemplatePedagogiqueRepository templateRepo;
    @Autowired
    private UtilisateurRepository utilisateurRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.teacher.access-code}")
    private String teacherAccessCode;

    @Override
    public void run(String... args) {

        if (niveauRepo.count() == 0) {
            niveauRepo.save(new Niveau(null, "L3"));
            niveauRepo.save(new Niveau(null, "Master"));
            niveauRepo.save(new Niveau(null, "Ingénieur"));
            niveauRepo.save(new Niveau(null, "Débutant"));
            niveauRepo.save(new Niveau(null, "Avancé"));
            niveauRepo.save(new Niveau(null, "Enseignants"));
            System.out.println("✅ Niveaux initialisés");
        }

        if (thematiqueRepo.count() == 0) {
            thematiqueRepo.save(new Thematique(null, "Santé numérique"));
            thematiqueRepo.save(new Thematique(null, "Intelligence Artificielle"));
            thematiqueRepo.save(new Thematique(null, "RGPD & Données"));
            thematiqueRepo.save(new Thematique(null, "Cybersécurité"));
            thematiqueRepo.save(new Thematique(null, "Télémédecine"));
            thematiqueRepo.save(new Thematique(null, "Interopérabilité"));
            System.out.println("✅ Thématiques initialisées");
        }

        if (tagRepo.count() == 0) {
            tagRepo.save(new Tag(null, "rgpd"));
            tagRepo.save(new Tag(null, "ia"));
            tagRepo.save(new Tag(null, "santé"));
            tagRepo.save(new Tag(null, "vidéo"));
            tagRepo.save(new Tag(null, "quiz"));
            tagRepo.save(new Tag(null, "interactif"));
            tagRepo.save(new Tag(null, "feeins"));
            tagRepo.save(new Tag(null, "numérique"));
            System.out.println("✅ Tags initialisés");
        }

        if (templateRepo.count() == 0) {
            templateRepo.save(new TemplatePedagogique(null, "Capsule vidéo seule",
                    "Une vidéo pédagogique autonome de 5 à 15 minutes", true, null));
            templateRepo.save(new TemplatePedagogique(null, "Vidéo + Quiz",
                    "Une vidéo suivie d'un quiz de vérification des acquis", true, null));
            templateRepo.save(new TemplatePedagogique(null, "Cours 30 min clé en main",
                    "Module complet non modifiable", false, null));
            templateRepo.save(new TemplatePedagogique(null, "Activité H5P interactive",
                    "Contenu interactif H5P avec feedback immédiat", true, null));
            System.out.println("✅ Templates initialisés");
        }

        if (utilisateurRepo.count() == 0) {
            AdministrateurPedagogique admin = new AdministrateurPedagogique();
            admin.setNom("Admin FEEINS");
            admin.setEmail("admin@feeins.fr");
            admin.setMotDePasse(passwordEncoder.encode("admin123"));
            utilisateurRepo.save(admin);

            Enseignant enseignant = new Enseignant();
            enseignant.setNom("Marion Collaro");
            enseignant.setEmail("marion@feeins.fr");
            enseignant.setMotDePasse(passwordEncoder.encode("prof123"));
            enseignant.setRole("Ingénieur pédagogique");
            enseignant.setSpecialite("Santé numérique");
            utilisateurRepo.save(enseignant);

            Etudiant etudiant = new Etudiant();
            etudiant.setNom("Étudiant Demo");
            etudiant.setEmail("etudiant@feeins.fr");
            etudiant.setMotDePasse(passwordEncoder.encode("etudiant123"));
            utilisateurRepo.save(etudiant);

            System.out.println("✅ Utilisateurs créés");
            System.out.println("   → admin@feeins.fr / admin123");
            System.out.println("   → marion@feeins.fr / prof123");
            System.out.println("   → Code d'accès : " + teacherAccessCode);
        }
        if (tagRepo.count() == 0) {
            // Thématiques FEEINS
            tagRepo.save(new Tag(null, "si-sante"));
            tagRepo.save(new Tag(null, "interoperabilite"));
            tagRepo.save(new Tag(null, "iot"));
            tagRepo.save(new Tag(null, "ia-sante"));
            tagRepo.save(new Tag(null, "rgpd"));
            tagRepo.save(new Tag(null, "telemedicine"));
            tagRepo.save(new Tag(null, "parcours-soins"));
            tagRepo.save(new Tag(null, "ethique"));

            // Types de contenu
            tagRepo.save(new Tag(null, "h5p-cle-en-main"));
            tagRepo.save(new Tag(null, "h5p-modifiable"));
            tagRepo.save(new Tag(null, "cours-cle-en-main"));
            tagRepo.save(new Tag(null, "cours-modifiable"));
            tagRepo.save(new Tag(null, "dialog-cards"));

            // Usage pédagogique
            tagRepo.save(new Tag(null, "evaluation-formative"));
            tagRepo.save(new Tag(null, "evaluation-sommative"));
            tagRepo.save(new Tag(null, "test-diagnostique"));
            tagRepo.save(new Tag(null, "glossaire"));
            tagRepo.save(new Tag(null, "banque-questions"));

            // Parcours
            tagRepo.save(new Tag(null, "decouvrir"));
            tagRepo.save(new Tag(null, "approfondir"));
            tagRepo.save(new Tag(null, "expertise"));

            System.out.println("✅ Tags FEEINS initialisés");
        }
    }
}