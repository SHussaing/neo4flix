package smahfood.neo4flix.recommendation.repo;

import java.util.List;

import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import smahfood.neo4flix.recommendation.api.dto.RecommendationDto;

@Repository
public class RecommendationRepository {

    private final Neo4jClient neo4jClient;

    public RecommendationRepository(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public List<RecommendationDto> recommendForUser(String userId, int limit) {
        return recommendForUser(userId, limit, null, null, null);
    }

    public List<RecommendationDto> recommendForUser(String userId, int limit, String genre, Integer yearFrom, Integer yearTo) {
        // Collaborative filtering-ish:
        // Find other users who rated the same movies, then recommend movies they rated that current user hasn't rated.
        return neo4jClient.query("""
                MATCH (me:User {id: $userId})-[:RATED]->(m1:Movie)<-[:RATED]-(other:User)-[:RATED]->(m2:Movie)
                WHERE NOT (me)-[:RATED]->(m2)
                  AND NOT (me)-[:WATCHLISTED]->(m2)
                  AND ($genre IS NULL OR toLower(m2.genre) CONTAINS toLower($genre))
                  AND ($yearFrom IS NULL OR m2.releaseYear >= $yearFrom)
                  AND ($yearTo IS NULL OR m2.releaseYear <= $yearTo)
                WITH m2, count(DISTINCT other) AS score
                RETURN m2.id AS id, m2.title AS title, m2.releaseYear AS releaseYear, m2.genre AS genre,
                       ('Because users with similar taste liked it (score=' + toString(score) + ')') AS reason
                ORDER BY score DESC, title ASC
                LIMIT $limit
                """)
                .bindAll(java.util.Map.of(
                        "userId", userId,
                        "limit", limit,
                        "genre", normalize(genre),
                        "yearFrom", yearFrom,
                        "yearTo", yearTo
                ))
                .fetchAs(RecommendationDto.class)
                .mappedBy((typeSystem, record) -> new RecommendationDto(
                        record.get("id").asString(),
                        record.get("title").isNull() ? null : record.get("title").asString(),
                        record.get("releaseYear").isNull() ? null : record.get("releaseYear").asInt(),
                        record.get("genre").isNull() ? null : record.get("genre").asString(),
                        record.get("reason").isNull() ? null : record.get("reason").asString()
                ))
                .all()
                .stream()
                .toList();
    }

    private static String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isBlank() ? null : t;
    }
}
