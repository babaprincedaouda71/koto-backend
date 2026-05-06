package com.koto.membre;

import com.koto.groupe.dto.GroupePreviewResponse;
import com.koto.membre.dto.MembreResponse;
import com.koto.shared.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MembreController {

    private final MembreService membreService;

    @GetMapping("/api/rejoindre/{token}")
    public ResponseEntity<ApiResponse<GroupePreviewResponse>> previewGroupe(
            @PathVariable UUID token) {
        return ResponseEntity.ok(ApiResponse.success(membreService.previewGroupe(token)));
    }

    @PostMapping("/api/rejoindre/{token}")
    public ResponseEntity<ApiResponse<MembreResponse>> demanderRejoindre(
            @PathVariable UUID token) {
        MembreResponse response = membreService.demanderRejoindre(token);
        return ResponseEntity.ok(ApiResponse.success("Demande envoyée, en attente de validation", response));
    }

    @GetMapping("/api/groupes/{groupeId}/membres")
    public ResponseEntity<ApiResponse<List<MembreResponse>>> getMembres(
            @PathVariable UUID groupeId) {
        return ResponseEntity.ok(ApiResponse.success(membreService.getMembres(groupeId)));
    }

    @GetMapping("/api/groupes/{groupeId}/membres/en-attente")
    public ResponseEntity<ApiResponse<List<MembreResponse>>> getDemandesEnAttente(
            @PathVariable UUID groupeId) {
        return ResponseEntity.ok(ApiResponse.success(membreService.getDemandesEnAttente(groupeId)));
    }

    @PostMapping("/api/groupes/{groupeId}/membres/{membreId}/approuver")
    public ResponseEntity<ApiResponse<MembreResponse>> approuver(
            @PathVariable UUID groupeId,
            @PathVariable UUID membreId) {
        MembreResponse response = membreService.approuverMembre(groupeId, membreId);
        return ResponseEntity.ok(ApiResponse.success("Membre approuvé", response));
    }

    @PostMapping("/api/groupes/{groupeId}/membres/{membreId}/rejeter")
    public ResponseEntity<ApiResponse<Void>> rejeter(
            @PathVariable UUID groupeId,
            @PathVariable UUID membreId) {
        membreService.rejeterMembre(groupeId, membreId);
        return ResponseEntity.ok(ApiResponse.success("Demande rejetée", null));
    }

    @PatchMapping("/api/groupes/{groupeId}/membres/{membreId}/ordre")
    public ResponseEntity<ApiResponse<MembreResponse>> modifierOrdre(
            @PathVariable UUID groupeId,
            @PathVariable UUID membreId,
            @RequestParam Integer nouvelOrdre) {
        MembreResponse response = membreService.modifierOrdre(groupeId, membreId, nouvelOrdre);
        return ResponseEntity.ok(ApiResponse.success("Ordre modifié", response));
    }
}
