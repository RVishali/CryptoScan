package com.example.demo;

import com.example.demo.repo.AppUserRepository;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Test
    void register_whenEmailExists_throws() {
        AppUserRepository repo = mock(AppUserRepository.class);
        when(repo.existsByEmail(anyString())).thenReturn(true);

        UserService service = new UserService(repo, new BCryptPasswordEncoder());

        assertThrows(IllegalArgumentException.class,
                () -> service.register("a@b.com", "userA", "User A", "password123"));
    }
}
