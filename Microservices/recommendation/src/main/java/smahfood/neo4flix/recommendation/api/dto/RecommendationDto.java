package smahfood.neo4flix.recommendation.api.dto;
public record RecommendationDto(
        String id,
        String title,
        Integer releaseYear,
        String genre,
        String reason
) {
}