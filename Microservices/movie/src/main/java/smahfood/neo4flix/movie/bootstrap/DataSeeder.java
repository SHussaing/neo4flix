package smahfood.neo4flix.movie.bootstrap;

import java.util.List;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import smahfood.neo4flix.movie.domain.MovieNode;
import smahfood.neo4flix.movie.repo.MovieRepository;

@Configuration
public class DataSeeder {

    @Bean
    ApplicationRunner seedMovies(
            MovieRepository movieRepository,
            @Value("${neo4flix.seed.enabled:true}") boolean enabled
    ) {
        return args -> {
            if (!enabled) return;

            // Seed only if DB is empty
            if (movieRepository.count() > 0) return;

            List<MovieNode> seed = List.of(
                    new MovieNode("m1", "The Matrix", "A hacker discovers reality is a simulation.", 1999, "Sci-Fi", null),
                    new MovieNode("m2", "Inception", "A thief steals secrets through dream-sharing technology.", 2010, "Sci-Fi", null),
                    new MovieNode("m3", "Interstellar", "Explorers travel through a wormhole in space.", 2014, "Sci-Fi", null),
                    new MovieNode("m4", "The Dark Knight", "Batman faces the Joker in Gotham City.", 2008, "Action", null),
                    new MovieNode("m5", "La La Land", "A musician and an actress fall in love in LA.", 2016, "Romance", null)
            );

            movieRepository.saveAll(seed);
        };
    }
}

