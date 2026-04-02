package smahfood.neo4flix.user.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Legacy endpoints from the earlier TOTP-based 2FA implementation.
 *
 * Email OTP is now used instead, so these routes are intentionally disabled.
 */
@RestController
public class TwoFactorController {

    @PostMapping("/auth/2fa/setup")
    public ResponseEntity<?> setup() {
        return ResponseEntity.status(410).body(Map.of("error", "TOTP_2FA_REMOVED"));
    }

    @PostMapping("/auth/2fa/verify")
    public ResponseEntity<?> verify() {
        return ResponseEntity.status(410).body(Map.of("error", "TOTP_2FA_REMOVED"));
    }

    @PostMapping("/auth/2fa/disable")
    public ResponseEntity<?> disable() {
        return ResponseEntity.status(410).body(Map.of("error", "TOTP_2FA_REMOVED"));
    }
}
