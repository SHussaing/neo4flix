package smahfood.neo4flix.rating.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import smahfood.neo4flix.rating.api.dto.MovieRatingResponse;
import smahfood.neo4flix.rating.api.dto.RatingDtos;
import smahfood.neo4flix.rating.repo.RatingRepository;
import smahfood.neo4flix.rating.security.JwtAuthFilter;

@RestController
public class RatingController {

    private final RatingRepository ratingRepository;

    public RatingController(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @PutMapping("/rating/{movieId}")
    public ResponseEntity<Void> rate(
            @PathVariable String movieId,
            @Valid @RequestBody RatingDtos.RateMovieRequest request,
            HttpServletRequest http
    ) {
        String userId = (String) http.getAttribute(JwtAuthFilter.ATTR_USER_ID);
        if (userId == null) return ResponseEntity.status(401).build();

        ratingRepository.upsertRating(userId, movieId, request.stars());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rating/me")
    public ResponseEntity<List<RatingDtos.MyRatingItem>> myRatings(HttpServletRequest http) {
        String userId = (String) http.getAttribute(JwtAuthFilter.ATTR_USER_ID);
        if (userId == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(ratingRepository.listMyRatingsWithMovie(userId));
    }

    @GetMapping("/rating/me/{movieId}")
    public ResponseEntity<MovieRatingResponse> myRatingForMovie(@PathVariable String movieId, HttpServletRequest http) {
        String userId = (String) http.getAttribute(JwtAuthFilter.ATTR_USER_ID);
        if (userId == null) return ResponseEntity.status(401).build();

        return ratingRepository.findMyRatingForMovie(userId, movieId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/rating/watchlist/{movieId}")
    public ResponseEntity<Void> addToWatchlist(@PathVariable String movieId, HttpServletRequest http) {
        String userId = (String) http.getAttribute(JwtAuthFilter.ATTR_USER_ID);
        if (userId == null) return ResponseEntity.status(401).build();

        ratingRepository.addToWatchlist(userId, movieId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/rating/watchlist/{movieId}")
    public ResponseEntity<Void> removeFromWatchlist(@PathVariable String movieId, HttpServletRequest http) {
        String userId = (String) http.getAttribute(JwtAuthFilter.ATTR_USER_ID);
        if (userId == null) return ResponseEntity.status(401).build();

        ratingRepository.removeFromWatchlist(userId, movieId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rating/watchlist/me")
    public ResponseEntity<List<java.util.Map<String, Object>>> myWatchlist(HttpServletRequest http) {
        String userId = (String) http.getAttribute(JwtAuthFilter.ATTR_USER_ID);
        if (userId == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(ratingRepository.listWatchlistRaw(userId));
    }
}
