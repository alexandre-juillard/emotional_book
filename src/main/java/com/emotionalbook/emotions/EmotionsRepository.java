package com.emotionalbook.emotions;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Accès aux données pour la taxonomie des émotions.
 */
@Repository
public class EmotionsRepository {

    private final JdbcTemplate jdbc;

    public EmotionsRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<EmotionDto> MAPPER = (rs, rowNum) ->
            new EmotionDto(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("level")
            );

    /**
     * Liste les émotions avec filtre optionnel sur le niveau (primaire|secondaire|tertiaire).
     */
    public List<EmotionDto> lister(String niveau) {
        String sql = "SELECT id,name,level FROM emotion_taxonomy " +
                "WHERE (? IS NULL OR level = ?) ORDER BY level,name";
        return jdbc.query(sql, MAPPER, niveau, niveau);
    }
}
