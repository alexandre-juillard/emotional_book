# Schéma de la base de données EmotionalBook (MySQL) – v2.1

Ce document normalise et étend le schéma initial pour couvrir le cahier des charges: suivi des émotions (roue de Plutchik/Willcox), déclencheurs, notes, stratégies, journal, notifications/rappels, OAuth2 (Google/Microsoft), export des données, sécurité/confidentialité.

## Principes de modélisation
- Une « entrée émotionnelle » regroupe l’émotion principale, l’intensité, la date, et relie notes, déclencheurs et stratégies.
- Catalogues normalisés pour émotions, déclencheurs et stratégies, avec possibilité d’items personnalisés par utilisateur.
- Contraintes d’intégrité (FK ON DELETE CASCADE pour les enfants d’une entrée), index pour les requêtes analytiques.
- Horodatage systématique (created_at/updated_at), fuseau (timezone) et locale au niveau utilisateur.

## Vue d’ensemble (ER simplifié)
- users 1—n emotion_entries
- emotion_entries 1—n notes | 1—n entry_triggers | 1—n entry_strategies | 0..n entry_emotions (émotions additionnelles)
- emotion_entries n—1 emotion_taxonomy (émotion principale)
- entry_triggers n—1 trigger_catalog | entry_strategies n—1 strategy_catalog
- users 1—n journals | 1—n reminder_schedules | 1—n exports | 1—n notifications_sent | 1—n oauth_accounts

---

## Tables cœur

### Utilisateurs (`users`)
- `id` (PK, BIGINT, AI)
- `email` (VARCHAR(255), NOT NULL, UNIQUE)
- `password_hash` (VARCHAR(255), NULL) — NULL si compte uniquement OAuth2
- `first_name` (VARCHAR(100), NULL)
- `last_name` (VARCHAR(100), NULL)
- `locale` (VARCHAR(10), DEFAULT 'fr')
- `timezone` (VARCHAR(50), DEFAULT 'Europe/Paris')
- `is_anonymous` (TINYINT(1), DEFAULT 0) — indicateur pour anonymiser les exports/analyses ou basculer en mode étude
- `created_at` (DATETIME, DEFAULT CURRENT_TIMESTAMP)
- `updated_at` (DATETIME, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)
- `last_login_at` (DATETIME, NULL)
- Index: UNIQUE(email)

### Comptes OAuth2 (`oauth_accounts`)
- `id` (PK, BIGINT, AI)
- `user_id` (FK -> users.id, NOT NULL)
- `provider` (ENUM('google','microsoft'), NOT NULL)
- `provider_user_id` (VARCHAR(191), NOT NULL)
- `created_at` (DATETIME, DEFAULT CURRENT_TIMESTAMP)
- Contraintes: UNIQUE(`provider`,`provider_user_id`), INDEX(`user_id`)

### Entrées émotionnelles (`emotion_entries`)
- `id` (PK, BIGINT, AI)
- `user_id` (FK -> users.id, NOT NULL)
- `primary_emotion_id` (FK -> emotion_taxonomy.id, NOT NULL)
- `intensity` (TINYINT UNSIGNED, CHECK 1..10, NOT NULL)
- `entry_at` (DATETIME, NOT NULL)
- `created_at` (DATETIME, DEFAULT CURRENT_TIMESTAMP)
- `updated_at` (DATETIME, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)
- Index: (`user_id`,`entry_at` DESC), INDEX(`primary_emotion_id`)

### Notes d’entrée (`notes`)
- `id` (PK, BIGINT, AI)
- `entry_id` (FK -> emotion_entries.id, NOT NULL)
- `content` (TEXT, NOT NULL)
- `created_at` (DATETIME, DEFAULT CURRENT_TIMESTAMP)
- `updated_at` (DATETIME, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)
- Contraintes: FK ON DELETE CASCADE, INDEX(`entry_id`)

---

## Catalogues (référentiels)

