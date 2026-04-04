package com.feeins.catalogue.config;

import com.feeins.catalogue.entity.*;
import com.feeins.catalogue.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Initialisation des données de référence au démarrage.
 *
 * Comptes créés :
 * - admin@feeins.fr / admin123 → ADMINISTRATEUR_PEDAGOGIQUE (valide les
 * ressources)
 * - marion@feeins.fr / prof123 → ENSEIGNANT (crée des templates)
 * - contrib@feeins.fr / contrib123 → CONTRIBUTEUR (crée et propose des
 * ressources)
 *
 * Accès public (sans compte) :
 * - Consultation, recherche, listing des ressources validées
 */
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

        // ===== NIVEAUX =====
        if (niveauRepo.count() == 0) {
            niveauRepo.save(new Niveau(null, "L3"));
            niveauRepo.save(new Niveau(null, "Master"));
            niveauRepo.save(new Niveau(null, "Ingénieur"));
            niveauRepo.save(new Niveau(null, "Débutant"));
            niveauRepo.save(new Niveau(null, "Avancé"));
            niveauRepo.save(new Niveau(null, "Enseignants"));
            System.out.println("✅ Niveaux initialisés");
        }

        // ===== THÉMATIQUES =====
        if (thematiqueRepo.count() == 0) {
            thematiqueRepo.save(new Thematique(null, "Santé numérique"));
            thematiqueRepo.save(new Thematique(null, "Intelligence Artificielle"));
            thematiqueRepo.save(new Thematique(null, "RGPD & Données"));
            thematiqueRepo.save(new Thematique(null, "Cybersécurité"));
            thematiqueRepo.save(new Thematique(null, "Télémédecine"));
            thematiqueRepo.save(new Thematique(null, "Interopérabilité"));
            thematiqueRepo.save(new Thematique(null, "Système d'information de santé"));
            thematiqueRepo.save(new Thematique(null, "IoT Internet des objets"));
            thematiqueRepo.save(new Thematique(null, "Gestion de parcours de soins"));
            thematiqueRepo.save(new Thematique(null, "Éthique et réglementation"));
            System.out.println("✅ Thématiques initialisées");
        }

        // ===== TAGS =====
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
            tagRepo.save(new Tag(null, "cybersecurite"));
            tagRepo.save(new Tag(null, "dpi"));

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

            // Parcours FEEINS
            tagRepo.save(new Tag(null, "decouvrir-esante"));
            tagRepo.save(new Tag(null, "approfondir"));
            tagRepo.save(new Tag(null, "expertise-metier"));

            System.out.println("✅ Tags FEEINS initialisés");
        }

        // ===== TEMPLATES (exemples de base — créés sans enseignant créateur) =====
        if (templateRepo.count() == 0) {
            templateRepo.save(new TemplatePedagogique(null,
                    "Capsule vidéo seule",
                    "Une vidéo pédagogique autonome de 5 à 15 minutes",
                    true, null, null));
            templateRepo.save(new TemplatePedagogique(null,
                    "Vidéo + Quiz",
                    "Une vidéo suivie d'un quiz de vérification des acquis",
                    true, null, null));
            templateRepo.save(new TemplatePedagogique(null,
                    "Cours 30 min clé en main",
                    "Module complet non modifiable",
                    false, null, null));
            templateRepo.save(new TemplatePedagogique(null,
                    "Activité H5P interactive",
                    "Contenu interactif H5P avec feedback immédiat",
                    true, null, null));
            System.out.println("✅ Templates initialisés");
        }

        // ===== UTILISATEURS =====
        if (utilisateurRepo.count() == 0) {

            // ADMINISTRATEUR PÉDAGOGIQUE — valide ou refuse les ressources
            AdministrateurPedagogique admin = new AdministrateurPedagogique();
            admin.setNom("Admin FEEINS");
            admin.setEmail("admin@feeins.fr");
            admin.setMotDePasse(passwordEncoder.encode("admin123"));
            utilisateurRepo.save(admin);

            // ENSEIGNANT — crée des templates à partir de ressources validées existantes
            Enseignant enseignant = new Enseignant();
            enseignant.setNom("Marion Collaro");
            enseignant.setEmail("marion@feeins.fr");
            enseignant.setMotDePasse(passwordEncoder.encode("prof123"));
            enseignant.setRole("Ingénieur pédagogique");
            enseignant.setSpecialite("Santé numérique");
            utilisateurRepo.save(enseignant);

            // CONTRIBUTEUR — crée et propose des ressources (consultant pédagogique)
            Contributeur contributeur = new Contributeur();
            contributeur.setNom("Contributeur Demo");
            contributeur.setEmail("contrib@feeins.fr");
            contributeur.setMotDePasse(passwordEncoder.encode("contrib123"));
            contributeur.setOrganisation("FEEINS Partenaire");
            utilisateurRepo.save(contributeur);

            System.out.println("✅ Utilisateurs créés :");
            System.out.println("   → admin@feeins.fr    / admin123   (ADMINISTRATEUR_PEDAGOGIQUE)");
            System.out.println("   → marion@feeins.fr   / prof123    (ENSEIGNANT → crée des templates)");
            System.out.println("   → contrib@feeins.fr  / contrib123 (CONTRIBUTEUR → crée des ressources)");
            System.out.println("   → Code d'accès privilégié : " + teacherAccessCode);
            System.out.println("   ℹ️  Les étudiants et visiteurs consultent SANS compte.");
        }
    }
}
