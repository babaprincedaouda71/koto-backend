package com.koto.groupe;

import com.koto.groupe.dto.GroupeRequest;
import com.koto.groupe.dto.GroupeResponse;
import com.koto.user.User;
import com.koto.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupeService {

    private final GroupeRepository groupeRepository;
    private final UserRepository userRepository;

    public GroupeResponse creerGroupe(GroupeRequest request) {
        User admin = getCurrentUser();

        Groupe groupe = Groupe.builder()
                .admin(admin)
                .nom(request.getNom())
                .montantCotisation(request.getMontantCotisation())
                .devise(request.getDevise())
                .nombreMembres(request.getNombreMembres())
                .dateDebut(request.getDateDebut())
                .statut(StatutGroupe.ACTIF)
                .tokenInvitation(UUID.randomUUID())
                .build();

        groupeRepository.save(groupe);
        return toResponse(groupe);
    }

    public List<GroupeResponse> getMesGroupes() {
        User user = getCurrentUser();
        return groupeRepository.findByAdminId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public GroupeResponse getGroupe(UUID id) {
        Groupe groupe = groupeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Groupe non trouvé"));
        return toResponse(groupe);
    }

    public UUID genererLienInvitation(UUID groupeId) {
        Groupe groupe = groupeRepository.findById(groupeId)
                .orElseThrow(() -> new RuntimeException("Groupe non trouvé"));

        User admin = getCurrentUser();
        if (!groupe.getAdmin().getId().equals(admin.getId())) {
            throw new RuntimeException("Accès refusé");
        }

        groupe.setTokenInvitation(UUID.randomUUID());
        groupeRepository.save(groupe);
        return groupe.getTokenInvitation();
    }

    private GroupeResponse toResponse(Groupe groupe) {
        return GroupeResponse.builder()
                .id(groupe.getId())
                .nom(groupe.getNom())
                .montantCotisation(groupe.getMontantCotisation())
                .devise(groupe.getDevise())
                .nombreMembres(groupe.getNombreMembres())
                .dateDebut(groupe.getDateDebut())
                .statut(groupe.getStatut())
                .tokenInvitation(groupe.getTokenInvitation())
                .adminNom(groupe.getAdmin().getNom())
                .adminPrenom(groupe.getAdmin().getPrenom())
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}