package com.koto.groupe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupeRepository extends JpaRepository<Groupe, UUID> {

    List<Groupe> findByAdminId(UUID adminId);

    Optional<Groupe> findByTokenInvitation(UUID token);

    @Query("""
        SELECT DISTINCT g FROM Groupe g
        LEFT JOIN g.admin a
        WHERE a.id = :userId
        OR g.id IN (
            SELECT m.groupe.id FROM Membre m WHERE m.user.id = :userId
        )
    """)
    List<Groupe> findAllByUserId(@Param("userId") UUID userId);
}