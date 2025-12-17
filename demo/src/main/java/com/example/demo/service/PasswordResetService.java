package com.example.demo.service;

import com.example.demo.domain.AppUser;
import com.example.demo.domain.PasswordResetToken;
import com.example.demo.repo.PasswordResetTokenRepository;
import com.example.demo.repo.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class PasswordResetService {
    private final PasswordResetTokenRepository tokenRepo;
    private final AppUserRepository userRepo;
    private final EmailService emailService;

    @Value("${app.frontend.reset-url}")
    private String frontendResetUrl;

    public PasswordResetService(PasswordResetTokenRepository tokenRepo, AppUserRepository userRepo, EmailService emailService) {
        this.tokenRepo = tokenRepo;
        this.userRepo = userRepo;
        this.emailService = emailService;
    }

    public void createAndSendToken(String email) {
        Optional<AppUser> userOpt = userRepo.findByEmail(email.toLowerCase());
        if (userOpt.isEmpty()) {
            // Do not reveal whether email exists
            return;
        }
        AppUser user = userOpt.get();
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS));
        tokenRepo.save(token);
        String link = frontendResetUrl + "?token=" + token.getToken();
        String body = "Hello " + (user.getFullName() != null ? user.getFullName() : user.getUsername()) + ",\n\n" +
                "You requested a password reset. Click the link below to set a new password (valid 1 hour):\n\n" + link + "\n\n" +
                "If you did not request this, ignore this email.\n\nRegards,\nRisk Analyzer Team";
        emailService.sendSimpleMessage(user.getEmail(), "Password reset request", body);
    }

    public boolean resetPassword(String tokenStr, String newPassword, PasswordEncoder encoder) {
        Optional<PasswordResetToken> t = tokenRepo.findByToken(tokenStr);
        if (t.isEmpty()) return false;
        PasswordResetToken token = t.get();
        if (token.getExpiresAt().isBefore(Instant.now())) {
            tokenRepo.delete(token);
            return false;
        }
        AppUser user = token.getUser();
        user.setPassword(encoder.encode(newPassword));
        userRepo.save(user);
        tokenRepo.delete(token);
        return true;
    }
}
