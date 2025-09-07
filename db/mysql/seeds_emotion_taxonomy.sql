-- EmotionalBook Seeds: emotion_taxonomy (utf8mb4)
-- Idempotent inserts using variables and NOT EXISTS checks

SET NAMES utf8mb4;
USE emotionalbook;
START TRANSACTION;

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

-- Secondaires pour chaque primaire
-- Joie
SET @JOY_CONT := (SELECT id FROM emotion_taxonomy WHERE name='Contentement' AND level='secondaire' AND parent_id=@JOY);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Contentement','secondaire',@JOY,1 WHERE @JOY_CONT IS NULL;
SET @JOY_CONT := COALESCE(@JOY_CONT, LAST_INSERT_ID());

SET @JOY_SER := (SELECT id FROM emotion_taxonomy WHERE name='Sérénité' AND level='secondaire' AND parent_id=@JOY);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Sérénité','secondaire',@JOY,1 WHERE @JOY_SER IS NULL;
SET @JOY_SER := COALESCE(@JOY_SER, LAST_INSERT_ID());

-- Tristesse
SET @SAD_MEL := (SELECT id FROM emotion_taxonomy WHERE name='Mélancolie' AND level='secondaire' AND parent_id=@SAD);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Mélancolie','secondaire',@SAD,1 WHERE @SAD_MEL IS NULL;
SET @SAD_MEL := COALESCE(@SAD_MEL, LAST_INSERT_ID());

SET @SAD_CHA := (SELECT id FROM emotion_taxonomy WHERE name='Chagrin' AND level='secondaire' AND parent_id=@SAD);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Chagrin','secondaire',@SAD,1 WHERE @SAD_CHA IS NULL;
SET @SAD_CHA := COALESCE(@SAD_CHA, LAST_INSERT_ID());

-- Colère
SET @ANG_FRU := (SELECT id FROM emotion_taxonomy WHERE name='Frustration' AND level='secondaire' AND parent_id=@ANGER);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Frustration','secondaire',@ANGER,1 WHERE @ANG_FRU IS NULL;
SET @ANG_FRU := COALESCE(@ANG_FRU, LAST_INSERT_ID());

SET @ANG_IRR := (SELECT id FROM emotion_taxonomy WHERE name='Irritation' AND level='secondaire' AND parent_id=@ANGER);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Irritation','secondaire',@ANGER,1 WHERE @ANG_IRR IS NULL;
SET @ANG_IRR := COALESCE(@ANG_IRR, LAST_INSERT_ID());

-- Peur
SET @FEAR_ANX := (SELECT id FROM emotion_taxonomy WHERE name='Anxiété' AND level='secondaire' AND parent_id=@FEAR);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Anxiété','secondaire',@FEAR,1 WHERE @FEAR_ANX IS NULL;
SET @FEAR_ANX := COALESCE(@FEAR_ANX, LAST_INSERT_ID());

SET @FEAR_WOR := (SELECT id FROM emotion_taxonomy WHERE name='Inquiétude' AND level='secondaire' AND parent_id=@FEAR);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Inquiétude','secondaire',@FEAR,1 WHERE @FEAR_WOR IS NULL;
SET @FEAR_WOR := COALESCE(@FEAR_WOR, LAST_INSERT_ID());

-- Surprise
SET @SUR_AST := (SELECT id FROM emotion_taxonomy WHERE name='Étonnement' AND level='secondaire' AND parent_id=@SURPRISE);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Étonnement','secondaire',@SURPRISE,1 WHERE @SUR_AST IS NULL;
SET @SUR_AST := COALESCE(@SUR_AST, LAST_INSERT_ID());

SET @SUR_STU := (SELECT id FROM emotion_taxonomy WHERE name='Stupeur' AND level='secondaire' AND parent_id=@SURPRISE);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Stupeur','secondaire',@SURPRISE,1 WHERE @SUR_STU IS NULL;
SET @SUR_STU := COALESCE(@SUR_STU, LAST_INSERT_ID());

