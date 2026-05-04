package com.koto.rappel;

import com.koto.paiement.Paiement;
import com.koto.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "rappels")
public class Rappel extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paiement_id", nullable = false)
    private Paiement paiement;

    @Column(nullable = false)
    private String canal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutRappel statut;
}