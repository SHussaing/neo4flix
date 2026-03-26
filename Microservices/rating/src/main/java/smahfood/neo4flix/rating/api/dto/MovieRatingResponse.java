package smahfood.neo4flix.rating.api.dto;

import java.time.Instant;

public record MovieRatingResponse(
        String movieId,
        Integer stars,
        Instant createdAt
) {
}

