package smahfood.neo4flix.movie.config;

// NOTE: This class is intentionally disabled because `smahfood.neo4flix.movie.bootstrap.DataSeeder`
// already provides a configurable seeding bean named `seedMovies`. Keeping both causes a
// BeanDefinitionOverrideException.

// import java.time.LocalDate;
// import java.util.List;
// import java.util.UUID;
//
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
//
// import smahfood.neo4flix.movie.domain.MovieNode;
// import smahfood.neo4flix.movie.repo.MovieRepository;
//
// @Configuration
// public class DevDataLoader {
//
//     @Bean
//     CommandLineRunner seedMovies(MovieRepository repo) {
//         return args -> {
//             if (repo.count() > 0) return;
//
//             List<MovieNode> seed = List.of(
//                     new MovieNode(UUID.randomUUID().toString(), "The Matrix", "A hacker discovers reality is a simulation.", 1999, "Sci-Fi", LocalDate.of(1999, 3, 31)),
//                     new MovieNode(UUID.randomUUID().toString(), "Inception", "A thief steals secrets through dream-sharing technology.", 2010, "Sci-Fi", LocalDate.of(2010, 7, 16)),
//                     new MovieNode(UUID.randomUUID().toString(), "Interstellar", "Explorers travel through a wormhole in space.", 2014, "Sci-Fi", LocalDate.of(2014, 11, 7)),
//                     new MovieNode(UUID.randomUUID().toString(), "The Godfather", "The aging patriarch transfers control of his empire.", 1972, "Crime", LocalDate.of(1972, 3, 24)),
//                     new MovieNode(UUID.randomUUID().toString(), "Spirited Away", "A girl enters a world of spirits.", 2001, "Animation", LocalDate.of(2001, 7, 20))
//             );
//
//             repo.saveAll(seed);
//         };
//     }
// }
