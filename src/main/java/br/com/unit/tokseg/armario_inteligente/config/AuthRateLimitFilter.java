package br.com.unit.tokseg.armario_inteligente.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
public class AuthRateLimitFilter extends OncePerRequestFilter {

    private final SecurityProperties securityProperties;
    private final StringRedisTemplate redisTemplate;

    public AuthRateLimitFilter(SecurityProperties securityProperties, @Nullable StringRedisTemplate redisTemplate) {
        this.securityProperties = securityProperties;
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/v1/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        SecurityProperties.RateLimit config = securityProperties.rateLimit();
        if (config == null || !config.enabled()) {
            filterChain.doFilter(request, response);
            return;
        }
        if (redisTemplate == null) {
            filterChain.doFilter(request, response);
            return;
        }

        long window = config.windowSeconds();
        long bucket = Instant.now().getEpochSecond() / window;
        String key = "ratelimit:auth:" + request.getRemoteAddr() + ":" + request.getRequestURI() + ":" + bucket;

        Long counter = redisTemplate.opsForValue().increment(key);
        if (counter != null && counter == 1L) {
            redisTemplate.expire(key, window + 1, TimeUnit.SECONDS);
        }
        if (counter != null && counter > config.maxRequests()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
            response.getWriter().write("""
                    {"type":"about:blank","title":"Muitas requisições","status":429,"detail":"Limite de requisições excedido. Tente novamente em instantes."}
                    """);
            return;
        }

        filterChain.doFilter(request, response);
    }
}

