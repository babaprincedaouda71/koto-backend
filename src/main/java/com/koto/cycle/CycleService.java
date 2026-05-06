package com.koto.cycle;

import com.koto.cycle.dto.CycleResponse;
import com.koto.groupe.Groupe;
import com.koto.groupe.GroupeRepository;
import com.koto.membre.Membre;
import com.koto.membre.MembreRepository;
import com.koto.membre.StatutMembre;
import com.koto.shared.BusinessException;
import com.koto.paiement.Paiement;
import com.koto.paiement.PaiementRepository;
import com.koto.paiement.StatutPaiement;
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
public class CycleService {

    private final CycleRepository cycleRepository;
    private final GroupeRepository groupeRepository;
    private final MembreRepository membreRepository;
    private final PaiementRepository paiementRepository;
    private final UserRepository userRepository;

    public CycleResponse demarrerCycle(UUID groupeId) {
        User admin = getCurrentUser();
        Groupe groupe = groupeRepository.findById(groupeId)
                .orElseThrow(() -> new BusinessException("Groupe non trouvé"));

        if (!groupe.getAdmin().getId().equals(admin.getId())) {
            throw new BusinessException("Accès refusé — admin uniquement");
        }

        cycleRepository.findByGroupeIdAndStatut(groupeId, StatutCycle.EN_COURS)
                .ifPresent(c -> {
                    throw new BusinessException("Un cycle est déjà en cours");
                });

        List<Membre> membres = membreRepository
                .findByGroupeIdAndStatutOrderByOrdreReceptionAsc(groupeId, StatutMembre.ACTIF);

        if (membres.isEmpty()) {
            throw new BusinessException("Le groupe n'a pas encore de membres actifs");
        }

        int prochainNumero = cycleRepository.findMaxNumeroCycleByGroupeId(groupeId) + 1;
        int indexBeneficiaire = (prochainNumero - 1) % membres.size();
        Membre beneficiaire = membres.get(indexBeneficiaire);

        LocalDate dateDebut = cycleRepository
                .findTopByGroupeIdAndStatutOrderByNumeroCycleDesc(groupeId, StatutCycle.TERMINE)
                .map(Cycle::getDateFin)
                .orElse(groupe.getDateDebut());
        LocalDate dateFin = dateDebut.plusMonths(1);

        Cycle cycle = Cycle.builder()
                .groupe(groupe)
                .beneficiaire(beneficiaire)
                .numeroCycle(prochainNumero)
                .dateDebut(dateDebut)
                .dateFin(dateFin)
                .statut(StatutCycle.EN_COURS)
                .build();

        cycleRepository.save(cycle);

        // Génère un paiement EN_ATTENTE pour chaque membre sauf le bénéficiaire
        for (Membre membre : membres) {
            if (!membre.getId().equals(beneficiaire.getId())) {
                Paiement paiement = Paiement.builder()
                        .cycle(cycle)
                        .membre(membre)
                        .montant(groupe.getMontantCotisation())
                        .statut(StatutPaiement.EN_ATTENTE)
                        .dateEcheance(dateFin)
                        .build();
                paiementRepository.save(paiement);
            }
        }

        return toResponse(cycle, membres.size());
    }

    public List<CycleResponse> getCycles(UUID groupeId) {
        List<Membre> membres = membreRepository
                .findByGroupeIdAndStatutOrderByOrdreReceptionAsc(groupeId, StatutMembre.ACTIF);
        return cycleRepository.findByGroupeIdOrderByNumeroCycleAsc(groupeId)
                .stream()
                .map(c -> toResponse(c, membres.size()))
                .toList();
    }

    public CycleResponse getCycleActif(UUID groupeId) {
        List<Membre> membres = membreRepository
                .findByGroupeIdAndStatutOrderByOrdreReceptionAsc(groupeId, StatutMembre.ACTIF);
        Cycle cycle = cycleRepository.findByGroupeIdAndStatut(groupeId, StatutCycle.EN_COURS)
                .orElseThrow(() -> new BusinessException("Aucun cycle en cours"));
        return toResponse(cycle, membres.size());
    }

    public CycleResponse cloturerCycle(UUID groupeId, UUID cycleId) {
        User admin = getCurrentUser();
        Groupe groupe = groupeRepository.findById(groupeId)
                .orElseThrow(() -> new BusinessException("Groupe non trouvé"));

        if (!groupe.getAdmin().getId().equals(admin.getId())) {
            throw new BusinessException("Accès refusé — admin uniquement");
        }

        Cycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new BusinessException("Cycle non trouvé"));

        if (!cycle.getGroupe().getId().equals(groupeId)) {
            throw new BusinessException("Accès refusé");
        }
        if (cycle.getStatut() != StatutCycle.EN_COURS) {
            throw new BusinessException("Ce cycle n'est pas en cours");
        }

        cycle.setStatut(StatutCycle.TERMINE);
        cycleRepository.save(cycle);

        List<Membre> membres = membreRepository
                .findByGroupeIdAndStatutOrderByOrdreReceptionAsc(groupeId, StatutMembre.ACTIF);
        return toResponse(cycle, membres.size());
    }

    private CycleResponse toResponse(Cycle cycle, int totalMembres) {
        List<Paiement> paiements = paiementRepository.findByCycleId(cycle.getId());
        long nombrePaie = paiements.stream()
                .filter(p -> p.getStatut() == StatutPaiement.PAYE)
                .count();
        long nombreEnAttente = paiements.stream()
                .filter(p -> p.getStatut() == StatutPaiement.EN_ATTENTE)
                .count();

        return CycleResponse.builder()
                .id(cycle.getId())
                .numeroCycle(cycle.getNumeroCycle())
                .dateDebut(cycle.getDateDebut())
                .dateFin(cycle.getDateFin())
                .statut(cycle.getStatut())
                .beneficiaireId(cycle.getBeneficiaire().getId())
                .beneficiaireNom(cycle.getBeneficiaire().getUser().getNom())
                .beneficiairePrenom(cycle.getBeneficiaire().getUser().getPrenom())
                .totalMembres(totalMembres)
                .nombrePaie((int) nombrePaie)
                .nombreEnAttente((int) nombreEnAttente)
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}