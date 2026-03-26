package smahfood.neo4flix.movie.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import smahfood.neo4flix.movie.api.dto.MovieDtos;
import smahfood.neo4flix.movie.domain.MovieNode;
import smahfood.neo4flix.movie.repo.MovieDetailsRepository;
import smahfood.neo4flix.movie.repo.MovieRepository;

@RestController
public class MovieController {

    private final MovieRepository movieRepository;
    private final MovieDetailsRepository movieDetailsRepository;

    public MovieController(MovieRepository movieRepository, MovieDetailsRepository movieDetailsRepository) {
        this.movieRepository = movieRepository;
        this.movieDetailsRepository = movieDetailsRepository;
    }

    @GetMapping("/movie")
    public List<MovieNode> all() {
        return movieRepository.findAllOrdered();
    }

    @GetMapping("/movie/search")
    public List<MovieNode> search(@RequestParam(name = "q") String q) {
        return movieRepository.search(q);
    }

    @GetMapping("/movie/{id}")
    public ResponseEntity<MovieNode> get(@PathVariable String id) {
        return movieRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/movie/{id}/details")
    public ResponseEntity<MovieDtos.MovieDetailsResponse> details(@PathVariable String id) {
        return movieDetailsRepository.findDetailsById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/movie")
    public ResponseEntity<MovieNode> create(@Valid @RequestBody MovieDtos.CreateMovieRequest body) {
        MovieNode movie = new MovieNode(
                UUID.randomUUID().toString(),
                body.title(),
                body.overview(),
                body.releaseYear(),
                body.genre(),
                body.releaseDate()
        );
        return ResponseEntity.ok(movieRepository.save(movie));
    }

    @PutMapping("/movie/{id}")
    public ResponseEntity<MovieNode> update(@PathVariable String id, @Valid @RequestBody MovieDtos.UpdateMovieRequest body) {
        MovieNode existing = movieRepository.findById(id).orElse(null);
        if (existing == null) return ResponseEntity.notFound().build();

        existing.setTitle(body.title());
        existing.setOverview(body.overview());
        existing.setReleaseYear(body.releaseYear());
        existing.setGenre(body.genre());
        existing.setReleaseDate(body.releaseDate());

        return ResponseEntity.ok(movieRepository.save(existing));
    }

    @DeleteMapping("/movie/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!movieRepository.existsById(id)) return ResponseEntity.notFound().build();
        movieRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
