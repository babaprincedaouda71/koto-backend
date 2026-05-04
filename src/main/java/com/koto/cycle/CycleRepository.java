package com.koto.cycle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CycleRepository extends JpaRepository<Cycle, UUID> {

    List<Cycle> findByGroupeIdOrderByNumeroCycleAsc(UUID groupeId);

    Optional<Cycle> findByGroupeIdAndStatut(UUID groupeId, StatutCycle statut);

    @Query("SELECT COALESCE(MAX(c.numeroCycle), 0) FROM Cycle c WHERE c.groupe.id = :groupeId")
    int findMaxNumeroCycleByGroupeId(@Param("groupeId") UUID groupeId);
}