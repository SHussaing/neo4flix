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
                    new MovieNode("m5", "La La Land", "A musician and an actress fall in love in LA.", 2016, "Romance", null),

                    new MovieNode("m6", "Fight Club", "An insomniac meets a soap maker with a dark secret.", 1999, "Drama", null),
                    new MovieNode("m7", "Forrest Gump", "A simple man lives an extraordinary life.", 1994, "Drama", null),
                    new MovieNode("m8", "The Shawshank Redemption", "Two imprisoned men bond over years.", 1994, "Drama", null),
                    new MovieNode("m9", "Gladiator", "A Roman general seeks revenge.", 2000, "Action", null),
                    new MovieNode("m10", "Titanic", "A love story aboard a doomed ship.", 1997, "Romance", null),

                    new MovieNode("m11", "Avatar", "Humans explore an alien world called Pandora.", 2009, "Sci-Fi", null),
                    new MovieNode("m12", "The Avengers", "Earth’s mightiest heroes unite.", 2012, "Action", null),
                    new MovieNode("m13", "Iron Man", "A billionaire builds a powered suit.", 2008, "Action", null),
                    new MovieNode("m14", "Doctor Strange", "A surgeon learns mystic arts.", 2016, "Fantasy", null),
                    new MovieNode("m15", "Black Panther", "A king defends his nation Wakanda.", 2018, "Action", null),

                    new MovieNode("m16", "The Lion King", "A lion cub embraces his destiny.", 1994, "Animation", null),
                    new MovieNode("m17", "Frozen", "Two sisters face magical challenges.", 2013, "Animation", null),
                    new MovieNode("m18", "Toy Story", "Toys come to life when humans aren’t around.", 1995, "Animation", null),
                    new MovieNode("m19", "Finding Nemo", "A fish searches for his son.", 2003, "Animation", null),
                    new MovieNode("m20", "Up", "An old man travels in a house lifted by balloons.", 2009, "Animation", null),

                    new MovieNode("m21", "Joker", "A failed comedian descends into madness.", 2019, "Drama", null),
                    new MovieNode("m22", "Parasite", "A poor family infiltrates a rich household.", 2019, "Thriller", null),
                    new MovieNode("m23", "Whiplash", "A drummer faces a ruthless instructor.", 2014, "Drama", null),
                    new MovieNode("m24", "The Social Network", "The story of Facebook’s creation.", 2010, "Drama", null),
                    new MovieNode("m25", "The Wolf of Wall Street", "A stockbroker rises and falls.", 2013, "Drama", null),

                    new MovieNode("m26", "Mad Max: Fury Road", "A high-speed chase in a post-apocalyptic world.", 2015, "Action", null),
                    new MovieNode("m27", "John Wick", "A retired hitman seeks vengeance.", 2014, "Action", null),
                    new MovieNode("m28", "Mission Impossible: Fallout", "An agent prevents a global disaster.", 2018, "Action", null),
                    new MovieNode("m29", "Skyfall", "James Bond faces a cyberterrorist.", 2012, "Action", null),
                    new MovieNode("m30", "Top Gun: Maverick", "A pilot trains a new generation.", 2022, "Action", null),

                    new MovieNode("m31", "Harry Potter and the Sorcerer's Stone", "A boy discovers he is a wizard.", 2001, "Fantasy", null),
                    new MovieNode("m32", "The Lord of the Rings: The Fellowship of the Ring", "A hobbit begins a quest.", 2001, "Fantasy", null),
                    new MovieNode("m33", "The Hobbit", "A reluctant hero joins a quest.", 2012, "Fantasy", null),
                    new MovieNode("m34", "Pirates of the Caribbean", "A pirate seeks treasure.", 2003, "Adventure", null),
                    new MovieNode("m35", "The Chronicles of Narnia", "Children discover a magical land.", 2005, "Fantasy", null),

                    new MovieNode("m36", "Get Out", "A man uncovers disturbing secrets.", 2017, "Horror", null),
                    new MovieNode("m37", "A Quiet Place", "A family survives in silence.", 2018, "Horror", null),
                    new MovieNode("m38", "The Conjuring", "Paranormal investigators face a dark force.", 2013, "Horror", null),
                    new MovieNode("m39", "It", "A group of kids face a shape-shifting clown.", 2017, "Horror", null),
                    new MovieNode("m40", "Hereditary", "A family experiences terrifying events.", 2018, "Horror", null),

                    new MovieNode("m41", "The Grand Budapest Hotel", "A concierge gets involved in a mystery.", 2014, "Comedy", null),
                    new MovieNode("m42", "Superbad", "Two teens navigate a wild night.", 2007, "Comedy", null),
                    new MovieNode("m43", "The Hangover", "Friends search for their missing groom.", 2009, "Comedy", null),
                    new MovieNode("m44", "Step Brothers", "Two grown men become stepbrothers.", 2008, "Comedy", null),
                    new MovieNode("m45", "Deadpool", "A sarcastic antihero seeks revenge.", 2016, "Comedy", null),

                    new MovieNode("m46", "Dune", "A young noble fights for control of a desert planet.", 2021, "Sci-Fi", null),
                    new MovieNode("m47", "Blade Runner 2049", "A blade runner uncovers secrets.", 2017, "Sci-Fi", null),
                    new MovieNode("m48", "Arrival", "A linguist communicates with aliens.", 2016, "Sci-Fi", null),
                    new MovieNode("m49", "Gravity", "An astronaut struggles to survive in space.", 2013, "Sci-Fi", null),
                    new MovieNode("m50", "The Martian", "An astronaut is stranded on Mars.", 2015, "Sci-Fi", null)
            );

            movieRepository.saveAll(seed);
        };
    }
}

