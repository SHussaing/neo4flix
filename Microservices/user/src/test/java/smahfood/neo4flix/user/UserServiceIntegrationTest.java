package smahfood.neo4flix.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.autoconfigure.exclude=org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration",
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.cloud.service-registry.auto-registration.enabled=false"
})
class UserServiceIntegrationTest {

    @Container
    static final Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5.26-community")
            .withAdminPassword("password");

    @DynamicPropertySource
    static void neo4jProps(DynamicPropertyRegistry registry) {
        registry.add("spring.neo4j.uri", neo4j::getBoltUrl);
        registry.add("spring.neo4j.authentication.username", () -> "neo4j");
        registry.add("spring.neo4j.authentication.password", () -> "password");
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.cloud.discovery.enabled", () -> "false");
        registry.add("spring.cloud.service-registry.auto-registration.enabled", () -> "false");
    }

    @LocalServerPort
    int port;

    @Autowired
    ObjectMapper objectMapper;

    private RestClient client() {
        return RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void registerThenLoginReturnsJwt() throws Exception {
        var reg = Map.of("email", "a@b.com", "name", "Alice", "password", "Aa123456!");

        ResponseEntity<Void> regRes = client().post().uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(reg)
                .retrieve()
                .toBodilessEntity();
        assertThat(regRes.getStatusCode().is2xxSuccessful()).isTrue();

        var login = Map.of("email", "a@b.com", "password", "Aa123456!");
        ResponseEntity<String> loginRes = client().post().uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(login)
                .retrieve()
                .toEntity(String.class);

        assertThat(loginRes.getStatusCode().value()).isEqualTo(200);
        Map<String, Object> body = objectMapper.readValue(loginRes.getBody(), new TypeReference<>() {
        });
        assertThat(body.get("token")).isNotNull();
    }

    @Test
    void disable2faRequiresCorrectPassword() throws Exception {
        var reg = Map.of("email", "c@d.com", "name", "Carol", "password", "Aa123456!");
        ResponseEntity<Void> regRes = client().post().uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(reg)
                .retrieve()
                .toBodilessEntity();
        assertThat(regRes.getStatusCode().is2xxSuccessful()).isTrue();

        var login = Map.of("email", "c@d.com", "password", "Aa123456!");
        ResponseEntity<String> loginRes = client().post().uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(login)
                .retrieve()
                .toEntity(String.class);
        assertThat(loginRes.getStatusCode().value()).isEqualTo(200);

        Map<String, Object> auth = objectMapper.readValue(loginRes.getBody(), new TypeReference<>() {
        });
        @SuppressWarnings("unchecked")
        Map<String, Object> user = (Map<String, Object>) auth.get("user");
        String userId = String.valueOf(user.get("id"));
        assertThat(userId).isNotBlank();

        // Without header => 401
        int noHeaderStatus = client().post().uri("/auth/2fa/disable")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("password", "Aa123456!"))
                .exchange((req, res) -> res.getStatusCode().value());
        assertThat(noHeaderStatus).isEqualTo(401);

        // Wrong password => 400
        int badStatus = client().post().uri("/auth/2fa/disable")
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("password", "wrong"))
                .exchange((req, res) -> res.getStatusCode().value());
        assertThat(badStatus).isEqualTo(400);

        // Correct password => 200
        int okStatus = client().post().uri("/auth/2fa/disable")
                .header("X-User-Id", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("password", "Aa123456!"))
                .exchange((req, res) -> res.getStatusCode().value());
        assertThat(okStatus).isEqualTo(200);
    }
}
