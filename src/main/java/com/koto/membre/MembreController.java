package com.koto.membre;

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

    @PostMapping("/api/rejoindre/{token}")
    public ResponseEntity<ApiResponse<MembreResponse>> rejoindre(
            @PathVariable UUID token) {
        MembreResponse response = membreService.rejoindreGroupe(token);
        return ResponseEntity.ok(ApiResponse.success("Vous avez rejoint le groupe", response));
    }

    @GetMapping("/api/groupes/{groupeId}/membres")
    public ResponseEntity<ApiResponse<List<MembreResponse>>> getMembres(
            @PathVariable UUID groupeId) {
        return ResponseEntity.ok(ApiResponse.success(membreService.getMembres(groupeId)));
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