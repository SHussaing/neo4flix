package smahfood.neo4flix.rating.security;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String ATTR_USER_ID = "userId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // For MVP, we trust gateway to validate the JWT.
        // Gateway will forward X-User-Id header (we'll add this next in gateway).
        String userId = request.getHeader("X-User-Id");
        if (userId != null && !userId.isBlank()) {
            request.setAttribute(ATTR_USER_ID, userId);
        }
        filterChain.doFilter(request, response);
    }
}

