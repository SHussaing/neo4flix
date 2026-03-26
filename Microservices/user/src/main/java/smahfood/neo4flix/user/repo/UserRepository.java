package smahfood.neo4flix.user.repo;
import java.util.Optional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import smahfood.neo4flix.user.domain.UserNode;
public interface UserRepository extends Neo4jRepository<UserNode, String> {
    Optional<UserNode> findByEmail(String email);
    boolean existsByEmail(String email);
}