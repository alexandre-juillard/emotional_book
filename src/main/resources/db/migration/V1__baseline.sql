-- V1 baseline du schéma EmotionalBook (extrait de schema.sql, sans CREATE DATABASE/USE ni transactions)
-- Charset/Collation gérés au niveau du serveur MySQL

-- Table: users
CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255) NOT NULL,
  password_hash VARCHAR(255) NULL,
  first_name VARCHAR(100) NULL,
  last_name VARCHAR(100) NULL,
  locale VARCHAR(10) DEFAULT 'fr',
  timezone VARCHAR(50) DEFAULT 'Europe/Paris',
  is_anonymous TINYINT(1) DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  last_login_at DATETIME NULL,
  CONSTRAINT uq_users_email UNIQUE (email)
) ENGINE=InnoDB;

-- Table: emotion_taxonomy
CREATE TABLE IF NOT EXISTS emotion_taxonomy (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(80) NOT NULL,
  level ENUM('primaire','secondaire','tertiaire') NOT NULL,
  parent_id INT NULL,
  color_hex CHAR(7) NULL,
  is_active TINYINT(1) DEFAULT 1,
  CONSTRAINT uq_emotion_taxonomy UNIQUE (name, level, parent_id),
  CONSTRAINT fk_emotion_taxonomy_parent FOREIGN KEY (parent_id)
    REFERENCES emotion_taxonomy(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE
) ENGINE=InnoDB;

-- Table: oauth_accounts
CREATE TABLE IF NOT EXISTS oauth_accounts (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  provider ENUM('google','microsoft') NOT NULL,
  provider_user_id VARCHAR(191) NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_oauth_provider_user UNIQUE (provider, provider_user_id),
  CONSTRAINT fk_oauth_user FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;
CREATE INDEX idx_oauth_user_id ON oauth_accounts(user_id);

-- Table: trigger_catalog
CREATE TABLE IF NOT EXISTS trigger_catalog (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  label VARCHAR(120) NOT NULL,
  user_id BIGINT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_trigger_user_label UNIQUE (user_id, label),
  CONSTRAINT fk_trigger_user FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;
CREATE INDEX idx_trigger_user_id ON trigger_catalog(user_id);

-- Table: strategy_catalog
CREATE TABLE IF NOT EXISTS strategy_catalog (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  label VARCHAR(120) NOT NULL,
  category VARCHAR(50) NULL,
  user_id BIGINT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_strategy_user_label UNIQUE (user_id, label),
  CONSTRAINT fk_strategy_user FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;
CREATE INDEX idx_strategy_user_id ON strategy_catalog(user_id);

-- Table: emotion_entries
CREATE TABLE IF NOT EXISTS emotion_entries (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  primary_emotion_id INT NOT NULL,
  intensity TINYINT UNSIGNED NOT NULL,
  entry_at DATETIME NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT chk_emotion_entries_intensity CHECK (intensity BETWEEN 1 AND 10),
  CONSTRAINT fk_entry_user FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT fk_entry_primary_emotion FOREIGN KEY (primary_emotion_id)
    REFERENCES emotion_taxonomy(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB;
CREATE INDEX idx_entry_user_entryat ON emotion_entries(user_id, entry_at DESC);
CREATE INDEX idx_entry_primary_emotion ON emotion_entries(primary_emotion_id);

-- Table: notes
CREATE TABLE IF NOT EXISTS notes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  entry_id BIGINT NOT NULL,
  content TEXT NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_notes_entry FOREIGN KEY (entry_id)
    REFERENCES emotion_entries(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;
CREATE INDEX idx_notes_entry_id ON notes(entry_id);

-- Table: entry_emotions
CREATE TABLE IF NOT EXISTS entry_emotions (
  entry_id BIGINT NOT NULL,
  emotion_id INT NOT NULL,
  PRIMARY KEY (entry_id, emotion_id),
  CONSTRAINT fk_entry_emotions_entry FOREIGN KEY (entry_id)
    REFERENCES emotion_entries(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT fk_entry_emotions_emotion FOREIGN KEY (emotion_id)
    REFERENCES emotion_taxonomy(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB;
CREATE INDEX idx_entry_emotions_emotion ON entry_emotions(emotion_id);

-- Table: entry_triggers
CREATE TABLE IF NOT EXISTS entry_triggers (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  entry_id BIGINT NOT NULL,
  trigger_id BIGINT NULL,
  custom_description VARCHAR(255) NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_entry_triggers_entry FOREIGN KEY (entry_id)
    REFERENCES emotion_entries(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT fk_entry_triggers_trigger FOREIGN KEY (trigger_id)
    REFERENCES trigger_catalog(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE
) ENGINE=InnoDB;
CREATE INDEX idx_entry_triggers_entry_id ON entry_triggers(entry_id);
CREATE INDEX idx_entry_triggers_trigger_id ON entry_triggers(trigger_id);

-- Table: entry_strategies
CREATE TABLE IF NOT EXISTS entry_strategies (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  entry_id BIGINT NOT NULL,
  strategy_id BIGINT NULL,
  notes TEXT NULL,
  effectiveness TINYINT UNSIGNED NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT chk_entry_strategies_effectiveness CHECK (effectiveness IS NULL OR (effectiveness BETWEEN 1 AND 5)),
  CONSTRAINT fk_entry_strategies_entry FOREIGN KEY (entry_id)
    REFERENCES emotion_entries(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT fk_entry_strategies_strategy FOREIGN KEY (strategy_id)
    REFERENCES strategy_catalog(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE
) ENGINE=InnoDB;
CREATE INDEX idx_entry_strategies_entry_id ON entry_strategies(entry_id);
CREATE INDEX idx_entry_strategies_strategy_id ON entry_strategies(strategy_id);

-- Table: journals
CREATE TABLE IF NOT EXISTS journals (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  entry_date DATETIME NOT NULL,
  content TEXT NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_journals_user FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;
CREATE INDEX idx_journals_user_entrydate ON journals(user_id, entry_date DESC);

-- Table: reminder_schedules
CREATE TABLE IF NOT EXISTS reminder_schedules (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  label VARCHAR(80) NULL,
  frequency ENUM('once','daily','weekly','custom') NOT NULL,
  time_of_day TIME NULL,
  days_of_week SET('mon','tue','wed','thu','fri','sat','sun') NULL,
  timezone VARCHAR(50) NOT NULL DEFAULT 'Europe/Paris',
  is_active TINYINT(1) DEFAULT 1,
  last_run_at DATETIME NULL,
  next_run_at DATETIME NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_reminder_user FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;
CREATE INDEX idx_reminder_user_id ON reminder_schedules(user_id);
CREATE INDEX idx_reminder_next_run_at ON reminder_schedules(next_run_at);

-- Table: notifications_sent
CREATE TABLE IF NOT EXISTS notifications_sent (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  schedule_id BIGINT NULL,
  type ENUM('reminder','export') NOT NULL,
  channel ENUM('push','email') NOT NULL,
  content TEXT NULL,
  status ENUM('queued','sent','failed') NOT NULL DEFAULT 'queued',
  sent_at DATETIME NULL,
  CONSTRAINT fk_notifications_user FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT fk_notifications_schedule FOREIGN KEY (schedule_id)
    REFERENCES reminder_schedules(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE
) ENGINE=InnoDB;
CREATE INDEX idx_notifications_user_sentat ON notifications_sent(user_id, sent_at DESC);
CREATE INDEX idx_notifications_type ON notifications_sent(type);

-- Table: exports
CREATE TABLE IF NOT EXISTS exports (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  format ENUM('CSV','PDF') NOT NULL,
  requested_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  status ENUM('pending','processing','completed','failed') NOT NULL DEFAULT 'pending',
  file_url VARCHAR(500) NULL,
  emailed_to VARCHAR(255) NULL,
  CONSTRAINT fk_exports_user FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;
CREATE INDEX idx_exports_user_requested_at ON exports(user_id, requested_at DESC);
