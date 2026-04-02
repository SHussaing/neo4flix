package smahfood.neo4flix.user.security;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Simple email-OTP provider.
 *
 * In production you would back this with an email provider (SMTP / SendGrid / Mailgun),
 * and persist challenges in a datastore.
 *
 * For this project we keep it in-memory and (optionally) log the OTP to the service logs.
 */
@Service
public class EmailOtpService {

    private static final Logger log = LoggerFactory.getLogger(EmailOtpService.class);

    private static final SecureRandom rnd = new SecureRandom();

    public record Challenge(String email, String otp, Instant expiresAt) {
    }

    public record CreatedChallenge(String challengeId, String otp) {
    }

    private final Map<String, Challenge> challenges = new ConcurrentHashMap<>();

    private final Duration ttl;
    private final boolean logOtp;

    public EmailOtpService(
            @Value("${security.email-otp.ttl-seconds:300}") long ttlSeconds,
            @Value("${security.email-otp.log-otp:true}") boolean logOtp
    ) {
        this.ttl = Duration.ofSeconds(ttlSeconds);
        this.logOtp = logOtp;
    }

    public CreatedChallenge createChallengeWithOtp(String email) {
        String otp = String.format("%06d", rnd.nextInt(1_000_000));
        String challengeId = UUID.randomUUID().toString();
        challenges.put(challengeId, new Challenge(email, otp, Instant.now().plus(ttl)));

        // For now we log it (dev-friendly). You can wire real email later.
        if (logOtp) {
            log.info("EMAIL_OTP for {} challengeId={} otp={} (expires in {}s)", email, challengeId, otp, ttl.toSeconds());
        }

        return new CreatedChallenge(challengeId, otp);
    }

    public String createChallenge(String email) {
        return createChallengeWithOtp(email).challengeId();
    }

    public boolean verify(String challengeId, String email, String otp) {
        if (challengeId == null || email == null || otp == null) return false;

        Challenge c = challenges.get(challengeId);
        if (c == null) return false;
        if (!email.equalsIgnoreCase(c.email())) return false;
        if (Instant.now().isAfter(c.expiresAt())) {
            challenges.remove(challengeId);
            return false;
        }

        boolean ok = c.otp().equals(otp);
        if (ok) {
            challenges.remove(challengeId);
        }
        return ok;
    }

    public void clearExpired() {
        Instant now = Instant.now();
        challenges.entrySet().removeIf(e -> now.isAfter(e.getValue().expiresAt()));
    }
}