-- Dégoût
SET @DIS_AV := (SELECT id FROM emotion_taxonomy WHERE name='Aversion' AND level='secondaire' AND parent_id=@DISGUST);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Aversion','secondaire',@DISGUST,1 WHERE @DIS_AV IS NULL;
SET @DIS_AV := COALESCE(@DIS_AV, LAST_INSERT_ID());

SET @DIS_REP := (SELECT id FROM emotion_taxonomy WHERE name='Répulsion' AND level='secondaire' AND parent_id=@DISGUST);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Répulsion','secondaire',@DISGUST,1 WHERE @DIS_REP IS NULL;
SET @DIS_REP := COALESCE(@DIS_REP, LAST_INSERT_ID());

-- Anticipation
SET @ANT_INT := (SELECT id FROM emotion_taxonomy WHERE name='Intérêt' AND level='secondaire' AND parent_id=@ANTIC);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Intérêt','secondaire',@ANTIC,1 WHERE @ANT_INT IS NULL;
SET @ANT_INT := COALESCE(@ANT_INT, LAST_INSERT_ID());

SET @ANT_VIG := (SELECT id FROM emotion_taxonomy WHERE name='Vigilance' AND level='secondaire' AND parent_id=@ANTIC);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Vigilance','secondaire',@ANTIC,1 WHERE @ANT_VIG IS NULL;
SET @ANT_VIG := COALESCE(@ANT_VIG, LAST_INSERT_ID());

-- Confiance
SET @TRU_AFF := (SELECT id FROM emotion_taxonomy WHERE name='Affection' AND level='secondaire' AND parent_id=@TRUST);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Affection','secondaire',@TRUST,1 WHERE @TRU_AFF IS NULL;
SET @TRU_AFF := COALESCE(@TRU_AFF, LAST_INSERT_ID());

SET @TRU_ADM := (SELECT id FROM emotion_taxonomy WHERE name='Admiration' AND level='secondaire' AND parent_id=@TRUST);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Admiration','secondaire',@TRUST,1 WHERE @TRU_ADM IS NULL;
SET @TRU_ADM := COALESCE(@TRU_ADM, LAST_INSERT_ID());

-- Tertiaires (échantillon)
-- Joie -> Contentement
SET @JOY_CONT_SAT := (SELECT id FROM emotion_taxonomy WHERE name='Satisfaction' AND level='tertiaire' AND parent_id=@JOY_CONT);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Satisfaction','tertiaire',@JOY_CONT,1 WHERE @JOY_CONT_SAT IS NULL;
SET @JOY_CONT_SAT := COALESCE(@JOY_CONT_SAT, LAST_INSERT_ID());

SET @JOY_CONT_GRA := (SELECT id FROM emotion_taxonomy WHERE name='Gratitude' AND level='tertiaire' AND parent_id=@JOY_CONT);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Gratitude','tertiaire',@JOY_CONT,1 WHERE @JOY_CONT_GRA IS NULL;
SET @JOY_CONT_GRA := COALESCE(@JOY_CONT_GRA, LAST_INSERT_ID());

-- Joie -> Sérénité
SET @JOY_SER_CAL := (SELECT id FROM emotion_taxonomy WHERE name='Calme' AND level='tertiaire' AND parent_id=@JOY_SER);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Calme','tertiaire',@JOY_SER,1 WHERE @JOY_SER_CAL IS NULL;
SET @JOY_SER_CAL := COALESCE(@JOY_SER_CAL, LAST_INSERT_ID());

SET @JOY_SER_PAX := (SELECT id FROM emotion_taxonomy WHERE name='Paix intérieure' AND level='tertiaire' AND parent_id=@JOY_SER);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Paix intérieure','tertiaire',@JOY_SER,1 WHERE @JOY_SER_PAX IS NULL;
SET @JOY_SER_PAX := COALESCE(@JOY_SER_PAX, LAST_INSERT_ID());

