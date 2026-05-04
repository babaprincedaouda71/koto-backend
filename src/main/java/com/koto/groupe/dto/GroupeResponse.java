package com.koto.groupe.dto;

import com.koto.groupe.StatutGroupe;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
public class GroupeResponse {
    private UUID id;
    private String nom;
    private Integer montantCotisation;
    private String devise;
    private Integer nombreMembres;
    private LocalDate dateDebut;
    private StatutGroupe statut;
    private UUID tokenInvitation;
    private String adminNom;
    private String adminPrenom;
}