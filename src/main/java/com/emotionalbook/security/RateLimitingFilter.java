package com.emotionalbook.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final ObjectMapper mapper = new ObjectMapper();

    private final int globalCapacity;
    private final int globalPeriodSeconds;
    private final int loginCapacity;
    private final int loginPeriodSeconds;
    private final int registerCapacity;
    private final int registerPeriodSeconds;

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public RateLimitingFilter(
            @Value("${security.rate-limit.global.capacity:120}") int globalCapacity,
            @Value("${security.rate-limit.global.period-seconds:60}") int globalPeriodSeconds,
            @Value("${security.rate-limit.login.capacity:20}") int loginCapacity,
            @Value("${security.rate-limit.login.period-seconds:60}") int loginPeriodSeconds,
            @Value("${security.rate-limit.register.capacity:10}") int registerCapacity,
            @Value("${security.rate-limit.register.period-seconds:60}") int registerPeriodSeconds
    ) {
        this.globalCapacity = globalCapacity;
        this.globalPeriodSeconds = globalPeriodSeconds;
        this.loginCapacity = loginCapacity;
        this.loginPeriodSeconds = loginPeriodSeconds;
        this.registerCapacity = registerCapacity;
        this.registerPeriodSeconds = registerPeriodSeconds;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        String ip = clientIp(request);
        String key = ip + scopeKey(path);
        Bucket bucket = buckets.computeIfAbsent(key, k -> newBucketForPath(path));
        var probe = bucket.tryConsumeAndReturnRemaining(1);
        response.setHeader("X-RateLimit-Remaining", String.valueOf(probe.getRemainingTokens()));
        if (!probe.isConsumed()) {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            mapper.writeValue(response.getOutputStream(), Map.of(
                    "message", "Trop de requêtes, merci de réessayer plus tard.",
                    "retryAfterSeconds", periodSecondsForPath(path)
            ));
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String clientIp(HttpServletRequest req) {
        String xf = req.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return Objects.toString(req.getRemoteAddr(), "unknown");
    }

    private String scopeKey(String path) {
        if (path.startsWith("/auth/login")) return ":LOGIN";
        if (path.startsWith("/auth/register")) return ":REGISTER";
        return ":GLOBAL";
    }

    private int periodSecondsForPath(String path) {
        if (path.startsWith("/auth/login")) return loginPeriodSeconds;
        if (path.startsWith("/auth/register")) return registerPeriodSeconds;
        return globalPeriodSeconds;
    }

    private Bucket newBucketForPath(String path) {
        if (path.startsWith("/auth/login")) {
            Bandwidth bw = Bandwidth.classic(loginCapacity, Refill.greedy(loginCapacity, Duration.ofSeconds(loginPeriodSeconds)));
            return Bucket.builder().addLimit(bw).build();
        }
        if (path.startsWith("/auth/register")) {
            Bandwidth bw = Bandwidth.classic(registerCapacity, Refill.greedy(registerCapacity, Duration.ofSeconds(registerPeriodSeconds)));
            return Bucket.builder().addLimit(bw).build();
        }
        Bandwidth bw = Bandwidth.classic(globalCapacity, Refill.greedy(globalCapacity, Duration.ofSeconds(globalPeriodSeconds)));
        return Bucket.builder().addLimit(bw).build();
    }
}

