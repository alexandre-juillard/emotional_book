-- V2 seeds taxonomie des émotions (idempotent)
-- Utilise des variables pour relier les niveaux hiérarchiques sans dépendre d’IDs fixes

-- Primaires
SET @JOY := (SELECT id FROM emotion_taxonomy WHERE name='Joie' AND level='primaire' AND parent_id IS NULL);
INSERT INTO emotion_taxonomy(name,level,parent_id,color_hex,is_active)
SELECT 'Joie','primaire',NULL,'#FFD166',1 WHERE @JOY IS NULL;
SET @JOY := COALESCE(@JOY, LAST_INSERT_ID());

SET @TRUST := (SELECT id FROM emotion_taxonomy WHERE name='Confiance' AND level='primaire' AND parent_id IS NULL);
INSERT INTO emotion_taxonomy(name,level,parent_id,color_hex,is_active)
SELECT 'Confiance','primaire',NULL,'#06D6A0',1 WHERE @TRUST IS NULL;
SET @TRUST := COALESCE(@TRUST, LAST_INSERT_ID());

SET @FEAR := (SELECT id FROM emotion_taxonomy WHERE name='Peur' AND level='primaire' AND parent_id IS NULL);
INSERT INTO emotion_taxonomy(name,level,parent_id,color_hex,is_active)
SELECT 'Peur','primaire',NULL,'#118AB2',1 WHERE @FEAR IS NULL;
SET @FEAR := COALESCE(@FEAR, LAST_INSERT_ID());

SET @SURPRISE := (SELECT id FROM emotion_taxonomy WHERE name='Surprise' AND level='primaire' AND parent_id IS NULL);
INSERT INTO emotion_taxonomy(name,level,parent_id,color_hex,is_active)
SELECT 'Surprise','primaire',NULL,'#8ECAE6',1 WHERE @SURPRISE IS NULL;
SET @SURPRISE := COALESCE(@SURPRISE, LAST_INSERT_ID());

SET @SAD := (SELECT id FROM emotion_taxonomy WHERE name='Tristesse' AND level='primaire' AND parent_id IS NULL);
INSERT INTO emotion_taxonomy(name,level,parent_id,color_hex,is_active)
SELECT 'Tristesse','primaire',NULL,'#26547C',1 WHERE @SAD IS NULL;
SET @SAD := COALESCE(@SAD, LAST_INSERT_ID());

SET @DISGUST := (SELECT id FROM emotion_taxonomy WHERE name='Dégoût' AND level='primaire' AND parent_id IS NULL);
INSERT INTO emotion_taxonomy(name,level,parent_id,color_hex,is_active)
SELECT 'Dégoût','primaire',NULL,'#6D597A',1 WHERE @DISGUST IS NULL;
SET @DISGUST := COALESCE(@DISGUST, LAST_INSERT_ID());

SET @ANGER := (SELECT id FROM emotion_taxonomy WHERE name='Colère' AND level='primaire' AND parent_id IS NULL);
INSERT INTO emotion_taxonomy(name,level,parent_id,color_hex,is_active)
SELECT 'Colère','primaire',NULL,'#EF476F',1 WHERE @ANGER IS NULL;
SET @ANGER := COALESCE(@ANGER, LAST_INSERT_ID());

SET @ANTIC := (SELECT id FROM emotion_taxonomy WHERE name='Anticipation' AND level='primaire' AND parent_id IS NULL);
INSERT INTO emotion_taxonomy(name,level,parent_id,color_hex,is_active)
SELECT 'Anticipation','primaire',NULL,'#F77F00',1 WHERE @ANTIC IS NULL;
SET @ANTIC := COALESCE(@ANTIC, LAST_INSERT_ID());

-- Secondaires (échantillon)
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Contentement','secondaire',@JOY,1 WHERE NOT EXISTS (
  SELECT 1 FROM emotion_taxonomy WHERE name='Contentement' AND level='secondaire' AND parent_id=@JOY
);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Sérénité','secondaire',@JOY,1 WHERE NOT EXISTS (
  SELECT 1 FROM emotion_taxonomy WHERE name='Sérénité' AND level='secondaire' AND parent_id=@JOY
);

INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Mélancolie','secondaire',@SAD,1 WHERE NOT EXISTS (
  SELECT 1 FROM emotion_taxonomy WHERE name='Mélancolie' AND level='secondaire' AND parent_id=@SAD
);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Chagrin','secondaire',@SAD,1 WHERE NOT EXISTS (
  SELECT 1 FROM emotion_taxonomy WHERE name='Chagrin' AND level='secondaire' AND parent_id=@SAD
);

INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Frustration','secondaire',@ANGER,1 WHERE NOT EXISTS (
  SELECT 1 FROM emotion_taxonomy WHERE name='Frustration' AND level='secondaire' AND parent_id=@ANGER
);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Irritation','secondaire',@ANGER,1 WHERE NOT EXISTS (
  SELECT 1 FROM emotion_taxonomy WHERE name='Irritation' AND level='secondaire' AND parent_id=@ANGER
);

-- Tertiaires (échantillon)
SET @JOY_CONT := (SELECT id FROM emotion_taxonomy WHERE name='Contentement' AND level='secondaire' AND parent_id=@JOY);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Satisfaction','tertiaire',@JOY_CONT,1 WHERE NOT EXISTS (
  SELECT 1 FROM emotion_taxonomy WHERE name='Satisfaction' AND level='tertiaire' AND parent_id=@JOY_CONT
);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Gratitude','tertiaire',@JOY_CONT,1 WHERE NOT EXISTS (
  SELECT 1 FROM emotion_taxonomy WHERE name='Gratitude' AND level='tertiaire' AND parent_id=@JOY_CONT
);

SET @ANG_FRU := (SELECT id FROM emotion_taxonomy WHERE name='Frustration' AND level='secondaire' AND parent_id=@ANGER);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Agacement','tertiaire',@ANG_FRU,1 WHERE NOT EXISTS (
  SELECT 1 FROM emotion_taxonomy WHERE name='Agacement' AND level='tertiaire' AND parent_id=@ANG_FRU
);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Dépit','tertiaire',@ANG_FRU,1 WHERE NOT EXISTS (
  SELECT 1 FROM emotion_taxonomy WHERE name='Dépit' AND level='tertiaire' AND parent_id=@ANG_FRU
);