-- Tristesse
SET @SAD_MEL_NOS := (SELECT id FROM emotion_taxonomy WHERE name='Nostalgie' AND level='tertiaire' AND parent_id=@SAD_MEL);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Nostalgie','tertiaire',@SAD_MEL,1 WHERE @SAD_MEL_NOS IS NULL;
SET @SAD_MEL_NOS := COALESCE(@SAD_MEL_NOS, LAST_INSERT_ID());

SET @SAD_MEL_ABA := (SELECT id FROM emotion_taxonomy WHERE name='Abattement' AND level='tertiaire' AND parent_id=@SAD_MEL);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Abattement','tertiaire',@SAD_MEL,1 WHERE @SAD_MEL_ABA IS NULL;
SET @SAD_MEL_ABA := COALESCE(@SAD_MEL_ABA, LAST_INSERT_ID());

SET @SAD_CHA_DEU := (SELECT id FROM emotion_taxonomy WHERE name='Deuil' AND level='tertiaire' AND parent_id=@SAD_CHA);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Deuil','tertiaire',@SAD_CHA,1 WHERE @SAD_CHA_DEU IS NULL;
SET @SAD_CHA_DEU := COALESCE(@SAD_CHA_DEU, LAST_INSERT_ID());

SET @SAD_CHA_PRO := (SELECT id FROM emotion_taxonomy WHERE name='Tristesse profonde' AND level='tertiaire' AND parent_id=@SAD_CHA);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Tristesse profonde','tertiaire',@SAD_CHA,1 WHERE @SAD_CHA_PRO IS NULL;
SET @SAD_CHA_PRO := COALESCE(@SAD_CHA_PRO, LAST_INSERT_ID());

-- Colère
SET @ANG_FRU_AGA := (SELECT id FROM emotion_taxonomy WHERE name='Agacement' AND level='tertiaire' AND parent_id=@ANG_FRU);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Agacement','tertiaire',@ANG_FRU,1 WHERE @ANG_FRU_AGA IS NULL;
SET @ANG_FRU_AGA := COALESCE(@ANG_FRU_AGA, LAST_INSERT_ID());

SET @ANG_FRU_DEP := (SELECT id FROM emotion_taxonomy WHERE name='Dépit' AND level='tertiaire' AND parent_id=@ANG_FRU);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Dépit','tertiaire',@ANG_FRU,1 WHERE @ANG_FRU_DEP IS NULL;
SET @ANG_FRU_DEP := COALESCE(@ANG_FRU_DEP, LAST_INSERT_ID());

SET @ANG_IRR_ENV := (SELECT id FROM emotion_taxonomy WHERE name='Énervement' AND level='tertiaire' AND parent_id=@ANG_IRR);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Énervement','tertiaire',@ANG_IRR,1 WHERE @ANG_IRR_ENV IS NULL;
SET @ANG_IRR_ENV := COALESCE(@ANG_IRR_ENV, LAST_INSERT_ID());

SET @ANG_IRR_EXA := (SELECT id FROM emotion_taxonomy WHERE name='Exaspération' AND level='tertiaire' AND parent_id=@ANG_IRR);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Exaspération','tertiaire',@ANG_IRR,1 WHERE @ANG_IRR_EXA IS NULL;
SET @ANG_IRR_EXA := COALESCE(@ANG_IRR_EXA, LAST_INSERT_ID());

-- Peur
SET @FEAR_ANX_NER := (SELECT id FROM emotion_taxonomy WHERE name='Nervosité' AND level='tertiaire' AND parent_id=@FEAR_ANX);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Nervosité','tertiaire',@FEAR_ANX,1 WHERE @FEAR_ANX_NER IS NULL;
SET @FEAR_ANX_NER := COALESCE(@FEAR_ANX_NER, LAST_INSERT_ID());

