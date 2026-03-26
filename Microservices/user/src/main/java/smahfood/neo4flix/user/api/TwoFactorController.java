package smahfood.neo4flix.user.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import smahfood.neo4flix.user.domain.UserNode;
import smahfood.neo4flix.user.repo.UserRepository;
import smahfood.neo4flix.user.security.TotpService;
import smahfood.neo4flix.user.security.UserHeaderFilter;

@RestController
public class TwoFactorController {

    private final UserRepository userRepository;
    private final TotpService totpService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public TwoFactorController(UserRepository userRepository, TotpService totpService) {
        this.userRepository = userRepository;
        this.totpService = totpService;
    }

    public record SetupResponse(String secret, String otpauthUri) {
    }

    @PostMapping("/auth/2fa/setup")
    @Transactional
    public ResponseEntity<?> setup(HttpServletRequest request) {
        String userId = (String) request.getAttribute(UserHeaderFilter.ATTR_USER_ID);
        if (userId == null) return ResponseEntity.status(401).build();

        UserNode user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();

        String secret = totpService.generateBase32Secret();
        user.setTwoFaSecret(secret);
        user.setTwoFaEnabled(false);
        userRepository.save(user);

        String uri = totpService.generateOtpAuthUri("Neo4flix", user.getEmail(), secret);
        return ResponseEntity.ok(new SetupResponse(secret, uri));
    }

    public record VerifyRequest(
            @NotBlank @Pattern(regexp = "\\d{6}") String otp
    ) {
    }

    @PostMapping("/auth/2fa/verify")
    @Transactional
    public ResponseEntity<?> verify(HttpServletRequest request, @Valid @RequestBody VerifyRequest body) {
        String userId = (String) request.getAttribute(UserHeaderFilter.ATTR_USER_ID);
        if (userId == null) return ResponseEntity.status(401).build();

        UserNode user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();
        if (user.getTwoFaSecret() == null || user.getTwoFaSecret().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "2FA_NOT_SETUP"));
        }

        if (!totpService.verifyCode(user.getTwoFaSecret(), body.otp())) {
            return ResponseEntity.badRequest().body(Map.of("error", "INVALID_OTP"));
        }

        user.setTwoFaEnabled(true);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    public record DisableRequest(@NotBlank String password) {
    }

    @PostMapping("/auth/2fa/disable")
    @Transactional
    public ResponseEntity<?> disable(HttpServletRequest request, @Valid @RequestBody DisableRequest body) {
        String userId = (String) request.getAttribute(UserHeaderFilter.ATTR_USER_ID);
        if (userId == null) return ResponseEntity.status(401).build();

        UserNode user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();

        if (!passwordEncoder.matches(body.password(), user.getPasswordHash())) {
            return ResponseEntity.badRequest().body(Map.of("error", "INVALID_CREDENTIALS"));
        }

        user.setTwoFaEnabled(false);
        user.setTwoFaSecret(null);
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }
}
