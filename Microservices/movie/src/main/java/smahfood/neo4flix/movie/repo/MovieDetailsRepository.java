package smahfood.neo4flix.movie.repo;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import smahfood.neo4flix.movie.api.dto.MovieDtos;

@Repository
public class MovieDetailsRepository {

    private final Neo4jClient neo4jClient;

    public MovieDetailsRepository(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public Optional<MovieDtos.MovieDetailsResponse> findDetailsById(String id) {
        return neo4jClient.query("""
                MATCH (m:Movie {id: $id})
                OPTIONAL MATCH (:User)-[r:RATED]->(m)
                RETURN m.id AS id,
                       m.title AS title,
                       m.overview AS overview,
                       m.releaseYear AS releaseYear,
                       m.genre AS genre,
                       m.releaseDate AS releaseDate,
                       avg(toFloat(r.stars)) AS averageRating,
                       count(r) AS ratingCount
                """)
                .bind(id).to("id")
                .fetchAs(MovieDtos.MovieDetailsResponse.class)
                .mappedBy((typeSystem, record) -> new MovieDtos.MovieDetailsResponse(
                        record.get("id").asString(),
                        record.get("title").isNull() ? null : record.get("title").asString(),
                        record.get("overview").isNull() ? null : record.get("overview").asString(),
                        record.get("releaseYear").isNull() ? null : record.get("releaseYear").asInt(),
                        record.get("genre").isNull() ? null : record.get("genre").asString(),
                        record.get("releaseDate").isNull() ? null : record.get("releaseDate").asLocalDate(),
                        record.get("averageRating").isNull() ? null : record.get("averageRating").asDouble(),
                        record.get("ratingCount").isNull() ? 0L : record.get("ratingCount").asLong()
                ))
                .one();
    }

    // Useful when SDN driver can't map LocalDate in some configurations.
    @SuppressWarnings("unused")
    private static LocalDate safeLocalDate(Object o) {
        return (o instanceof LocalDate ld) ? ld : null;
    }
}

