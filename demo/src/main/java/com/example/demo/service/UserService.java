package com.example.demo.service;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.domain.AppUser;
import com.example.demo.repo.AppUserRepository;

@Service
public class UserService {
    private final AppUserRepository userRepo;
    private final PasswordEncoder encoder;

    public UserService(AppUserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    public AppUser register(String email, String username, String fullName, String rawPassword) {
        if (userRepo.existsByEmail(email.toLowerCase())) {
            throw new IllegalArgumentException("EMAIL_EXISTS");
        }
        if (userRepo.existsByUsername(username)) {
            throw new IllegalArgumentException("USERNAME_EXISTS");
        }
        AppUser u = new AppUser();
        u.setEmail(email.toLowerCase());
        u.setUsername(username);
        u.setFullName(fullName);
        u.setPassword(encoder.encode(rawPassword));
        return userRepo.save(u);
    }

    public AppUser login(String email, String rawPassword) {
    AppUser user = userRepo.findByEmail(email).orElse(null);
    if (user == null) return null;

    if (!encoder.matches(rawPassword, user.getPassword())) return null;

    return user;
}


    public Optional<AppUser> findByEmail(String email) { return userRepo.findByEmail(email); }
    public Optional<AppUser> findByUsername(String username) { return userRepo.findByUsername(username); }

    public AppUser getCurrentUser() {
    var ctx = SecurityContextHolder.getContext();
    if (ctx == null) return null;
    var auth = ctx.getAuthentication();
    if (auth == null) return null;

    Object principal = auth.getPrincipal();

    // if principal is already our AppUser (rare), return it
    if (principal instanceof AppUser) {
        return (AppUser) principal;
    }

    // if principal is a Spring UserDetails (common), use its username (which you store as email)
    if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
        String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        return userRepo.findByEmail(username).orElse(null);
    }

    // if principal is a String (you set email as principal), handle it
    if (principal instanceof String) {
        String maybeEmail = (String) principal;
        return userRepo.findByEmail(maybeEmail).orElse(null);
    }

    // fallback: try Authentication#getName() (may return username/email)
    String name = auth.getName();
    if (name != null && !name.isBlank()) {
        return userRepo.findByEmail(name).orElse(null);
    }

    return null;
}

}
