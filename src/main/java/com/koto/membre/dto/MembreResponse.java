package com.koto.membre.dto;

import com.koto.membre.StatutMembre;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class MembreResponse {
    private UUID id;
    private UUID userId;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private Integer ordreReception;
    private StatutMembre statut;
    private LocalDateTime rejointLe;
}