### Taxonomie des émotions (`emotion_taxonomy`)
- `id` (PK, INT, AI)
- `name` (VARCHAR(80), NOT NULL)
- `level` (ENUM('primaire','secondaire','tertiaire'), NOT NULL)
- `parent_id` (FK -> emotion_taxonomy.id, NULL) — hiérarchie (ex: tertiaire -> secondaire -> primaire)
- `color_hex` (CHAR(7), NULL) — #RRGGBB (optionnel)
- `is_active` (TINYINT(1), DEFAULT 1)
- Contraintes: UNIQUE(`name`,`level`,`parent_id`), INDEX(`parent_id`)

### Déclencheurs — catalogue (`trigger_catalog`)
- `id` (PK, BIGINT, AI)
- `label` (VARCHAR(120), NOT NULL)
- `user_id` (FK -> users.id, NULL) — NULL = item système partagé, sinon spécifique utilisateur
- `created_at` (DATETIME, DEFAULT CURRENT_TIMESTAMP)
- Contraintes: UNIQUE(`user_id`,`label`), INDEX(`user_id`)

### Stratégies — catalogue (`strategy_catalog`)
- `id` (PK, BIGINT, AI)
- `label` (VARCHAR(120), NOT NULL)
- `category` (VARCHAR(50), NULL) — ex: respiration, cognition, activité
- `user_id` (FK -> users.id, NULL)
- `created_at` (DATETIME, DEFAULT CURRENT_TIMESTAMP)
- Contraintes: UNIQUE(`user_id`,`label`), INDEX(`user_id`)

---

## Relations N—N des entrées

### Émotions additionnelles (`entry_emotions`)
- `entry_id` (FK -> emotion_entries.id, NOT NULL)
- `emotion_id` (FK -> emotion_taxonomy.id, NOT NULL)
- PK composite: (`entry_id`,`emotion_id`)
- Contraintes: FK ON DELETE CASCADE, INDEX(`emotion_id`)

### Déclencheurs d’une entrée (`entry_triggers`)
- `id` (PK, BIGINT, AI)
- `entry_id` (FK -> emotion_entries.id, NOT NULL)
- `trigger_id` (FK -> trigger_catalog.id, NULL) — NULL si uniquement description libre
- `custom_description` (VARCHAR(255), NULL)
- `created_at` (DATETIME, DEFAULT CURRENT_TIMESTAMP)
- Index: (`entry_id`), INDEX(`trigger_id`)

### Stratégies d’une entrée (`entry_strategies`)
- `id` (PK, BIGINT, AI)
- `entry_id` (FK -> emotion_entries.id, NOT NULL)
- `strategy_id` (FK -> strategy_catalog.id, NULL)
- `notes` (TEXT, NULL)
- `effectiveness` (TINYINT UNSIGNED, NULL) — échelle 1..5 (optionnel)
- `created_at` (DATETIME, DEFAULT CURRENT_TIMESTAMP)
- Index: (`entry_id`), INDEX(`strategy_id`)

---

## Journal de bord (`journals`)
- `id` (PK, BIGINT, AI)
- `user_id` (FK -> users.id, NOT NULL)
- `entry_date` (DATETIME, NOT NULL)
- `content` (TEXT, NOT NULL)
- `created_at` (DATETIME, DEFAULT CURRENT_TIMESTAMP)
- `updated_at` (DATETIME, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)
- Index: (`user_id`,`entry_date` DESC)

---

## Rappels, notifications et exports

