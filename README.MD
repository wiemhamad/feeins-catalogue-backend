# 📚 FEEINS Catalogue - Backend Spring Boot

Catalogue intelligent de grains pédagogiques pour le projet FEEINS.

---

## 🚀 Lancer le projet

```bash
# Compiler et lancer
./mvnw spring-boot:run

# Ou avec Maven installé
mvn spring-boot:run
```

L'API démarre sur : **http://localhost:8080**  
Console H2 (base de données) : **http://localhost:8080/h2-console**

---

## 🔐 Authentification

### Utilisateurs créés au démarrage

| Email | Mot de passe | Rôle |
|-------|-------------|------|
| admin@feeins.fr | admin123 | ADMINISTRATEUR_PEDAGOGIQUE |
| marion@feeins.fr | prof123 | ENSEIGNANT |
| etudiant@feeins.fr | etudiant123 | ETUDIANT |

### Code d'accès enseignant
Pour s'inscrire en tant qu'enseignant ou admin, le champ `codeAccesEnseignant` doit valoir :
```
FEEINS2025
```
(configurable dans `application.properties` → `app.teacher.access-code`)

---

## 🌐 Endpoints API

### Auth (public)

| Méthode | URL | Description |
|---------|-----|-------------|
| POST | `/api/auth/login` | Se connecter |
| POST | `/api/auth/register` | S'inscrire |

**Exemple login :**
```json
POST /api/auth/login
{
  "email": "marion@feeins.fr",
  "motDePasse": "prof123"
}
```

**Exemple inscription enseignant :**
```json
POST /api/auth/register
{
  "nom": "Jean Dupont",
  "email": "jean@univ.fr",
  "motDePasse": "motdepasse123",
  "typeUtilisateur": "ENSEIGNANT",
  "codeAccesEnseignant": "FEEINS2025",
  "role": "Maître de conférences",
  "specialite": "Santé numérique"
}
```

---

### Ressources pédagogiques (grains)

| Méthode | URL | Rôle requis | Description |
|---------|-----|-------------|-------------|
| GET | `/api/ressources` | Public | Lister les ressources validées |
| GET | `/api/ressources/{id}` | Public | Consulter une ressource |
| POST | `/api/ressources/rechercher` | Public | Recherche avancée |
| GET | `/api/ressources/toutes` | ADMIN | Toutes les ressources |
| POST | `/api/ressources/creer` | ENSEIGNANT/ADMIN | Créer une ressource |
| PUT | `/api/ressources/{id}/modifier` | ENSEIGNANT/ADMIN | Modifier |
| POST | `/api/ressources/{id}/valider` | ADMIN | Valider |
| POST | `/api/ressources/{id}/refuser` | ADMIN | Refuser |
| DELETE | `/api/ressources/{id}/supprimer` | ADMIN | Supprimer |
| POST | `/api/ressources/{id}/tags/{tagId}` | ENSEIGNANT/ADMIN | Ajouter un tag |
| DELETE | `/api/ressources/{id}/tags/{tagId}` | ENSEIGNANT/ADMIN | Retirer un tag |

**Exemple créer une ressource :**
```json
POST /api/ressources/creer
Authorization: Bearer <token>
{
  "titre": "Introduction au RGPD en santé",
  "description": "Capsule vidéo sur les bases du RGPD appliqué à la santé numérique",
  "dureeMinutes": 12,
  "typeSupport": "VIDEO",
  "urlAcces": "https://moodle.univ-jfc.fr/mod/resource/view.php?id=1234",
  "difficulte": "DEBUTANT",
  "objectifsPedagogiques": "Comprendre les obligations RGPD dans le domaine de la santé",
  "competencesVisees": "Appliquer les principes RGPD à des cas concrets",
  "niveauId": 1,
  "thematiqueId": 3,
  "tagIds": [1, 4],
  "templateId": 1
}
```

**Exemple recherche avancée :**
```json
POST /api/ressources/rechercher
{
  "keyword": "RGPD",
  "niveauId": 1,
  "typeSupport": "VIDEO",
  "difficulte": "DEBUTANT",
  "dureeMax": 20
}
```

---

### Référentiels (public)

| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/api/niveaux` | Lister les niveaux |
| GET | `/api/thematiques` | Lister les thématiques |
| GET | `/api/tags` | Lister les tags |

---

### Templates (Enseignant/Admin)

| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/api/templates` | Lister tous les templates |
| GET | `/api/templates/modifiables` | Templates modifiables |
| GET | `/api/templates/cle-en-main` | Templates non modifiables |
| POST | `/api/templates` | Créer un template (ADMIN) |
| PUT | `/api/templates/{id}` | Modifier (ADMIN) |
| DELETE | `/api/templates/{id}` | Supprimer (ADMIN) |

---

## 📦 Types de support disponibles
`VIDEO`, `H5P`, `PDF`, `QUIZ`, `HTML`, `LIEN`, `AUTRE`

## 📊 Niveaux de difficulté
`DEBUTANT`, `INTERMEDIAIRE`, `AVANCE`

## 📋 Statuts de ressource
`EN_ATTENTE` → `VALIDEE` ou `REFUSEE` → `ARCHIVEE`

---

## 🏷️ Nomenclature automatique

Chaque ressource reçoit un code unique à la création :
```
FEEINS-{TYPE}-{NIVEAU}-{SEQUENCE}
Exemple : FEEINS-VIDEO-L3-0001
```

---

## 🔧 Configuration production (PostgreSQL)

Dans `application.properties`, remplacer la config H2 par :
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/feeins_catalogue
spring.datasource.username=postgres
spring.datasource.password=votre_mot_de_passe
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=false
```

---

## 🗂️ Structure du projet

```
src/main/java/com/feeins/catalogue/
├── CatalogueApplication.java
├── entity/          # Entités JPA (Utilisateur, RessourcePedagogique...)
├── repository/      # Interfaces Spring Data JPA
├── service/         # Logique métier
├── controller/      # Endpoints REST
├── dto/             # Objets de transfert (Request/Response)
├── security/        # JWT, UserDetailsService, Filter
└── config/          # SecurityConfig, DataInitializer, ExceptionHandler
```