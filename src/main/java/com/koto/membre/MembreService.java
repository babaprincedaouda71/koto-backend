package com.koto.membre;

import com.koto.groupe.Groupe;
import com.koto.groupe.GroupeRepository;
import com.koto.membre.dto.MembreResponse;
import com.koto.shared.BusinessException;
import com.koto.user.User;
import com.koto.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MembreService {

    private final MembreRepository membreRepository;
    private final GroupeRepository groupeRepository;
    private final UserRepository userRepository;

    public MembreResponse rejoindreGroupe(UUID tokenInvitation) {
        Groupe groupe = groupeRepository.findByTokenInvitation(tokenInvitation)
                .orElseThrow(() -> new BusinessException("Lien d'invitation invalide"));

        User user = getCurrentUser();

        if (membreRepository.existsByGroupeIdAndUserId(groupe.getId(), user.getId())) {
            throw new BusinessException("Vous êtes déjà membre de ce groupe");
        }

        int prochainOrdre = membreRepository
                .findMaxOrdreReceptionByGroupeId(groupe.getId()) + 1;

        Membre membre = Membre.builder()
                .groupe(groupe)
                .user(user)
                .ordreReception(prochainOrdre)
                .statut(StatutMembre.ACTIF)
                .build();

        try {
            membreRepository.save(membre);
            membreRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Vous êtes déjà membre de ce groupe");
        }
        return toResponse(membre);
    }

    public List<MembreResponse> getMembres(UUID groupeId) {
        return membreRepository
                .findByGroupeIdOrderByOrdreReceptionAsc(groupeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public MembreResponse modifierOrdre(UUID groupeId, UUID membreId, Integer nouvelOrdre) {
        User admin = getCurrentUser();
        Groupe groupe = groupeRepository.findById(groupeId)
                .orElseThrow(() -> new BusinessException("Accès refusé"));

        if (!groupe.getAdmin().getId().equals(admin.getId())) {
            throw new BusinessException("Accès refusé");
        }

        Membre membre = membreRepository.findById(membreId)
                .orElseThrow(() -> new BusinessException("Accès refusé"));

        if (!membre.getGroupe().getId().equals(groupeId)) {
            throw new BusinessException("Accès refusé");
        }

        membre.setOrdreReception(nouvelOrdre);
        membreRepository.save(membre);
        return toResponse(membre);
    }

    private MembreResponse toResponse(Membre membre) {
        return MembreResponse.builder()
                .id(membre.getId())
                .userId(membre.getUser().getId())
                .nom(membre.getUser().getNom())
                .prenom(membre.getUser().getPrenom())
                .telephone(membre.getUser().getTelephone())
                .email(membre.getUser().getEmail())
                .ordreReception(membre.getOrdreReception())
                .statut(membre.getStatut())
                .rejointLe(membre.getCreeLe())
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}