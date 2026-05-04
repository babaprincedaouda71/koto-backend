package com.koto.groupe.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class GroupeRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotNull(message = "Le montant est obligatoire")
    @Min(value = 1, message = "Le montant doit être positif")
    private Integer montantCotisation;

    @NotBlank(message = "La devise est obligatoire")
    private String devise;

    @NotNull(message = "Le nombre de membres est obligatoire")
    @Min(value = 2, message = "Un groupe doit avoir au moins 2 membres")
    private Integer nombreMembres;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;
}