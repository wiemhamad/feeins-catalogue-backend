package com.feeins.catalogue.config;

import com.feeins.catalogue.entity.*;
import com.feeins.catalogue.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Initialisation des données de référence au démarrage.
 *
 * Comptes créés :
 * - admin@feeins.fr / admin123 → ADMINISTRATEUR_PEDAGOGIQUE
 * - marion@feeins.fr / prof123 → ENSEIGNANT
 * - contrib@feeins.fr / contrib123 → CONTRIBUTEUR
 *
 * Ressources fictives FEEINS créées pour démonstration.
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
    private RessourcePedagogiqueRepository ressourceRepo;
    @Autowired
    private ContributeurRepository contributeurRepo;
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
            tagRepo.save(new Tag(null, "h5p-cle-en-main"));
            tagRepo.save(new Tag(null, "h5p-modifiable"));
            tagRepo.save(new Tag(null, "cours-cle-en-main"));
            tagRepo.save(new Tag(null, "cours-modifiable"));
            tagRepo.save(new Tag(null, "dialog-cards"));
            tagRepo.save(new Tag(null, "evaluation-formative"));
            tagRepo.save(new Tag(null, "evaluation-sommative"));
            tagRepo.save(new Tag(null, "test-diagnostique"));
            tagRepo.save(new Tag(null, "glossaire"));
            tagRepo.save(new Tag(null, "banque-questions"));
            tagRepo.save(new Tag(null, "decouvrir-esante"));
            tagRepo.save(new Tag(null, "approfondir"));
            tagRepo.save(new Tag(null, "expertise-metier"));
            System.out.println("✅ Tags FEEINS initialisés");
        }

        // ===== TEMPLATES =====
        if (templateRepo.count() == 0) {
            templateRepo.save(new TemplatePedagogique(null, "Capsule vidéo seule",
                    "Une vidéo pédagogique autonome de 5 à 15 minutes", true, null, null));
            templateRepo.save(new TemplatePedagogique(null, "Vidéo + Quiz",
                    "Une vidéo suivie d'un quiz de vérification des acquis", true, null, null));
            templateRepo.save(new TemplatePedagogique(null, "Cours 30 min clé en main", "Module complet non modifiable",
                    false, null, null));
            templateRepo.save(new TemplatePedagogique(null, "Activité H5P interactive",
                    "Contenu interactif H5P avec feedback immédiat", true, null, null));
            System.out.println("✅ Templates initialisés");
        }

        // ===== UTILISATEURS =====
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

            Contributeur contributeur = new Contributeur();
            contributeur.setNom("Contributeur Demo");
            contributeur.setEmail("contrib@feeins.fr");
            contributeur.setMotDePasse(passwordEncoder.encode("contrib123"));
            contributeur.setOrganisation("FEEINS Partenaire");
            utilisateurRepo.save(contributeur);

            System.out.println("✅ Utilisateurs créés :");
            System.out.println("   → admin@feeins.fr    / admin123   (ADMINISTRATEUR_PEDAGOGIQUE)");
            System.out.println("   → marion@feeins.fr   / prof123    (ENSEIGNANT)");
            System.out.println("   → contrib@feeins.fr  / contrib123 (CONTRIBUTEUR)");
        }

        // ===== RESSOURCES FICTIVES FEEINS =====
        if (ressourceRepo.count() == 0) {
            Contributeur contrib = contributeurRepo.findByEmail("contrib@feeins.fr").orElse(null);
            Niveau niveauL3 = niveauRepo.findAll().stream().filter(n -> n.getNom().equals("L3")).findFirst()
                    .orElse(null);
            Niveau niveauMaster = niveauRepo.findAll().stream().filter(n -> n.getNom().equals("Master")).findFirst()
                    .orElse(null);
            Niveau niveauDebutant = niveauRepo.findAll().stream().filter(n -> n.getNom().equals("Débutant")).findFirst()
                    .orElse(null);

            Thematique themSIS = thematiqueRepo.findAll().stream()
                    .filter(t -> t.getNom().contains("Système d'information")).findFirst().orElse(null);
            Thematique themIA = thematiqueRepo.findAll().stream()
                    .filter(t -> t.getNom().contains("Intelligence Artificielle")).findFirst().orElse(null);
            Thematique themRGPD = thematiqueRepo.findAll().stream().filter(t -> t.getNom().contains("RGPD")).findFirst()
                    .orElse(null);
            Thematique themInterop = thematiqueRepo.findAll().stream()
                    .filter(t -> t.getNom().contains("Interopérabilité")).findFirst().orElse(null);
            Thematique themEthique = thematiqueRepo.findAll().stream().filter(t -> t.getNom().contains("Éthique"))
                    .findFirst().orElse(null);
            Thematique themIoT = thematiqueRepo.findAll().stream().filter(t -> t.getNom().contains("IoT")).findFirst()
                    .orElse(null);
            Thematique themTele = thematiqueRepo.findAll().stream().filter(t -> t.getNom().contains("Télémédecine"))
                    .findFirst().orElse(null);
            Thematique themParcours = thematiqueRepo.findAll().stream()
                    .filter(t -> t.getNom().contains("Gestion de parcours")).findFirst().orElse(null);

            Tag tagSIS = tagRepo.findAll().stream().filter(t -> t.getLibelle().equals("si-sante")).findFirst()
                    .orElse(null);
            Tag tagInterop = tagRepo.findAll().stream().filter(t -> t.getLibelle().equals("interoperabilite"))
                    .findFirst().orElse(null);
            Tag tagIA = tagRepo.findAll().stream().filter(t -> t.getLibelle().equals("ia-sante")).findFirst()
                    .orElse(null);
            Tag tagRGPD = tagRepo.findAll().stream().filter(t -> t.getLibelle().equals("rgpd")).findFirst()
                    .orElse(null);
            Tag tagDPI = tagRepo.findAll().stream().filter(t -> t.getLibelle().equals("dpi")).findFirst().orElse(null);
            Tag tagEthique = tagRepo.findAll().stream().filter(t -> t.getLibelle().equals("ethique")).findFirst()
                    .orElse(null);
            Tag tagIoT = tagRepo.findAll().stream().filter(t -> t.getLibelle().equals("iot")).findFirst().orElse(null);
            Tag tagH5P = tagRepo.findAll().stream().filter(t -> t.getLibelle().equals("h5p-cle-en-main")).findFirst()
                    .orElse(null);
            Tag tagDecouvrir = tagRepo.findAll().stream().filter(t -> t.getLibelle().equals("decouvrir-esante"))
                    .findFirst().orElse(null);
            Tag tagApprofondir = tagRepo.findAll().stream().filter(t -> t.getLibelle().equals("approfondir"))
                    .findFirst().orElse(null);
            Tag tagEvalForm = tagRepo.findAll().stream().filter(t -> t.getLibelle().equals("evaluation-formative"))
                    .findFirst().orElse(null);
            Tag tagGlossaire = tagRepo.findAll().stream().filter(t -> t.getLibelle().equals("glossaire")).findFirst()
                    .orElse(null);
            Tag tagCyber = tagRepo.findAll().stream().filter(t -> t.getLibelle().equals("cybersecurite")).findFirst()
                    .orElse(null);
            Tag tagTele = tagRepo.findAll().stream().filter(t -> t.getLibelle().equals("telemedicine")).findFirst()
                    .orElse(null);
            Tag tagParcours = tagRepo.findAll().stream().filter(t -> t.getLibelle().equals("parcours-soins"))
                    .findFirst().orElse(null);

            // --- 1. Introduction e-santé (VIDEO, VALIDEE) ---
            RessourcePedagogique r1 = new RessourcePedagogique();
            r1.setTitre("Introduction à la e-santé");
            r1.setDescription(
                    "Panorama de la santé numérique en France : définitions, acteurs, enjeux réglementaires et perspectives. Idéal pour débuter le parcours FEEINS.");
            r1.setTypeSupport(RessourcePedagogique.TypeSupport.VIDEO);
            r1.setDureeMinutes(12);
            r1.setDifficulte(RessourcePedagogique.Difficulte.DEBUTANT);
            r1.setNiveau(niveauDebutant);
            r1.setThematique(themSIS);
            r1.setUsagePedagogique(RessourcePedagogique.UsagePedagogique.COURS);
            r1.setStatut(RessourcePedagogique.StatutRessource.VALIDEE);
            r1.setVisible(true);
            r1.setUrlAcces("https://moodle.univ-jfc.fr/mod/resource/view.php?id=1001");
            r1.setObjectifsPedagogiques("Comprendre les composantes de la e-santé et ses acteurs institutionnels");
            r1.setCompetencesVisees("Situer la e-santé dans le système de santé français");
            r1.setUsageMoodle("Parcours-Decouvrir-L3, Intro-SIS-Master");
            r1.setDroits(RessourcePedagogique.Droits.FEEINS_INTERNE);
            r1.setAuteurPartenaire("ISIS / Marion Collaro");
            r1.setNomenclature("FEEINS-VIDEO-DEBUTANT-0001");
            r1.setDerniereVerification(LocalDate.of(2025, 9, 1));
            r1.setContributeur(contrib);
            r1.setTags(List.of(tagSIS, tagDecouvrir).stream().filter(t -> t != null).toList());
            ressourceRepo.save(r1);

            // --- 2. Le Dossier Patient Informatisé (DPI) — H5P interactif ---
            RessourcePedagogique r2 = new RessourcePedagogique();
            r2.setTitre("Le Dossier Patient Informatisé (DPI) — Activité H5P");
            r2.setDescription(
                    "Activité interactive H5P permettant de comprendre la structure, les composantes et les usages du DPI dans un établissement de santé. Dialog cards avec cas concrets.");
            r2.setTypeSupport(RessourcePedagogique.TypeSupport.H5P);
            r2.setDureeMinutes(25);
            r2.setDifficulte(RessourcePedagogique.Difficulte.INTERMEDIAIRE);
            r2.setNiveau(niveauL3);
            r2.setThematique(themSIS);
            r2.setUsagePedagogique(RessourcePedagogique.UsagePedagogique.ACTIVITE);
            r2.setStatut(RessourcePedagogique.StatutRessource.VALIDEE);
            r2.setVisible(true);
            r2.setUrlAcces("https://moodle.univ-jfc.fr/mod/hvp/view.php?id=2042");
            r2.setObjectifsPedagogiques("Identifier les fonctionnalités principales d'un DPI, distinguer DPI/DSP/DMP");
            r2.setCompetencesVisees("Analyser un système DPI dans un contexte hospitalier");
            r2.setUsageMoodle("SIS-L3-TD2, Master-Interop-CM1");
            r2.setDroits(RessourcePedagogique.Droits.FEEINS_INTERNE);
            r2.setAuteurPartenaire("FEEINS / Sylvain Barreau");
            r2.setNomenclature("FEEINS-H5P-L3-0001");
            r2.setDerniereVerification(LocalDate.of(2025, 9, 15));
            r2.setContributeur(contrib);
            r2.setTags(List.of(tagSIS, tagDPI, tagH5P, tagApprofondir).stream().filter(t -> t != null).toList());
            ressourceRepo.save(r2);

            // --- 3. RGPD et données de santé — PDF cours ---
            RessourcePedagogique r3 = new RessourcePedagogique();
            r3.setTitre("RGPD et données de santé — Support de cours");
            r3.setDescription(
                    "Support de cours complet sur l'application du RGPD aux données de santé : catégories particulières, bases légales, droits des patients, CNIL et DPO en établissement de santé.");
            r3.setTypeSupport(RessourcePedagogique.TypeSupport.PDF);
            r3.setDureeMinutes(45);
            r3.setDifficulte(RessourcePedagogique.Difficulte.INTERMEDIAIRE);
            r3.setNiveau(niveauMaster);
            r3.setThematique(themRGPD);
            r3.setUsagePedagogique(RessourcePedagogique.UsagePedagogique.COURS);
            r3.setStatut(RessourcePedagogique.StatutRessource.VALIDEE);
            r3.setVisible(true);
            r3.setUrlAcces("https://moodle.univ-jfc.fr/mod/resource/view.php?id=3011");
            r3.setObjectifsPedagogiques("Maîtriser les obligations RGPD spécifiques au secteur de la santé");
            r3.setCompetencesVisees(
                    "Identifier les risques RGPD, élaborer une politique de protection des données de santé");
            r3.setUsageMoodle("RGPD-Master-CM1, Ingenieur-Ethique-TD1");
            r3.setDroits(RessourcePedagogique.Droits.FEEINS_INTERNE);
            r3.setAuteurPartenaire("ISIS");
            r3.setNomenclature("FEEINS-PDF-MASTER-0001");
            r3.setDerniereVerification(LocalDate.of(2025, 10, 1));
            r3.setContributeur(contrib);
            r3.setTags(List.of(tagRGPD, tagEthique, tagApprofondir).stream().filter(t -> t != null).toList());
            ressourceRepo.save(r3);

            // --- 4. Quiz de positionnement — Interopérabilité HL7/FHIR ---
            RessourcePedagogique r4 = new RessourcePedagogique();
            r4.setTitre("Quiz de positionnement — Interopérabilité et standards HL7/FHIR");
            r4.setDescription(
                    "Quiz diagnostique permettant à l'apprenant d'évaluer son niveau sur les standards d'interopérabilité en santé (HL7, FHIR, IHE) avant de suivre le module approfondi.");
            r4.setTypeSupport(RessourcePedagogique.TypeSupport.QUIZ);
            r4.setDureeMinutes(15);
            r4.setDifficulte(RessourcePedagogique.Difficulte.INTERMEDIAIRE);
            r4.setNiveau(niveauMaster);
            r4.setThematique(themInterop);
            r4.setUsagePedagogique(RessourcePedagogique.UsagePedagogique.QUIZ_POSITIONNEMENT);
            r4.setStatut(RessourcePedagogique.StatutRessource.VALIDEE);
            r4.setVisible(true);
            r4.setUrlAcces("https://moodle.univ-jfc.fr/mod/quiz/view.php?id=4055");
            r4.setObjectifsPedagogiques("Auto-évaluer ses connaissances sur l'interopérabilité avant le cours");
            r4.setCompetencesVisees("Identifier ses lacunes sur les standards HL7/FHIR/IHE");
            r4.setUsageMoodle("Interop-Master-Intro, SIS-Ingenieur-Quiz");
            r4.setDroits(RessourcePedagogique.Droits.FEEINS_INTERNE);
            r4.setAuteurPartenaire("ISIS / FEEINS");
            r4.setNomenclature("FEEINS-QUIZ-MASTER-0001");
            r4.setDerniereVerification(LocalDate.of(2025, 9, 20));
            r4.setContributeur(contrib);
            r4.setTags(List.of(tagInterop, tagDecouvrir).stream().filter(t -> t != null).toList());
            ressourceRepo.save(r4);

            // --- 5. IA et aide à la décision médicale — Vidéo ---
            RessourcePedagogique r5 = new RessourcePedagogique();
            r5.setTitre("IA et aide à la décision médicale");
            r5.setDescription(
                    "Présentation des applications de l'intelligence artificielle en médecine : diagnostic assisté par IA, radiologie, oncologie, gestion des risques. Cas d'usage réels et limites éthiques.");
            r5.setTypeSupport(RessourcePedagogique.TypeSupport.VIDEO);
            r5.setDureeMinutes(20);
            r5.setDifficulte(RessourcePedagogique.Difficulte.AVANCE);
            r5.setNiveau(niveauMaster);
            r5.setThematique(themIA);
            r5.setUsagePedagogique(RessourcePedagogique.UsagePedagogique.COURS);
            r5.setStatut(RessourcePedagogique.StatutRessource.VALIDEE);
            r5.setVisible(true);
            r5.setUrlAcces("https://moodle.univ-jfc.fr/mod/resource/view.php?id=5002");
            r5.setObjectifsPedagogiques(
                    "Comprendre les enjeux de l'IA en santé, distinguer IA descriptive/prédictive/prescriptive");
            r5.setCompetencesVisees("Évaluer les bénéfices et risques d'un système IA dans un contexte médical");
            r5.setUsageMoodle("IA-Sante-Master-CM2, Ingenieur-IA-TD3");
            r5.setDroits(RessourcePedagogique.Droits.FEEINS_INTERNE);
            r5.setAuteurPartenaire("ISIS / Cathy Pons");
            r5.setNomenclature("FEEINS-VIDEO-MASTER-0002");
            r5.setDerniereVerification(LocalDate.of(2025, 11, 1));
            r5.setContributeur(contrib);
            r5.setTags(List.of(tagIA, tagEthique, tagApprofondir).stream().filter(t -> t != null).toList());
            ressourceRepo.save(r5);

            // --- 6. Glossaire e-santé ---
            RessourcePedagogique r6 = new RessourcePedagogique();
            r6.setTitre("Glossaire des termes de la e-santé");
            r6.setDescription(
                    "Référentiel de 80+ termes clés de la santé numérique : DPI, DSP, DMP, HL7, FHIR, IHE, ANS, HDS, PGSSI-S... Consultable à tout moment pendant le parcours.");
            r6.setTypeSupport(RessourcePedagogique.TypeSupport.HTML);
            r6.setDureeMinutes(5);
            r6.setDifficulte(RessourcePedagogique.Difficulte.DEBUTANT);
            r6.setNiveau(niveauDebutant);
            r6.setThematique(themSIS);
            r6.setUsagePedagogique(RessourcePedagogique.UsagePedagogique.RESSOURCE_COMPLEMENTAIRE);
            r6.setStatut(RessourcePedagogique.StatutRessource.VALIDEE);
            r6.setVisible(true);
            r6.setUrlAcces("https://moodle.univ-jfc.fr/mod/glossary/view.php?id=6001");
            r6.setObjectifsPedagogiques("Disposer d'un référentiel terminologique complet pour le parcours FEEINS");
            r6.setCompetencesVisees("Maîtriser le vocabulaire professionnel de la e-santé");
            r6.setUsageMoodle("Tous parcours FEEINS");
            r6.setDroits(RessourcePedagogique.Droits.LIBRE);
            r6.setAuteurPartenaire("FEEINS collectif");
            r6.setNomenclature("FEEINS-HTML-DEBUTANT-0001");
            r6.setDerniereVerification(LocalDate.of(2025, 9, 1));
            r6.setContributeur(contrib);
            r6.setTags(List.of(tagSIS, tagGlossaire, tagDecouvrir).stream().filter(t -> t != null).toList());
            ressourceRepo.save(r6);

            // --- 7. Cybersécurité des SI de santé — PDF ---
            RessourcePedagogique r7 = new RessourcePedagogique();
            r7.setTitre("Cybersécurité des systèmes d'information de santé");
            r7.setDescription(
                    "Cours sur les menaces spécifiques aux SI de santé : ransomware hospitaliers, vecteurs d'attaque, politique PGSSI-S, certification HDS et obligations de l'ANS. Basé sur les incidents réels (CHU Corbeil, CHSF...).");
            r7.setTypeSupport(RessourcePedagogique.TypeSupport.PDF);
            r7.setDureeMinutes(60);
            r7.setDifficulte(RessourcePedagogique.Difficulte.AVANCE);
            r7.setNiveau(niveauMaster);
            r7.setThematique(themSIS);
            r7.setUsagePedagogique(RessourcePedagogique.UsagePedagogique.COURS);
            r7.setStatut(RessourcePedagogique.StatutRessource.VALIDEE);
            r7.setVisible(true);
            r7.setUrlAcces("https://moodle.univ-jfc.fr/mod/resource/view.php?id=7003");
            r7.setObjectifsPedagogiques("Identifier les vulnérabilités propres aux SI de santé et les contre-mesures");
            r7.setCompetencesVisees("Concevoir une politique de sécurité adaptée à un établissement de santé");
            r7.setUsageMoodle("Cyber-Sante-Master-CM3, Ingenieur-SI-Sante-TD4");
            r7.setDroits(RessourcePedagogique.Droits.FEEINS_INTERNE);
            r7.setAuteurPartenaire("ISIS");
            r7.setNomenclature("FEEINS-PDF-MASTER-0002");
            r7.setDerniereVerification(LocalDate.of(2025, 10, 15));
            r7.setContributeur(contrib);
            r7.setTags(List.of(tagCyber, tagSIS, tagApprofondir).stream().filter(t -> t != null).toList());
            ressourceRepo.save(r7);

            // --- 8. Télémédecine et téléconsultation — Lien ANAP ---
            RessourcePedagogique r8 = new RessourcePedagogique();
            r8.setTitre("Étude ANAP 2023 — Bilan de la télémédecine post-COVID");
            r8.setDescription(
                    "Rapport ANAP analysant le déploiement de la téléconsultation en France depuis 2020 : chiffres, bonnes pratiques, freins et facteurs de succès dans différentes spécialités.");
            r8.setTypeSupport(RessourcePedagogique.TypeSupport.LIEN);
            r8.setDureeMinutes(30);
            r8.setDifficulte(RessourcePedagogique.Difficulte.INTERMEDIAIRE);
            r8.setNiveau(niveauL3);
            r8.setThematique(themTele);
            r8.setUsagePedagogique(RessourcePedagogique.UsagePedagogique.RESSOURCE_COMPLEMENTAIRE);
            r8.setStatut(RessourcePedagogique.StatutRessource.VALIDEE);
            r8.setVisible(true);
            r8.setUrlAcces("https://anap.fr/s/ressource/bilan-telemedecine-2023");
            r8.setObjectifsPedagogiques("Analyser l'impact organisationnel de la télémédecine sur le système de santé");
            r8.setCompetencesVisees("Évaluer les conditions de déploiement d'un service de téléconsultation");
            r8.setUsageMoodle("Tele-Sante-L3-CM1, Parcours-Expertise-Metier");
            r8.setDroits(RessourcePedagogique.Droits.LIBRE);
            r8.setAuteurPartenaire("ANAP");
            r8.setNomenclature("FEEINS-LIEN-L3-0001");
            r8.setDerniereVerification(LocalDate.of(2025, 11, 10));
            r8.setContributeur(contrib);
            r8.setTags(List.of(tagTele, tagParcours).stream().filter(t -> t != null).toList());
            ressourceRepo.save(r8);

            // --- 9. IoT médical et objets connectés — H5P ---
            RessourcePedagogique r9 = new RessourcePedagogique();
            r9.setTitre("IoT médical — Capteurs, dispositifs connectés et enjeux");
            r9.setDescription(
                    "Module H5P interactif sur l'Internet of Things en santé : monitoring à distance, dispositifs implantables, wearables, interopérabilité avec le SI hospitalier et enjeux de sécurité des données captées.");
            r9.setTypeSupport(RessourcePedagogique.TypeSupport.H5P);
            r9.setDureeMinutes(35);
            r9.setDifficulte(RessourcePedagogique.Difficulte.INTERMEDIAIRE);
            r9.setNiveau(niveauL3);
            r9.setThematique(themIoT);
            r9.setUsagePedagogique(RessourcePedagogique.UsagePedagogique.ACTIVITE);
            r9.setStatut(RessourcePedagogique.StatutRessource.VALIDEE);
            r9.setVisible(true);
            r9.setUrlAcces("https://moodle.univ-jfc.fr/mod/hvp/view.php?id=9088");
            r9.setObjectifsPedagogiques(
                    "Distinguer les types de dispositifs IoT médicaux et leurs contraintes réglementaires");
            r9.setCompetencesVisees("Évaluer l'intégration d'un objet connecté dans une architecture SI de santé");
            r9.setUsageMoodle("IoT-Sante-L3-TD2, Master-IoT-CM1");
            r9.setDroits(RessourcePedagogique.Droits.FEEINS_INTERNE);
            r9.setAuteurPartenaire("ISIS");
            r9.setNomenclature("FEEINS-H5P-L3-0002");
            r9.setDerniereVerification(LocalDate.of(2025, 10, 20));
            r9.setContributeur(contrib);
            r9.setTags(List.of(tagIoT, tagH5P, tagApprofondir).stream().filter(t -> t != null).toList());
            ressourceRepo.save(r9);

            // --- 10. Quiz évaluation formative — RGPD Santé (avec corrigé) ---
            RessourcePedagogique r10 = new RessourcePedagogique();
            r10.setTitre("Quiz formatif — RGPD et données de santé (avec corrigé)");
            r10.setDescription(
                    "Quiz d'auto-évaluation sur le RGPD appliqué aux données de santé. ⚠️ Contient les réponses et les feedbacks détaillés — NE PAS utiliser comme évaluation sommative.");
            r10.setTypeSupport(RessourcePedagogique.TypeSupport.QUIZ);
            r10.setDureeMinutes(20);
            r10.setDifficulte(RessourcePedagogique.Difficulte.INTERMEDIAIRE);
            r10.setNiveau(niveauMaster);
            r10.setThematique(themRGPD);
            r10.setUsagePedagogique(RessourcePedagogique.UsagePedagogique.EVALUATION_FORMATIVE);
            r10.setStatut(RessourcePedagogique.StatutRessource.VALIDEE);
            r10.setVisible(true);
            r10.setUrlAcces("https://moodle.univ-jfc.fr/mod/quiz/view.php?id=10099");
            r10.setObjectifsPedagogiques("Vérifier la compréhension des obligations RGPD en santé");
            r10.setCompetencesVisees("Appliquer les principes RGPD à des situations concrètes de santé");
            r10.setUsageMoodle("RGPD-Master-Quiz-Formatif");
            r10.setDroits(RessourcePedagogique.Droits.FEEINS_INTERNE);
            r10.setAuteurPartenaire("ISIS");
            r10.setNomenclature("FEEINS-QUIZ-MASTER-0002");
            r10.setDerniereVerification(LocalDate.of(2025, 11, 5));
            r10.setContributeur(contrib);
            r10.setTags(List.of(tagRGPD, tagEvalForm).stream().filter(t -> t != null).toList());
            ressourceRepo.save(r10);

            // --- 11. Ressource EN ATTENTE (pour démontrer le workflow admin) ---
            RessourcePedagogique r11 = new RessourcePedagogique();
            r11.setTitre("Parcours de soins coordonné — Introduction");
            r11.setDescription(
                    "Présentation du parcours de soins en France : médecin traitant, spécialistes, HAD, SSIAD. Coordination et outils numériques de suivi.");
            r11.setTypeSupport(RessourcePedagogique.TypeSupport.VIDEO);
            r11.setDureeMinutes(18);
            r11.setDifficulte(RessourcePedagogique.Difficulte.DEBUTANT);
            r11.setNiveau(niveauL3);
            r11.setThematique(themParcours);
            r11.setUsagePedagogique(RessourcePedagogique.UsagePedagogique.COURS);
            r11.setStatut(RessourcePedagogique.StatutRessource.EN_ATTENTE);
            r11.setVisible(false);
            r11.setUrlAcces("https://moodle.univ-jfc.fr/mod/resource/view.php?id=11010");
            r11.setObjectifsPedagogiques("Comprendre le fonctionnement du parcours de soins coordonné");
            r11.setCompetencesVisees("Identifier les acteurs et outils numériques du parcours de soins");
            r11.setUsageMoodle("Parcours-Soins-L3-CM1");
            r11.setDroits(RessourcePedagogique.Droits.FEEINS_INTERNE);
            r11.setAuteurPartenaire("ISIS");
            r11.setNomenclature("FEEINS-VIDEO-L3-0002");
            r11.setContributeur(contrib);
            r11.setTags(List.of(tagParcours, tagDecouvrir).stream().filter(t -> t != null).toList());
            ressourceRepo.save(r11);

            System.out.println("✅ 11 ressources fictives FEEINS créées (10 VALIDÉES + 1 EN_ATTENTE)");
        }
    }
}