package com.koto.auth;

import com.koto.auth.dto.LoginRequest;
import com.koto.auth.dto.RegisterRequest;
import com.koto.auth.dto.UserResponse;
import com.koto.shared.ApiResponse;
import com.koto.user.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {
        var auth = authService.register(request);
        setJwtCookie(response, auth.getToken());
        return ResponseEntity.ok(ApiResponse.success("Compte créé avec succès", toUserResponse(auth)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        var auth = authService.login(request);
        setJwtCookie(response, auth.getToken());
        return ResponseEntity.ok(ApiResponse.success("Connexion réussie", toUserResponse(auth)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        clearJwtCookie(response);
        return ResponseEntity.ok(ApiResponse.success("Déconnecté", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(
                new UserResponse(user.getId(), user.getNom(), user.getPrenom(), user.getEmail(), user.getRole().name())
        ));
    }

    private void setJwtCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .path("/")
                .maxAge(86400)
                .sameSite("Lax")
                // .secure(true) — à activer quand HTTPS est en place
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearJwtCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private UserResponse toUserResponse(com.koto.auth.dto.AuthResponse auth) {
        return new UserResponse(auth.getId(), auth.getNom(), auth.getPrenom(), auth.getEmail(), auth.getRole());
    }
}
