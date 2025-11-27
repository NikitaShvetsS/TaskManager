package org.example.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.service.CustomUserDetailsService;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private static final List<String> PUBLIC = List.of(
            "/api/auth/**",
            "/api/test/health",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        log.debug("Checking shouldNotFilter for uri={}", uri);
        return PUBLIC.stream().anyMatch(p -> PATH_MATCHER.match(p, uri));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String uri = req.getRequestURI();
        log.debug("JwtFilter triggered for path={}", uri);

        String header = req.getHeader("Authorization");
        log.debug("Authorization header = {}", header);

        if (header == null || header.isBlank()) {
            chain.doFilter(req, res);
            return;
        }

        try {
            String token;
            if (header.startsWith("Bearer ")) {
                token = header.substring(7);
            } else {
                token = header;
            }

            Claims claims = jwtUtil.parseClaims(token);
            String email = claims.get("email", String.class);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("JWT authentication successful for email={}", email);
                } else {
                    log.warn("JWT validation failed for email={}", email);
                }
            }

            chain.doFilter(req, res);

        } catch (Exception e) {
            log.error("JWT authentication failed: {}", e.getMessage(), e);
            SecurityContextHolder.clearContext();
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            res.getWriter().write("{\"error\":\"Unauthorized\"}");
        }
    }
}


