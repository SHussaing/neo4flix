package smahfood.neo4flix.movie.api.dto;
import java.time.LocalDate;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public final class MovieDtos {
    private MovieDtos() {
    }
    public record CreateMovieRequest(
            @NotBlank @Size(min = 1, max = 200) String title,
            @Size(max = 4000) String overview,
            @Min(1888) @Max(2100) Integer releaseYear,
            @Size(max = 100) String genre,
            LocalDate releaseDate
    ) {
    }
    public record UpdateMovieRequest(
            @NotBlank @Size(min = 1, max = 200) String title,
            @Size(max = 4000) String overview,
            @Min(1888) @Max(2100) Integer releaseYear,
            @Size(max = 100) String genre,
            LocalDate releaseDate
    ) {
    }
    public record MovieDetailsResponse(
            String id,
            String title,
            String overview,
            Integer releaseYear,
            String genre,
            LocalDate releaseDate,
            Double averageRating,
            Long ratingCount
    ) {
    }
}