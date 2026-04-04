package com.karandashov.service;

import com.karandashov.dto.UniversalResponse;
import com.karandashov.dto.auth.RegisterUserRequest;
import com.karandashov.dto.auth.RegisterUserResponse;
import com.karandashov.entity.UserEntity;
import com.karandashov.exceptions.ConflictException;
import com.karandashov.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UniversalResponse<RegisterUserResponse> register(RegisterUserRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }

        UserEntity userEntity = UserEntity.builder()
                .id(UUID.randomUUID())
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .createdAt(Instant.now())
                .build();
        userEntity = userRepository.save(userEntity);

        RegisterUserResponse response = new RegisterUserResponse(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getCreatedAt()
        );
        return new UniversalResponse<>(response);
    }

    @Override
    @NullMarked
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        return User.withUsername(userEntity.getEmail())
                .password(userEntity.getPasswordHash())
                .authorities("USER")
                .build();
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
