package com.emotionalbook.security;

import com.emotionalbook.users.User;
import com.emotionalbook.users.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                Claims claims = jwtService.validerEtExtraire(token);
                Object uidObj = claims.get("uid");
                String email = claims.getSubject();
                if (uidObj != null && email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    long uid = Long.parseLong(uidObj.toString());
                    Optional<User> u = userRepository.trouverParId(uid);
                    if (u.isPresent()) {
                        var principal = u.get();
                        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                        String role = principal.getRole();
                        if (role != null && !role.isBlank()) {
                            String upper = role.toUpperCase();
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + upper));
                            // Hiérarchie: ADMIN => ADMIN + USER
                            if ("ADMIN".equals(upper)) {
                                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                            }
                        }
                        var authToken = new UsernamePasswordAuthenticationToken(principal, null, authorities);
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (Exception ignored) {
                // Token invalide/expiré: on ignore, les routes protégées seront 401
            }
        }
        filterChain.doFilter(request, response);
    }
}
