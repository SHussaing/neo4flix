package smahfood.neo4flix.user.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, UserHeaderFilter userHeaderFilter) throws Exception {
        // Gateway enforces JWT for protected routes and forwards X-User-Id.
        // So every endpoint in this service can be permitted; individual controllers may still return 401
        // if X-User-Id is missing.
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .addFilterBefore(userHeaderFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(hb -> hb.disable())
                .formLogin(fl -> fl.disable());

        return http.build();
    }
}
