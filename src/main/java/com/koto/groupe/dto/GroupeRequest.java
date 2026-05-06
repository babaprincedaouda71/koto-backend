package com.koto.groupe.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class GroupeRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;

    @NotNull(message = "Le montant est obligatoire")
    @Min(value = 1, message = "Le montant doit être positif")
    @Max(value = 100_000_000, message = "Le montant ne peut pas dépasser 100 000 000")
    private Integer montantCotisation;

    @NotBlank(message = "La devise est obligatoire")
    private String devise;

    @NotNull(message = "Le nombre de membres est obligatoire")
    @Min(value = 2, message = "Un groupe doit avoir au moins 2 membres")
    @Max(value = 500, message = "Un groupe ne peut pas dépasser 500 membres")
    private Integer nombreMembres;

    @NotNull(message = "La date de début est obligatoire")
    @FutureOrPresent(message = "La date de début ne peut pas être dans le passé")
    private LocalDate dateDebut;
}