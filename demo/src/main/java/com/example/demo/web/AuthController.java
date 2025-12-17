package com.example.demo.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.AppUser;
import com.example.demo.dto.auth.AuthResponse;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.service.PasswordResetService;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final PasswordResetService passwordResetService;
    private final PasswordEncoder encoder;

    public AuthController(UserService userService, PasswordResetService passwordResetService, PasswordEncoder encoder) {
        this.userService = userService; this.passwordResetService = passwordResetService; this.encoder = encoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest r) {
        try {
            AppUser u = userService.register(r.getEmail(), r.getUsername(), r.getFullName(), r.getPassword());
            return ResponseEntity.ok(new AuthResponse(u.getId(), u.getEmail(), u.getUsername(), u.getFullName()));
        } catch (IllegalArgumentException ex) {
            String code = ex.getMessage();
            if ("EMAIL_EXISTS".equals(code)) {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "Email already in use"));
            } else if ("USERNAME_EXISTS".equals(code)) {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "Username already in use"));
            } else {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "registration_failed"));
            }
        }
    }


@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody java.util.Map<String, String> body,
                               HttpServletRequest request) {
    String email = body.get("email");
    String password = body.get("password");

    AppUser u = userService.login(email, password);
    if (u == null) {
        return ResponseEntity.status(401).body(java.util.Map.of("error", "Invalid credentials"));
    }

    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(
                u.getEmail(),
                null,
                java.util.List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

    SecurityContextHolder.getContext().setAuthentication(auth);

    // create session and explicitly save SecurityContext into the session
    var session = request.getSession(true);
    session.setAttribute(
        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
        SecurityContextHolder.getContext()
    );

    return ResponseEntity.ok(new AuthResponse(
            u.getId(),
            u.getEmail(),
            u.getUsername(),
            u.getFullName()
    ));
}



    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgot(@RequestBody java.util.Map<String,String> body) {
        passwordResetService.createAndSendToken(body.get("email"));
        return ResponseEntity.ok(java.util.Map.of("status","ok"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> reset(@RequestBody java.util.Map<String,String> body) {
        boolean ok = passwordResetService.resetPassword(body.get("token"), body.get("newPassword"), encoder);
        if (!ok) return ResponseEntity.badRequest().body(java.util.Map.of("error","invalid_or_expired"));
        return ResponseEntity.ok(java.util.Map.of("status","ok"));
    }
}
