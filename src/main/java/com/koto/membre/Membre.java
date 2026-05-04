package com.koto.membre;

import com.koto.groupe.Groupe;
import com.koto.shared.BaseEntity;
import com.koto.user.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "membres")
public class Membre extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_id", nullable = false)
    private Groupe groupe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "ordre_reception", nullable = false)
    private Integer ordreReception;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutMembre statut;
}