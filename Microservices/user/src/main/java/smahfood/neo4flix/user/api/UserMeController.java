package smahfood.neo4flix.user.api;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import smahfood.neo4flix.user.api.dto.AuthDtos;
import smahfood.neo4flix.user.domain.UserNode;
import smahfood.neo4flix.user.repo.UserRepository;
import smahfood.neo4flix.user.security.UserHeaderFilter;

@RestController
public class UserMeController {

    private final UserRepository userRepository;

    public UserMeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    @Transactional(readOnly = true)
    public ResponseEntity<AuthDtos.UserResponse> me(HttpServletRequest request) {
        String userId = (String) request.getAttribute(UserHeaderFilter.ATTR_USER_ID);
        if (userId == null) return ResponseEntity.status(401).build();

        return userRepository.findById(userId)
                .map(u -> ResponseEntity.ok(new AuthDtos.UserResponse(u.getId(), u.getEmail(), u.getName())))
                .orElse(ResponseEntity.status(401).build());
    }

    public record UpdateProfileRequest(
            @NotBlank @Size(min = 2, max = 50) String name
    ) {
    }

    @PutMapping("/me")
    @Transactional
    public ResponseEntity<AuthDtos.UserResponse> update(
            HttpServletRequest request,
            @Valid @RequestBody UpdateProfileRequest body
    ) {
        String userId = (String) request.getAttribute(UserHeaderFilter.ATTR_USER_ID);
        if (userId == null) return ResponseEntity.status(401).build();

        UserNode user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();

        user.setName(body.name());
        userRepository.save(user);

        return ResponseEntity.ok(new AuthDtos.UserResponse(user.getId(), user.getEmail(), user.getName()));
    }
}
