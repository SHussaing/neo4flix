package smahfood.neo4flix.movie;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import smahfood.neo4flix.movie.domain.MovieNode;
import smahfood.neo4flix.movie.repo.MovieDetailsRepository;
import smahfood.neo4flix.movie.repo.MovieRepository;

@Testcontainers
@SpringBootTest(properties = {
        "neo4flix.seed.enabled=false",
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.cloud.service-registry.auto-registration.enabled=false"
})
class MovieServiceIntegrationTest {

    @Container
    static final Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5.26-community")
            .withAdminPassword("password");

    @DynamicPropertySource
    static void neo4jProps(DynamicPropertyRegistry registry) {
        registry.add("spring.neo4j.uri", neo4j::getBoltUrl);
        registry.add("spring.neo4j.authentication.username", () -> "neo4j");
        registry.add("spring.neo4j.authentication.password", () -> "password");
    }

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    MovieDetailsRepository movieDetailsRepository;

    @Test
    void canCreateAndFetchMovieAndDetails() {
        MovieNode m = new MovieNode("m_test", "Test Movie", "Overview", 2020, "Action", null);
        movieRepository.save(m);

        var found = movieRepository.findById("m_test");
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test Movie");

        var details = movieDetailsRepository.findDetailsById("m_test");
        assertThat(details).isPresent();
        assertThat(details.get().averageRating()).isNull();
        assertThat(details.get().ratingCount()).isEqualTo(0L);
    }
}
