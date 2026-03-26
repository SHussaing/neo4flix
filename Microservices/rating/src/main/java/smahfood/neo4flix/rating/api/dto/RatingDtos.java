package smahfood.neo4flix.rating.api.dto;

import java.time.Instant;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public final class RatingDtos {

    private RatingDtos() {
    }

    public record RateMovieRequest(
            @NotNull @Min(1) @Max(10) Integer stars
    ) {
    }

    public record RatingResponse(
            String movieId,
            Integer stars,
            Instant createdAt
    ) {
    }

    public record MyRatingItem(
            String movieId,
            String title,
            Integer releaseYear,
            String genre,
            Integer stars,
            Instant createdAt
    ) {
    }
}