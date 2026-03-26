package smahfood.neo4flix.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        // Authentication is handled by JwtUserHeaderFilter at the gateway level.
        // This chain only decides which paths are public vs. protected.
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(ex -> ex
                        // public
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers("/movie/**").permitAll()
                        // protected
                        .pathMatchers("/rating/**").authenticated()
                        .pathMatchers("/recommendation/**").authenticated()
                        .pathMatchers("/users/me", "/users/me/**").authenticated()
                        // everything else public (e.g. swagger later)
                        .anyExchange().permitAll()
                )
                .build();
    }
}

