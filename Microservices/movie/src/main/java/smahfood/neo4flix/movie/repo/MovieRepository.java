package smahfood.neo4flix.movie.repo;
import java.util.List;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import smahfood.neo4flix.movie.domain.MovieNode;
public interface MovieRepository extends Neo4jRepository<MovieNode, String> {
    @Query("MATCH (m:Movie) RETURN m ORDER BY m.title")
    List<MovieNode> findAllOrdered();
    @Query("MATCH (m:Movie) WHERE toLower(m.title) CONTAINS toLower($q) OR toLower(m.genre) CONTAINS toLower($q) OR toString(m.releaseYear) CONTAINS $q RETURN m ORDER BY m.title")
    List<MovieNode> search(String q);
}