### Programmation des rappels (`reminder_schedules`)
- `id` (PK, BIGINT, AI)
- `user_id` (FK -> users.id, NOT NULL)
- `label` (VARCHAR(80), NULL)
- `frequency` (ENUM('once','daily','weekly','custom'), NOT NULL)
- `time_of_day` (TIME, NULL) — utile pour daily/weekly
- `days_of_week` (SET('mon','tue','wed','thu','fri','sat','sun'), NULL) — utile pour weekly/custom
- `timezone` (VARCHAR(50), NOT NULL DEFAULT 'Europe/Paris')
- `is_active` (TINYINT(1), DEFAULT 1)
- `last_run_at` (DATETIME, NULL)
- `next_run_at` (DATETIME, NULL)
- `created_at` (DATETIME, DEFAULT CURRENT_TIMESTAMP)
- `updated_at` (DATETIME, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)
- Index: (`user_id`), INDEX(`next_run_at`)

### Journal des notifications envoyées (`notifications_sent`)
- `id` (PK, BIGINT, AI)
- `user_id` (FK -> users.id, NOT NULL)
- `schedule_id` (FK -> reminder_schedules.id, NULL)
- `type` (ENUM('reminder','export'), NOT NULL)
- `channel` (ENUM('push','email'), NOT NULL)
- `content` (TEXT, NULL)
- `status` (ENUM('queued','sent','failed'), NOT NULL DEFAULT 'queued')
- `sent_at` (DATETIME, NULL)
- Index: (`user_id`,`sent_at` DESC), INDEX(`type`)

### Exports de données (`exports`)
- `id` (PK, BIGINT, AI)
- `user_id` (FK -> users.id, NOT NULL)
- `format` (ENUM('CSV','PDF'), NOT NULL)
- `requested_at` (DATETIME, DEFAULT CURRENT_TIMESTAMP)
- `status` (ENUM('pending','processing','completed','failed'), NOT NULL DEFAULT 'pending')
- `file_url` (VARCHAR(500), NULL)
- `emailed_to` (VARCHAR(255), NULL)
- Index: (`user_id`,`requested_at` DESC)

---

## Contraintes & règles globales
- Moteur: InnoDB pour toutes les tables; FK activées.
- Suppression en cascade:
  - Suppression d’un `user` SUPPRIME en cascade: `emotion_entries`, `journals`, `reminder_schedules`, `notifications_sent`, `exports`, `oauth_accounts`, `trigger_catalog`, `strategy_catalog` (puis, par cascade, `notes`, `entry_triggers`, `entry_strategies`, `entry_emotions`).
  - Suppression d’une `emotion_entry` SUPPRIME en cascade: `notes`, `entry_triggers`, `entry_strategies`, `entry_emotions`.
  - Suppression d’un item des catalogues utilisateur (`trigger_catalog`, `strategy_catalog`) met à NULL les FKs dans `entry_triggers`/`entry_strategies` (ON DELETE SET NULL) pour préserver l’historique de l’entrée.
- Validation des bornes: `intensity` (1..10), `effectiveness` (1..5).
- Unicité logique: `oauth_accounts(provider, provider_user_id)`, `trigger_catalog(user_id,label)`, `strategy_catalog(user_id,label)`, `emotion_taxonomy(name,level,parent_id)`.
- Index analytiques: principaux sur (`user_id`,`entry_at`) et (`user_id`,`entry_date`) pour filtres temporels.

## Évolutions possibles
- Tags libres pour les entrées (`tags`, `entry_tags`).
- Localisation optionnelle (lat, lon) sur `emotion_entries`.
- Partage contrôlé (champ `visibility` ENUM sur `emotion_entries`/`journals`).
- Archivage/anonymisation différée (champ `deleted_at` sur `users` et soft-delete pour conformité RGPD si suppression définitive non souhaitée).

---

## Notes d’implémentation MySQL
- Jeu de caractères recommandé: utf8mb4, collation utf8mb4_0900_ai_ci.
- Types: BIGINT pour PK utilisateurs/entrées, INT pour taxonomie, TINYINT pour flags/échelles.
- Penser à des vues matérialisées ou tables d’agrégats si les analyses deviennent lourdes.

Ce schéma reste compatible avec la version précédente tout en intégrant les ajustements demandés (prénom/nom, suppression du titre, précisions sur les cascades).
