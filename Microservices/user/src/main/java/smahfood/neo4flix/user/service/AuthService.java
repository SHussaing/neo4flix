package smahfood.neo4flix.user.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smahfood.neo4flix.user.api.dto.AuthDtos;
import smahfood.neo4flix.user.domain.UserNode;
import smahfood.neo4flix.user.repo.UserRepository;
import smahfood.neo4flix.user.security.EmailOtpService;
import smahfood.neo4flix.user.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final EmailOtpService emailOtpService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, JwtService jwtService, EmailOtpService emailOtpService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.emailOtpService = emailOtpService;
    }

    @Transactional
    public void register(AuthDtos.RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("DUPLICATE_EMAIL");
        }

        UserNode user = new UserNode(
                UUID.randomUUID().toString(),
                request.email().toLowerCase(),
                request.name(),
                passwordEncoder.encode(request.password()),
                List.of("USER"),
                true,
                null,
                Instant.now()
        );

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public AuthDtos.OtpChallengeResponse login(AuthDtos.LoginRequest request) {
        UserNode user = userRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("INVALID_CREDENTIALS"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("INVALID_CREDENTIALS");
        }

        EmailOtpService.CreatedChallenge c = emailOtpService.createChallengeWithOtp(user.getEmail());
        return new AuthDtos.OtpChallengeResponse(c.challengeId(), user.getEmail(), c.otp());
    }

    // Removed legacy TOTP-style verify method; verification is done via email+challengeId+otp.

    public AuthDtos.AuthResponse verifyOtp(String email, String otp, String challengeId) {
        UserNode user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("INVALID_CREDENTIALS"));

        if (!emailOtpService.verify(challengeId, email, otp)) {
            throw new IllegalArgumentException("INVALID_OTP");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRoles());
        return new AuthDtos.AuthResponse(
                token,
                new AuthDtos.UserResponse(user.getId(), user.getEmail(), user.getName()),
                false
        );
    }
}
