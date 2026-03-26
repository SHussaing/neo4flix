package smahfood.neo4flix.recommendation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import smahfood.neo4flix.recommendation.repo.RecommendationRepository;

@Testcontainers
@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration",
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.cloud.service-registry.auto-registration.enabled=false"
})
class RecommendationServiceIntegrationTest {

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

    @Autowired
    Neo4jClient neo4jClient;

    @Autowired
    RecommendationRepository repository;

    @BeforeEach
    void seed() {
        neo4jClient.query("MATCH (n) DETACH DELETE n").run();
        neo4jClient.query("""
                CREATE (:User {id:'u1'})
                CREATE (:User {id:'u2'})
                CREATE (:Movie {id:'m1', title:'A1', genre:'Action', releaseYear:2001})
                CREATE (:Movie {id:'m2', title:'A2', genre:'Action', releaseYear:2002})
                CREATE (:Movie {id:'m3', title:'R1', genre:'Romance', releaseYear:2010})
                WITH 1 as _
                MATCH (u1:User {id:'u1'}), (u2:User {id:'u2'}), (m1:Movie {id:'m1'}), (m2:Movie {id:'m2'}), (m3:Movie {id:'m3'})
                CREATE (u1)-[:RATED {stars: 9, createdAt: datetime()}]->(m1)
                CREATE (u2)-[:RATED {stars: 8, createdAt: datetime()}]->(m1)
                CREATE (u2)-[:RATED {stars: 9, createdAt: datetime()}]->(m2)
                CREATE (u2)-[:RATED {stars: 7, createdAt: datetime()}]->(m3)
                """).run();
    }

    @Test
    void recommendsUnratedMovies() {
        var recs = repository.recommendForUser("u1", 20);
        assertThat(recs).extracting(r -> r.id()).contains("m2", "m3");
    }

    @Test
    void filtersByGenreAndYear() {
        List<?> actionOnly = repository.recommendForUser("u1", 20, "Action", null, null);
        assertThat(actionOnly).hasSize(1);

        List<?> yearRange = repository.recommendForUser("u1", 20, null, 2005, 2015);
        assertThat(yearRange).hasSize(1);
    }
}
