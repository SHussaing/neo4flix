package smahfood.neo4flix.gateway.security;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

@Component
public class JwtUserHeaderFilter implements GlobalFilter, Ordered {

    private static final String HEADER_USER_ID = "X-User-Id";

    private final SecretKey key;

    public JwtUserHeaderFilter(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public int getOrder() {
        return -100; // early
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        boolean requiresAuth = requiresAuth(path);

        // Only login/register are public. Everything else is decided by requiresAuth.
        if (isPublicAuthEndpoint(path)) {
            return chain.filter(exchange);
        }

        String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            if (requiresAuth) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            return chain.filter(exchange);
        }

        String token = auth.substring("Bearer ".length());

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String userId = claims.getSubject();
            if (userId == null || userId.isBlank()) {
                if (requiresAuth) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
                return chain.filter(exchange);
            }

            ServerWebExchange mutated = exchange.mutate()
                    .request(r -> r.headers(h -> h.set(HEADER_USER_ID, userId)))
                    .build();

            return chain.filter(mutated);
        } catch (Exception ex) {
            if (requiresAuth) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            return chain.filter(exchange);
        }
    }

    private static boolean requiresAuth(String path) {
        // Public movies so home page works without login.
        if (path.startsWith("/movie")) return false;

        // Public auth endpoints:
        if (isPublicAuthEndpoint(path)) return false;

        // 2FA must be protected.
        if (path.startsWith("/auth/2fa")) return true;

        // Protected:
        if (path.startsWith("/rating")) return true;
        if (path.startsWith("/recommendation")) return true;
        if (path.startsWith("/users/me")) return true;

        // Default: public
        return false;
    }

    private static boolean isPublicAuthEndpoint(String path) {
        return path.equals("/auth/login") || path.equals("/auth/register");
    }
}
