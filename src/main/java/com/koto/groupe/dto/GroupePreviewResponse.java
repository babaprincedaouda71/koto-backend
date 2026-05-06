package com.koto.groupe.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class GroupePreviewResponse {
    private UUID groupeId;
    private String nom;
    private Integer montantCotisation;
    private String devise;
    private Integer nombreMembres;
    private Integer membresActifs;
    private LocalDate dateDebut;
    private String adminPrenom;
    private String adminNom;
}
