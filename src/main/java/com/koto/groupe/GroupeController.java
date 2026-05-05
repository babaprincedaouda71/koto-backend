package com.koto.groupe;

import com.koto.groupe.dto.GroupeRequest;
import com.koto.groupe.dto.GroupeResponse;
import com.koto.shared.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/groupes")
@RequiredArgsConstructor
public class GroupeController {

    private final GroupeService groupeService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @PostMapping
    public ResponseEntity<ApiResponse<GroupeResponse>> creerGroupe(
            @Valid @RequestBody GroupeRequest request) {
        GroupeResponse response = groupeService.creerGroupe(request);
        return ResponseEntity.ok(ApiResponse.success("Groupe créé avec succès", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GroupeResponse>>> getMesGroupes() {
        return ResponseEntity.ok(ApiResponse.success(groupeService.getMesGroupes()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GroupeResponse>> getGroupe(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(groupeService.getGroupe(id)));
    }

    @GetMapping("/{id}/invite")
    public ResponseEntity<ApiResponse<String>> genererInvitation(@PathVariable UUID id) {
        UUID token = groupeService.genererLienInvitation(id);
        String lien = frontendUrl + "/rejoindre/" + token;
        return ResponseEntity.ok(ApiResponse.success("Lien généré", lien));
    }
}