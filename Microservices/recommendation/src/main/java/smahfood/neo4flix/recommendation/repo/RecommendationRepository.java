package smahfood.neo4flix.recommendation.repo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // First try collaborative filtering.
        List<RecommendationDto> collaborative = recommendCollaborative(userId, limit, genre, yearFrom, yearTo);
        if (!collaborative.isEmpty()) return collaborative;

        // Fallback for new/low-activity users: popular movies by average rating and rating count.
        return recommendPopular(userId, limit, genre, yearFrom, yearTo);
    }

    private List<RecommendationDto> recommendCollaborative(String userId, int limit, String genre, Integer yearFrom, Integer yearTo) {
        // Collaborative filtering-ish:
        // Find other users who rated the same movies, then recommend movies they rated that current user hasn't rated.
        // IMPORTANT: don't make the query depend on relationship types that may not yet exist in a fresh DB.
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("limit", limit);
        params.put("genre", normalize(genre));
        params.put("yearFrom", yearFrom);
        params.put("yearTo", yearTo);

        return neo4jClient.query("""
                MATCH (me:User {id: $userId})-[:RATED]->(m1:Movie)<-[:RATED]-(other:User)-[:RATED]->(m2:Movie)
                WHERE NOT (me)-[:RATED]->(m2)
                  AND ($genre IS NULL OR toLower(m2.genre) CONTAINS toLower($genre))
                  AND ($yearFrom IS NULL OR m2.releaseYear >= $yearFrom)
                  AND ($yearTo IS NULL OR m2.releaseYear <= $yearTo)
                WITH DISTINCT me, m2, count(DISTINCT other) AS score
                WHERE NOT EXISTS { (me)-[:WATCHLISTED]->(m2) }
                RETURN m2.id AS id, m2.title AS title, m2.releaseYear AS releaseYear, m2.genre AS genre,
                       ('Because users with similar taste liked it (score=' + toString(score) + ')') AS reason
                ORDER BY score DESC, title ASC
                LIMIT $limit
                """)
                .bindAll(params)
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

    private List<RecommendationDto> recommendPopular(String userId, int limit, String genre, Integer yearFrom, Integer yearTo) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("limit", limit);
        params.put("genre", normalize(genre));
        params.put("yearFrom", yearFrom);
        params.put("yearTo", yearTo);

        return neo4jClient.query("""
                MATCH (m:Movie)
                WHERE ($genre IS NULL OR toLower(m.genre) CONTAINS toLower($genre))
                  AND ($yearFrom IS NULL OR m.releaseYear >= $yearFrom)
                  AND ($yearTo IS NULL OR m.releaseYear <= $yearTo)
                WITH m
                OPTIONAL MATCH (:User)-[r:RATED]->(m)
                WITH m, avg(toFloat(r.stars)) AS avgStars, count(r) AS ratingCount
                ORDER BY ratingCount DESC, avgStars DESC, m.title ASC
                LIMIT $limit
                RETURN m.id AS id, m.title AS title, m.releaseYear AS releaseYear, m.genre AS genre,
                       (CASE
                          WHEN ratingCount > 0 THEN 'Popular right now (avg=' + toString(round(avgStars*10)/10) + ', ratings=' + toString(ratingCount) + ')'
                          ELSE 'Popular in the catalog'
                        END) AS reason
                """)
                .bindAll(params)
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
