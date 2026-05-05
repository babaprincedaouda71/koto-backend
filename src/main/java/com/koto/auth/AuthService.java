package com.koto.auth;

import com.koto.auth.dto.AuthResponse;
import com.koto.auth.dto.LoginRequest;
import com.koto.auth.dto.RegisterRequest;
import com.koto.shared.BusinessException;
import com.koto.user.Role;
import com.koto.user.User;
import com.koto.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final LoginAttemptService loginAttemptService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Cet email est déjà utilisé");
        }
        if (userRepository.existsByTelephone(request.getTelephone())) {
            throw new BusinessException("Ce numéro de téléphone est déjà utilisé");
        }

        User user = User.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .telephone(request.getTelephone())
                .email(request.getEmail())
                .motDePasseHash(passwordEncoder.encode(request.getMotDePasse()))
                .role(Role.MEMBRE)
                .build();

        userRepository.save(user);
        String token = jwtService.generateToken(user);

        return new AuthResponse(token, user.getId(), user.getNom(), user.getPrenom(), user.getEmail(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        if (loginAttemptService.isBlocked(request.getEmail())) {
            throw new BusinessException("Trop de tentatives. Réessayez dans 15 minutes.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse())
            );
        } catch (BadCredentialsException e) {
            loginAttemptService.recordFailure(request.getEmail());
            throw e;
        }

        loginAttemptService.reset(request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getNom(), user.getPrenom(), user.getEmail(), user.getRole().name());
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow();
    }
}
