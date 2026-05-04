package com.koto.paiement.dto;

import com.koto.paiement.StatutPaiement;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
public class PaiementResponse {
    private UUID id;
    private UUID membreId;
    private String membreNom;
    private String membrePrenom;
    private String telephone;
    private Integer montant;
    private StatutPaiement statut;
    private LocalDate dateEcheance;
    private LocalDate datePaiement;
    private String note;
    private int nombreRappels;
}