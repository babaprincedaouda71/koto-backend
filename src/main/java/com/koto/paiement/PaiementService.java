package com.koto.paiement;

import com.koto.membre.Membre;
import com.koto.membre.MembreRepository;
import com.koto.paiement.dto.PaiementResponse;
import com.koto.rappel.Rappel;
import com.koto.rappel.RappelRepository;
import com.koto.rappel.StatutRappel;
import com.koto.user.User;
import com.koto.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final MembreRepository membreRepository;
    private final UserRepository userRepository;
    private final RappelRepository rappelRepository;

    public List<PaiementResponse> getImpayes(UUID groupeId) {
        return paiementRepository
                .findImpayesByGroupeId(groupeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PaiementResponse confirmerPaiement(UUID paiementId, String note) {
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));

        if (paiement.getStatut() == StatutPaiement.PAYE) {
            throw new RuntimeException("Ce paiement est déjà confirmé");
        }

        paiement.setStatut(StatutPaiement.PAYE);
        paiement.setDatePaiement(LocalDate.now());
        paiement.setNote(note);
        paiementRepository.save(paiement);

        return toResponse(paiement);
    }

    public List<PaiementResponse> getPaiementsMembre(UUID membreId) {
        return paiementRepository.findByMembreId(membreId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public String envoyerRappel(UUID paiementId) {
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));

        if (paiement.getStatut() == StatutPaiement.PAYE) {
            throw new RuntimeException("Ce paiement est déjà réglé");
        }

        Membre membre = paiement.getMembre();
        User user = membre.getUser();
        String telephone = user.getTelephone();
        String nom = user.getPrenom() + " " + user.getNom();
        int montant = paiement.getMontant();
        String devise = paiement.getCycle().getGroupe().getDevise();
        String nomGroupe = paiement.getCycle().getGroupe().getNom();

        String message = String.format(
            "Bonjour %s, vous avez un paiement en attente de %d %s pour la tontine \"%s\". Merci de régulariser votre situation.",
            nom, montant, devise, nomGroupe
        );

        String lienWhatsApp = "https://wa.me/" +
                telephone.replaceAll("[^0-9]", "") +
                "?text=" +
                message.replace(" ", "%20");

        Rappel rappel = Rappel.builder()
                .paiement(paiement)
                .canal("WHATSAPP")
                .statut(StatutRappel.ENVOYE)
                .build();
        rappelRepository.save(rappel);

        return lienWhatsApp;
    }

    public List<PaiementResponse> getPaiementsCycleActif(UUID groupeId) {
        return paiementRepository.findByCycleActif(groupeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private PaiementResponse toResponse(Paiement paiement) {
        long nombreRappels = rappelRepository.countByPaiementId(paiement.getId());
        return PaiementResponse.builder()
                .id(paiement.getId())
                .membreId(paiement.getMembre().getId())
                .membreNom(paiement.getMembre().getUser().getNom())
                .membrePrenom(paiement.getMembre().getUser().getPrenom())
                .telephone(paiement.getMembre().getUser().getTelephone())
                .montant(paiement.getMontant())
                .statut(paiement.getStatut())
                .dateEcheance(paiement.getDateEcheance())
                .datePaiement(paiement.getDatePaiement())
                .note(paiement.getNote())
                .nombreRappels((int) nombreRappels)
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}