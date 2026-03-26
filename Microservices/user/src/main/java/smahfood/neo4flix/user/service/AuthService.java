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
import smahfood.neo4flix.user.security.JwtService;
import smahfood.neo4flix.user.security.TotpService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final TotpService totpService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, JwtService jwtService, TotpService totpService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.totpService = totpService;
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
                false,
                null,
                Instant.now()
        );

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        UserNode user = userRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("INVALID_CREDENTIALS"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("INVALID_CREDENTIALS");
        }

        if (user.isTwoFaEnabled()) {
            if (request.otp() == null || !totpService.verifyCode(user.getTwoFaSecret(), request.otp())) {
                throw new IllegalArgumentException("OTP_REQUIRED");
            }
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRoles());

        return new AuthDtos.AuthResponse(
                token,
                new AuthDtos.UserResponse(user.getId(), user.getEmail(), user.getName()),
                false
        );
    }
}
