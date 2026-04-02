package smahfood.neo4flix.user.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import smahfood.neo4flix.user.api.dto.AuthDtos;
import smahfood.neo4flix.user.service.AuthService;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<Void> register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthDtos.OtpChallengeResponse> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    public record VerifyLoginRequest(
            @jakarta.validation.constraints.Email @jakarta.validation.constraints.NotBlank String email,
            @jakarta.validation.constraints.NotBlank String challengeId,
            @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Pattern(regexp = "\\d{6}") String otp
    ) {
    }

    @PostMapping("/auth/login/verify")
    public ResponseEntity<AuthDtos.AuthResponse> verify(@Valid @RequestBody VerifyLoginRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request.email(), request.otp(), request.challengeId()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handle(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(java.util.Map.of(
                "error", ex.getMessage(),
                "message", ex.getMessage()
        ));
    }
}
