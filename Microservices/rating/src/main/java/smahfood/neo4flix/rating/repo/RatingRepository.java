package smahfood.neo4flix.rating.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import smahfood.neo4flix.rating.api.dto.MovieRatingResponse;
import smahfood.neo4flix.rating.api.dto.RatingDtos;

@Repository
public class RatingRepository {

    private final Neo4jClient neo4jClient;

    public RatingRepository(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public void upsertRating(String userId, String movieId, int stars) {
        neo4jClient.query("""
                MERGE (u:User {id: $userId})
                MERGE (m:Movie {id: $movieId})
                MERGE (u)-[r:RATED]->(m)
                SET r.stars = $stars, r.createdAt = coalesce(r.createdAt, datetime())
                """)
                .bindAll(java.util.Map.of("userId", userId, "movieId", movieId, "stars", stars))
                .run();
    }

    public Optional<MovieRatingResponse> findMyRatingForMovie(String userId, String movieId) {
        return neo4jClient.query("""
                MATCH (u:User {id: $userId})-[r:RATED]->(m:Movie {id: $movieId})
                RETURN m.id as movieId, r.stars as stars, r.createdAt as createdAt
                """)
                .bindAll(java.util.Map.of("userId", userId, "movieId", movieId))
                .fetchAs(MovieRatingResponse.class)
                .mappedBy((typeSystem, record) -> new MovieRatingResponse(
                        record.get("movieId").asString(),
                        record.get("stars").isNull() ? null : record.get("stars").asInt(),
                        record.get("createdAt").isNull() ? null : record.get("createdAt").asZonedDateTime().toInstant()
                ))
                .one();
    }

    public List<RatingDtos.RatingResponse> listMyRatings(String userId) {
        return neo4jClient.query("""
                MATCH (u:User {id: $userId})-[r:RATED]->(m:Movie)
                RETURN m.id as movieId, r.stars as stars, r.createdAt as createdAt
                ORDER BY r.createdAt DESC
                """)
                .bind(userId).to("userId")
                .fetchAs(RatingDtos.RatingResponse.class)
                .mappedBy((typeSystem, record) -> new RatingDtos.RatingResponse(
                        record.get("movieId").asString(),
                        record.get("stars").isNull() ? null : record.get("stars").asInt(),
                        record.get("createdAt").isNull() ? null : record.get("createdAt").asZonedDateTime().toInstant()
                ))
                .all()
                .stream()
                .toList();
    }

    public void addToWatchlist(String userId, String movieId) {
        neo4jClient.query("""
                MERGE (u:User {id: $userId})
                MERGE (m:Movie {id: $movieId})
                MERGE (u)-[w:WATCHLISTED]->(m)
                SET w.createdAt = coalesce(w.createdAt, datetime())
                """)
                .bindAll(java.util.Map.of("userId", userId, "movieId", movieId))
                .run();
    }

    public void removeFromWatchlist(String userId, String movieId) {
        neo4jClient.query("""
                MATCH (u:User {id: $userId})-[w:WATCHLISTED]->(m:Movie {id: $movieId})
                DELETE w
                """)
                .bindAll(java.util.Map.of("userId", userId, "movieId", movieId))
                .run();
    }

    public List<java.util.Map<String, Object>> listWatchlistRaw(String userId) {
        return neo4jClient.query("""
                MATCH (u:User {id: $userId})-[w:WATCHLISTED]->(m:Movie)
                RETURN m.id as id, m.title as title, m.overview as overview, m.releaseYear as releaseYear, m.genre as genre
                ORDER BY w.createdAt DESC
                """)
                .bind(userId).to("userId")
                .fetch()
                .all()
                .stream()
                .toList();
    }

    public List<RatingDtos.MyRatingItem> listMyRatingsWithMovie(String userId) {
        return neo4jClient.query("""
                MATCH (u:User {id: $userId})-[r:RATED]->(m:Movie)
                RETURN m.id as movieId,
                       m.title as title,
                       m.releaseYear as releaseYear,
                       m.genre as genre,
                       r.stars as stars,
                       r.createdAt as createdAt
                ORDER BY r.createdAt DESC
                """)
                .bind(userId).to("userId")
                .fetchAs(RatingDtos.MyRatingItem.class)
                .mappedBy((typeSystem, record) -> new RatingDtos.MyRatingItem(
                        record.get("movieId").asString(),
                        record.get("title").isNull() ? null : record.get("title").asString(),
                        record.get("releaseYear").isNull() ? null : record.get("releaseYear").asInt(),
                        record.get("genre").isNull() ? null : record.get("genre").asString(),
                        record.get("stars").isNull() ? null : record.get("stars").asInt(),
                        record.get("createdAt").isNull() ? null : record.get("createdAt").asZonedDateTime().toInstant()
                ))
                .all()
                .stream()
                .toList();
    }
}
