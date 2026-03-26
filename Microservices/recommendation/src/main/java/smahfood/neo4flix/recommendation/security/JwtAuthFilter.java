package smahfood.neo4flix.recommendation.security;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String ATTR_USER_ID = "userId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Gateway will validate JWT; it should forward the user id for internal services.
        String userId = request.getHeader("X-User-Id");
        if (userId != null && !userId.isBlank()) {
            request.setAttribute(ATTR_USER_ID, userId);
        }
        filterChain.doFilter(request, response);
    }
}

