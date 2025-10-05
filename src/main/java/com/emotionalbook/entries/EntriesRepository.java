package com.emotionalbook.entries;

import com.emotionalbook.entries.dto.EntreeEmotionDto;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class EntriesRepository {

    private final JdbcTemplate jdbc;

    public EntriesRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static EntreeEmotionDto mapRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        Long id = rs.getLong("id");
        Long userId = rs.getLong("user_id");
        Integer primaryEmotionId = rs.getInt("primary_emotion_id");
        String primaryEmotionName = rs.getString("primary_emotion_name");
        Integer intensity = rs.getInt("intensity");
        LocalDateTime entryAt = rs.getTimestamp("entry_at").toLocalDateTime();
        String note = rs.getString("note");
        return new EntreeEmotionDto(id, userId, primaryEmotionId, primaryEmotionName, intensity, entryAt, note);
    }

    public EntreeEmotionDto creer(Long utilisateurId, Integer emotionPrincipaleId, Integer intensite, LocalDateTime dateHeure, String note) {
        String sql = "INSERT INTO emotion_entries(user_id, primary_emotion_id, intensity, entry_at) VALUES (?,?,?,?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, utilisateurId);
            ps.setInt(2, emotionPrincipaleId);
            ps.setInt(3, intensite);
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(dateHeure));
            return ps;
        }, kh);
        long id = ((Number) kh.getKey()).longValue();
        if (note != null && !note.isBlank()) {
            jdbc.update("INSERT INTO notes(entry_id, content) VALUES (?,?)", id, note);
        }
        return trouverParId(id).orElseThrow();
    }

    public Optional<EntreeEmotionDto> trouverParId(Long id) {
        String sql = "SELECT e.id, e.user_id, e.primary_emotion_id, et.name AS primary_emotion_name, e.intensity, e.entry_at, " +
                " (SELECT n.content FROM notes n WHERE n.entry_id = e.id ORDER BY n.id ASC LIMIT 1) AS note " +
                "FROM emotion_entries e JOIN emotion_taxonomy et ON et.id = e.primary_emotion_id WHERE e.id = ?";
        try {
            return Optional.ofNullable(jdbc.queryForObject(sql, (rs, rn) -> mapRow(rs), id));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<EntreeEmotionDto> lister(Long utilisateurId, LocalDateTime from, LocalDateTime to) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT e.id, e.user_id, e.primary_emotion_id, et.name AS primary_emotion_name, e.intensity, e.entry_at, ")
          .append(" (SELECT n.content FROM notes n WHERE n.entry_id = e.id ORDER BY n.id ASC LIMIT 1) AS note ")
          .append("FROM emotion_entries e JOIN emotion_taxonomy et ON et.id = e.primary_emotion_id ")
          .append("WHERE e.user_id = ? ");
        if (from != null) sb.append("AND e.entry_at >= ? ");
        if (to != null) sb.append("AND e.entry_at <= ? ");
        sb.append("ORDER BY e.entry_at DESC, e.id DESC");
        Object[] params;
        if (from != null && to != null) params = new Object[]{utilisateurId, from, to};
        else if (from != null) params = new Object[]{utilisateurId, from};
        else if (to != null) params = new Object[]{utilisateurId, to};
        else params = new Object[]{utilisateurId};
        return jdbc.query(sb.toString(), (rs, rn) -> mapRow(rs), params);
    }

    public boolean supprimer(Long id) {
        int n = jdbc.update("DELETE FROM emotion_entries WHERE id = ?", id);
        return n > 0;
    }
}

