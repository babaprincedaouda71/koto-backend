package com.koto.cycle.dto;

import com.koto.cycle.StatutCycle;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CycleResponse {
    private UUID id;
    private Integer numeroCycle;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private StatutCycle statut;
    private UUID beneficiaireId;
    private String beneficiaireNom;
    private String beneficiairePrenom;
    private int totalMembres;
    private int nombrePaie;
    private int nombreEnAttente;
}