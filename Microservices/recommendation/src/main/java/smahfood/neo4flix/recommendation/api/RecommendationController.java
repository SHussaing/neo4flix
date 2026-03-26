package smahfood.neo4flix.recommendation.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import smahfood.neo4flix.recommendation.api.dto.RecommendationDto;
import smahfood.neo4flix.recommendation.repo.RecommendationRepository;
import smahfood.neo4flix.recommendation.security.JwtAuthFilter;

@RestController
public class RecommendationController {

    private final RecommendationRepository repository;

    public RecommendationController(RecommendationRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/recommendation/me")
    public ResponseEntity<List<RecommendationDto>> me(
            HttpServletRequest request,
            @RequestParam(name = "limit", defaultValue = "20") int limit,
            @RequestParam(name = "genre", required = false) String genre,
            @RequestParam(name = "yearFrom", required = false) Integer yearFrom,
            @RequestParam(name = "yearTo", required = false) Integer yearTo
    ) {
        String userId = (String) request.getAttribute(JwtAuthFilter.ATTR_USER_ID);
        if (userId == null) return ResponseEntity.status(401).build();

        int safeLimit = Math.min(Math.max(limit, 1), 50);
        return ResponseEntity.ok(repository.recommendForUser(userId, safeLimit, genre, yearFrom, yearTo));
    }
}