SET @FEAR_ANX_APP := (SELECT id FROM emotion_taxonomy WHERE name='Appréhension' AND level='tertiaire' AND parent_id=@FEAR_ANX);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Appréhension','tertiaire',@FEAR_ANX,1 WHERE @FEAR_ANX_APP IS NULL;
SET @FEAR_ANX_APP := COALESCE(@FEAR_ANX_APP, LAST_INSERT_ID());

SET @FEAR_WOR_SOU := (SELECT id FROM emotion_taxonomy WHERE name='Souci' AND level='tertiaire' AND parent_id=@FEAR_WOR);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Souci','tertiaire',@FEAR_WOR,1 WHERE @FEAR_WOR_SOU IS NULL;
SET @FEAR_WOR_SOU := COALESCE(@FEAR_WOR_SOU, LAST_INSERT_ID());

SET @FEAR_WOR_CRA := (SELECT id FROM emotion_taxonomy WHERE name='Crainte' AND level='tertiaire' AND parent_id=@FEAR_WOR);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Crainte','tertiaire',@FEAR_WOR,1 WHERE @FEAR_WOR_CRA IS NULL;
SET @FEAR_WOR_CRA := COALESCE(@FEAR_WOR_CRA, LAST_INSERT_ID());

-- Surprise
SET @SUR_AST_AGR := (SELECT id FROM emotion_taxonomy WHERE name='Surprise agréable' AND level='tertiaire' AND parent_id=@SUR_AST);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Surprise agréable','tertiaire',@SUR_AST,1 WHERE @SUR_AST_AGR IS NULL;
SET @SUR_AST_AGR := COALESCE(@SUR_AST_AGR, LAST_INSERT_ID());

SET @SUR_AST_NEU := (SELECT id FROM emotion_taxonomy WHERE name='Surprise neutre' AND level='tertiaire' AND parent_id=@SUR_AST);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Surprise neutre','tertiaire',@SUR_AST,1 WHERE @SUR_AST_NEU IS NULL;
SET @SUR_AST_NEU := COALESCE(@SUR_AST_NEU, LAST_INSERT_ID());

SET @SUR_STU_INC := (SELECT id FROM emotion_taxonomy WHERE name='Incrédulité' AND level='tertiaire' AND parent_id=@SUR_STU);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Incrédulité','tertiaire',@SUR_STU,1 WHERE @SUR_STU_INC IS NULL;
SET @SUR_STU_INC := COALESCE(@SUR_STU_INC, LAST_INSERT_ID());

SET @SUR_STU_SID := (SELECT id FROM emotion_taxonomy WHERE name='Sidération' AND level='tertiaire' AND parent_id=@SUR_STU);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Sidération','tertiaire',@SUR_STU,1 WHERE @SUR_STU_SID IS NULL;
SET @SUR_STU_SID := COALESCE(@SUR_STU_SID, LAST_INSERT_ID());

-- Dégoût
SET @DIS_AV_DEP := (SELECT id FROM emotion_taxonomy WHERE name='Déplaisir' AND level='tertiaire' AND parent_id=@DIS_AV);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Déplaisir','tertiaire',@DIS_AV,1 WHERE @DIS_AV_DEP IS NULL;
SET @DIS_AV_DEP := COALESCE(@DIS_AV_DEP, LAST_INSERT_ID());

SET @DIS_AV_DGL := (SELECT id FROM emotion_taxonomy WHERE name='Dégoût léger' AND level='tertiaire' AND parent_id=@DIS_AV);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Dégoût léger','tertiaire',@DIS_AV,1 WHERE @DIS_AV_DGL IS NULL;
SET @DIS_AV_DGL := COALESCE(@DIS_AV_DGL, LAST_INSERT_ID());

SET @DIS_REP_NAU := (SELECT id FROM emotion_taxonomy WHERE name='Nausée' AND level='tertiaire' AND parent_id=@DIS_REP);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Nausée','tertiaire',@DIS_REP,1 WHERE @DIS_REP_NAU IS NULL;
SET @DIS_REP_NAU := COALESCE(@DIS_REP_NAU, LAST_INSERT_ID());

