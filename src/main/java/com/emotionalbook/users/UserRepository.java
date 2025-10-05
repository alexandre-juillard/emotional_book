package com.emotionalbook.users;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static User map(java.sql.ResultSet rs) throws java.sql.SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFirstName(rs.getString("first_name"));
        u.setLastName(rs.getString("last_name"));
        u.setRole(rs.getString("role"));
        u.setLocale(rs.getString("locale"));
        u.setTimezone(rs.getString("timezone"));
        u.setAnonymous(rs.getBoolean("is_anonymous"));
        var created = rs.getTimestamp("created_at");
        var updated = rs.getTimestamp("updated_at");
        if (created != null) u.setCreatedAt(created.toLocalDateTime());
        if (updated != null) u.setUpdatedAt(updated.toLocalDateTime());
        return u;
    }

    public Optional<User> trouverParId(long id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject("SELECT * FROM users WHERE id=?", (rs, rn) -> map(rs), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<User> trouverParEmail(String email) {
        try {
            return Optional.ofNullable(jdbc.queryForObject("SELECT * FROM users WHERE email=?", (rs, rn) -> map(rs), email));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public long creer(String email, String passwordHash, String firstName, String lastName) {
        String sql = "INSERT INTO users(email, password_hash, first_name, last_name) VALUES (?,?,?,?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, email);
            ps.setString(2, passwordHash);
            ps.setString(3, firstName);
            ps.setString(4, lastName);
            return ps;
        }, kh);
        return ((Number) kh.getKey()).longValue();
    }

    public void mettreAJourProfil(long id, String firstName, String lastName, String locale, String timezone) {
        String sql = "UPDATE users SET first_name = COALESCE(?, first_name), last_name = COALESCE(?, last_name), locale = COALESCE(?, locale), timezone = COALESCE(?, timezone) WHERE id = ?";
        jdbc.update(sql, firstName, lastName, locale, timezone, id);
    }
}
