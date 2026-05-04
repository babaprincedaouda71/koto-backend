package com.koto.rappel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RappelRepository extends JpaRepository<Rappel, UUID> {
    long countByPaiementId(UUID paiementId);
}