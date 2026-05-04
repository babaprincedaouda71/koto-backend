package com.koto.groupe;

import com.koto.shared.BaseEntity;
import com.koto.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "groupes")
public class Groupe extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @Column(nullable = false)
    private String nom;

    @Column(name = "montant_cotisation", nullable = false)
    private Integer montantCotisation;

    @Column(nullable = false)
    private String devise;

    @Column(name = "nombre_membres", nullable = false)
    private Integer nombreMembres;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutGroupe statut;

    @Column(name = "token_invitation")
    private UUID tokenInvitation;
}