package smahfood.neo4flix.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        // Gateway auth is enforced by JwtUserHeaderFilter (a GlobalFilter).
        // Keep Spring Security present (classpath) but prevent it from applying to any exchange.
        return http
                .securityMatcher(ServerWebExchangeMatchers.pathMatchers("/__security_disabled__"))
                .build();
    }
}
