package smahfood.neo4flix.movie.domain;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Movie")
public class MovieNode {

    @Id
    private String id;

    private String title;

    private String overview;

    private Integer releaseYear;

    private String genre;

    private LocalDate releaseDate;

    public MovieNode() {
    }

    public MovieNode(String id, String title, String overview, Integer releaseYear, String genre, LocalDate releaseDate) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.releaseYear = releaseYear;
        this.genre = genre;
        this.releaseDate = releaseDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
}

