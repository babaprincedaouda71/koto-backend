package com.koto.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private UUID id;
    private String nom;
    private String prenom;
    private String email;
    private String role;
}