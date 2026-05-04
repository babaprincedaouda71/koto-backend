package com.koto.paiement;

import com.koto.paiement.dto.ConfirmerPaiementRequest;
import com.koto.paiement.dto.PaiementResponse;
import com.koto.shared.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PaiementController {

    private final PaiementService paiementService;

    @GetMapping("/api/groupes/{groupeId}/cycles/actif/impayes")
    public ResponseEntity<ApiResponse<List<PaiementResponse>>> getImpayes(
            @PathVariable UUID groupeId) {
        return ResponseEntity.ok(ApiResponse.success(paiementService.getImpayes(groupeId)));
    }

    @GetMapping("/api/groupes/{groupeId}/cycles/actif/paiements")
    public ResponseEntity<ApiResponse<List<PaiementResponse>>> getPaiementsCycleActif(
            @PathVariable UUID groupeId) {
        return ResponseEntity.ok(ApiResponse.success(
                paiementService.getPaiementsCycleActif(groupeId)));
    }

    @PatchMapping("/api/paiements/{id}/confirmer")
    public ResponseEntity<ApiResponse<PaiementResponse>> confirmerPaiement(
            @PathVariable UUID id,
            @RequestBody(required = false) ConfirmerPaiementRequest request) {
        String note = request != null ? request.getNote() : null;
        PaiementResponse response = paiementService.confirmerPaiement(id, note);
        return ResponseEntity.ok(ApiResponse.success("Paiement confirmé", response));
    }

    @GetMapping("/api/membres/{membreId}/paiements")
    public ResponseEntity<ApiResponse<List<PaiementResponse>>> getPaiementsMembre(
            @PathVariable UUID membreId) {
        return ResponseEntity.ok(ApiResponse.success(
                paiementService.getPaiementsMembre(membreId)));
    }

    @PatchMapping("/api/paiements/{id}/declarer")
    public ResponseEntity<ApiResponse<PaiementResponse>> declarerPaiement(@PathVariable UUID id) {
        PaiementResponse response = paiementService.declarerPaiement(id);
        return ResponseEntity.ok(ApiResponse.success("Paiement déclaré", response));
    }

    @PatchMapping("/api/paiements/{id}/invalider")
    public ResponseEntity<ApiResponse<PaiementResponse>> invaliderPaiement(@PathVariable UUID id) {
        PaiementResponse response = paiementService.invaliderPaiement(id);
        return ResponseEntity.ok(ApiResponse.success("Paiement invalidé", response));
    }

    @PostMapping("/api/paiements/{id}/rappel")
    public ResponseEntity<ApiResponse<String>> envoyerRappel(@PathVariable UUID id) {
        String lien = paiementService.envoyerRappel(id);
        return ResponseEntity.ok(ApiResponse.success("Rappel enregistré", lien));
    }
}