package com.koto.cycle;

import com.koto.cycle.dto.CycleResponse;
import com.koto.shared.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/groupes/{groupeId}/cycles")
@RequiredArgsConstructor
public class CycleController {

    private final CycleService cycleService;

    @PostMapping
    public ResponseEntity<ApiResponse<CycleResponse>> demarrerCycle(
            @PathVariable UUID groupeId) {
        CycleResponse response = cycleService.demarrerCycle(groupeId);
        return ResponseEntity.ok(ApiResponse.success("Cycle démarré", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CycleResponse>>> getCycles(
            @PathVariable UUID groupeId) {
        return ResponseEntity.ok(ApiResponse.success(cycleService.getCycles(groupeId)));
    }

    @GetMapping("/actif")
    public ResponseEntity<ApiResponse<CycleResponse>> getCycleActif(
            @PathVariable UUID groupeId) {
        return ResponseEntity.ok(ApiResponse.success(cycleService.getCycleActif(groupeId)));
    }
}