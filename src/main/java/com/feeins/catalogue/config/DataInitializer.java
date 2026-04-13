package com.feeins.catalogue.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.feeins.catalogue.repository.FiltreConfigRepository;
import com.feeins.catalogue.entity.FiltreConfig;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private FiltreConfigRepository filtreRepo;

    @Override
    public void run(String... args) {
        // Initialiser les filtres de type de contenu si absents
        if (!filtreRepo.existsByCategorie("TYPE_SUPPORT")) {
            filtreRepo.save(new FiltreConfig(null, "TYPE_SUPPORT", "VIDEO",  "🎥 Vidéo",         true, 1));
            filtreRepo.save(new FiltreConfig(null, "TYPE_SUPPORT", "H5P",   "🎮 H5P interactif", true, 2));
            filtreRepo.save(new FiltreConfig(null, "TYPE_SUPPORT", "PDF",   "📄 PDF",             true, 3));
            filtreRepo.save(new FiltreConfig(null, "TYPE_SUPPORT", "QUIZ",  "❓ Quiz",            true, 4));
            filtreRepo.save(new FiltreConfig(null, "TYPE_SUPPORT", "HTML",  "🌐 HTML",            true, 5));
            filtreRepo.save(new FiltreConfig(null, "TYPE_SUPPORT", "LIEN",  "🔗 Lien externe",   true, 6));
            filtreRepo.save(new FiltreConfig(null, "TYPE_SUPPORT", "AUTRE", "📦 Autre",           false, 7));
            System.out.println("✅ Filtres TYPE_SUPPORT initialisés");
        }

        // Initialiser les filtres de difficulté si absents
        if (!filtreRepo.existsByCategorie("DIFFICULTE")) {
            filtreRepo.save(new FiltreConfig(null, "DIFFICULTE", "DEBUTANT",      "🟢 Débutant",      true, 1));
            filtreRepo.save(new FiltreConfig(null, "DIFFICULTE", "INTERMEDIAIRE", "🟡 Intermédiaire", true, 2));
            filtreRepo.save(new FiltreConfig(null, "DIFFICULTE", "AVANCE",        "🔴 Avancé",        true, 3));
            System.out.println("✅ Filtres DIFFICULTE initialisés");
        }

        System.out.println("✅ Backend connecté à Neon PostgreSQL — données chargées depuis la base.");
    }
}