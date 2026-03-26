package smahfood.neo4flix.rating;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import smahfood.neo4flix.rating.repo.RatingRepository;

@Testcontainers
@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration",
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.cloud.service-registry.auto-registration.enabled=false"
})
class RatingServiceIntegrationTest {

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
    RatingRepository ratingRepository;

    @Test
    void upsertAndFetchMyRating() {
        ratingRepository.upsertRating("u1", "m1", 7);

        var found = ratingRepository.findMyRatingForMovie("u1", "m1");
        assertThat(found).isPresent();
        assertThat(found.get().stars()).isEqualTo(7);

        var list = ratingRepository.listMyRatingsWithMovie("u1");
        assertThat(list).hasSize(1);
        assertThat(list.getFirst().movieId()).isEqualTo("m1");
    }
}