SET @DIS_REP_REJ := (SELECT id FROM emotion_taxonomy WHERE name='Rejet' AND level='tertiaire' AND parent_id=@DIS_REP);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Rejet','tertiaire',@DIS_REP,1 WHERE @DIS_REP_REJ IS NULL;
SET @DIS_REP_REJ := COALESCE(@DIS_REP_REJ, LAST_INSERT_ID());

-- Anticipation
SET @ANT_INT_CUR := (SELECT id FROM emotion_taxonomy WHERE name='Curiosité' AND level='tertiaire' AND parent_id=@ANT_INT);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Curiosité','tertiaire',@ANT_INT,1 WHERE @ANT_INT_CUR IS NULL;
SET @ANT_INT_CUR := COALESCE(@ANT_INT_CUR, LAST_INSERT_ID());

SET @ANT_INT_MOT := (SELECT id FROM emotion_taxonomy WHERE name='Motivation' AND level='tertiaire' AND parent_id=@ANT_INT);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Motivation','tertiaire',@ANT_INT,1 WHERE @ANT_INT_MOT IS NULL;
SET @ANT_INT_MOT := COALESCE(@ANT_INT_MOT, LAST_INSERT_ID());

SET @ANT_VIG_APR := (SELECT id FROM emotion_taxonomy WHERE name='Anticipation prudente' AND level='tertiaire' AND parent_id=@ANT_VIG);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Anticipation prudente','tertiaire',@ANT_VIG,1 WHERE @ANT_VIG_APR IS NULL;
SET @ANT_VIG_APR := COALESCE(@ANT_VIG_APR, LAST_INSERT_ID());

SET @ANT_VIG_PRE := (SELECT id FROM emotion_taxonomy WHERE name='Préparation' AND level='tertiaire' AND parent_id=@ANT_VIG);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Préparation','tertiaire',@ANT_VIG,1 WHERE @ANT_VIG_PRE IS NULL;
SET @ANT_VIG_PRE := COALESCE(@ANT_VIG_PRE, LAST_INSERT_ID());

-- Confiance
SET @TRU_AFF_ATT := (SELECT id FROM emotion_taxonomy WHERE name='Attachement' AND level='tertiaire' AND parent_id=@TRU_AFF);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Attachement','tertiaire',@TRU_AFF,1 WHERE @TRU_AFF_ATT IS NULL;
SET @TRU_AFF_ATT := COALESCE(@TRU_AFF_ATT, LAST_INSERT_ID());

SET @TRU_AFF_CHA := (SELECT id FROM emotion_taxonomy WHERE name='Chaleur' AND level='tertiaire' AND parent_id=@TRU_AFF);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Chaleur','tertiaire',@TRU_AFF,1 WHERE @TRU_AFF_CHA IS NULL;
SET @TRU_AFF_CHA := COALESCE(@TRU_AFF_CHA, LAST_INSERT_ID());

SET @TRU_ADM_RES := (SELECT id FROM emotion_taxonomy WHERE name='Respect' AND level='tertiaire' AND parent_id=@TRU_ADM);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Respect','tertiaire',@TRU_ADM,1 WHERE @TRU_ADM_RES IS NULL;
SET @TRU_ADM_RES := COALESCE(@TRU_ADM_RES, LAST_INSERT_ID());

SET @TRU_ADM_EST := (SELECT id FROM emotion_taxonomy WHERE name='Estime' AND level='tertiaire' AND parent_id=@TRU_ADM);
INSERT INTO emotion_taxonomy(name,level,parent_id,is_active)
SELECT 'Estime','tertiaire',@TRU_ADM,1 WHERE @TRU_ADM_EST IS NULL;
SET @TRU_ADM_EST := COALESCE(@TRU_ADM_EST, LAST_INSERT_ID());

COMMIT;

