package com.koto.membre;

import com.koto.groupe.Groupe;
import com.koto.groupe.GroupeRepository;
import com.koto.groupe.dto.GroupePreviewResponse;
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

    public GroupePreviewResponse previewGroupe(UUID tokenInvitation) {
        Groupe groupe = groupeRepository.findByTokenInvitation(tokenInvitation)
                .orElseThrow(() -> new BusinessException("Lien d'invitation invalide ou expiré"));

        int membresActifs = membreRepository.countByGroupeIdAndStatut(groupe.getId(), StatutMembre.ACTIF);

        return GroupePreviewResponse.builder()
                .groupeId(groupe.getId())
                .nom(groupe.getNom())
                .montantCotisation(groupe.getMontantCotisation())
                .devise(groupe.getDevise())
                .nombreMembres(groupe.getNombreMembres())
                .membresActifs(membresActifs)
                .dateDebut(groupe.getDateDebut())
                .adminPrenom(groupe.getAdmin().getPrenom())
                .adminNom(groupe.getAdmin().getNom())
                .build();
    }

    public MembreResponse demanderRejoindre(UUID tokenInvitation) {
        Groupe groupe = groupeRepository.findByTokenInvitation(tokenInvitation)
                .orElseThrow(() -> new BusinessException("Lien d'invitation invalide ou expiré"));

        User user = getCurrentUser();

        if (groupe.getAdmin().getId().equals(user.getId())) {
            throw new BusinessException("L'administrateur ne peut pas rejoindre son propre groupe comme membre");
        }

        if (membreRepository.existsByGroupeIdAndUserId(groupe.getId(), user.getId())) {
            throw new BusinessException("Vous avez déjà rejoint ou demandé à rejoindre ce groupe");
        }

        int membresActifs = membreRepository.countByGroupeIdAndStatut(groupe.getId(), StatutMembre.ACTIF);
        if (membresActifs >= groupe.getNombreMembres()) {
            throw new BusinessException("Ce groupe est complet");
        }

        Membre membre = Membre.builder()
                .groupe(groupe)
                .user(user)
                .ordreReception(null)
                .statut(StatutMembre.EN_ATTENTE)
                .build();

        try {
            membreRepository.save(membre);
            membreRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Vous avez déjà rejoint ou demandé à rejoindre ce groupe");
        }
        return toResponse(membre);
    }

    public MembreResponse approuverMembre(UUID groupeId, UUID membreId) {
        Groupe groupe = groupeRepository.findById(groupeId)
                .orElseThrow(() -> new BusinessException("Groupe non trouvé"));

        verifierAdmin(groupe);

        Membre membre = membreRepository.findById(membreId)
                .orElseThrow(() -> new BusinessException("Membre non trouvé"));

        if (!membre.getGroupe().getId().equals(groupeId)) {
            throw new BusinessException("Accès refusé");
        }
        if (membre.getStatut() != StatutMembre.EN_ATTENTE) {
            throw new BusinessException("Ce membre n'est pas en attente d'approbation");
        }

        int membresActifs = membreRepository.countByGroupeIdAndStatut(groupeId, StatutMembre.ACTIF);
        if (membresActifs >= groupe.getNombreMembres()) {
            throw new BusinessException("Le groupe est complet, impossible d'approuver");
        }

        int prochainOrdre = membreRepository.findMaxOrdreReceptionByGroupeId(groupeId) + 1;
        membre.setOrdreReception(prochainOrdre);
        membre.setStatut(StatutMembre.ACTIF);
        membreRepository.save(membre);
        return toResponse(membre);
    }

    public void rejeterMembre(UUID groupeId, UUID membreId) {
        Groupe groupe = groupeRepository.findById(groupeId)
                .orElseThrow(() -> new BusinessException("Groupe non trouvé"));

        verifierAdmin(groupe);

        Membre membre = membreRepository.findById(membreId)
                .orElseThrow(() -> new BusinessException("Membre non trouvé"));

        if (!membre.getGroupe().getId().equals(groupeId)) {
            throw new BusinessException("Accès refusé");
        }
        if (membre.getStatut() != StatutMembre.EN_ATTENTE) {
            throw new BusinessException("Ce membre n'est pas en attente d'approbation");
        }

        membreRepository.delete(membre);
    }

    public List<MembreResponse> getMembres(UUID groupeId) {
        return membreRepository
                .findByGroupeIdOrderByOrdreReceptionAsc(groupeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<MembreResponse> getDemandesEnAttente(UUID groupeId) {
        Groupe groupe = groupeRepository.findById(groupeId)
                .orElseThrow(() -> new BusinessException("Groupe non trouvé"));
        verifierAdmin(groupe);

        return membreRepository
                .findByGroupeIdAndStatutOrderByCreeLe(groupeId, StatutMembre.EN_ATTENTE)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public MembreResponse modifierOrdre(UUID groupeId, UUID membreId, Integer nouvelOrdre) {
        Groupe groupe = groupeRepository.findById(groupeId)
                .orElseThrow(() -> new BusinessException("Groupe non trouvé"));

        verifierAdmin(groupe);

        Membre membre = membreRepository.findById(membreId)
                .orElseThrow(() -> new BusinessException("Membre non trouvé"));

        if (!membre.getGroupe().getId().equals(groupeId)) {
            throw new BusinessException("Accès refusé");
        }

        membre.setOrdreReception(nouvelOrdre);
        membreRepository.save(membre);
        return toResponse(membre);
    }

    private void verifierAdmin(Groupe groupe) {
        User user = getCurrentUser();
        if (!groupe.getAdmin().getId().equals(user.getId())) {
            throw new BusinessException("Accès refusé");
        }
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
