package com.koto.membre;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembreRepository extends JpaRepository<Membre, UUID> {

    List<Membre> findByGroupeIdOrderByOrdreReceptionAsc(UUID groupeId);

    List<Membre> findByGroupeIdAndStatutOrderByOrdreReceptionAsc(UUID groupeId, StatutMembre statut);

    Optional<Membre> findByGroupeIdAndUserId(UUID groupeId, UUID userId);

    boolean existsByGroupeIdAndUserId(UUID groupeId, UUID userId);

    int countByGroupeId(UUID groupeId);

    int countByGroupeIdAndStatut(UUID groupeId, StatutMembre statut);

    List<Membre> findByGroupeIdAndStatutOrderByCreeLe(UUID groupeId, StatutMembre statut);

    @Query("SELECT COALESCE(MAX(m.ordreReception), 0) FROM Membre m WHERE m.groupe.id = :groupeId")
    int findMaxOrdreReceptionByGroupeId(@Param("groupeId") UUID groupeId);
}