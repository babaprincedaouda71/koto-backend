package com.koto.paiement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PaiementRepository extends JpaRepository<Paiement, UUID> {

    List<Paiement> findByCycleId(UUID cycleId);

    List<Paiement> findByMembreId(UUID membreId);

    @Query("""
        SELECT p FROM Paiement p
        WHERE p.cycle.groupe.id = :groupeId
        AND p.cycle.statut = 'EN_COURS'
        AND p.statut != 'PAYE'
    """)
    List<Paiement> findImpayesByGroupeId(@Param("groupeId") UUID groupeId);

    @Query("""
        SELECT p FROM Paiement p
        WHERE p.cycle.groupe.id = :groupeId
        AND p.cycle.statut = 'EN_COURS'
    """)
    List<Paiement> findByCycleActif(@Param("groupeId") UUID groupeId);
}