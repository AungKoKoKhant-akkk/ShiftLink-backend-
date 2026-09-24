package com.aungkokokhant.shifLink.swap;

import com.aungkokokhant.shifLink.swap.dto.CreateSwapRequest;
import com.aungkokokhant.shifLink.swap.dto.SwapRequestResponse;
import com.aungkokokhant.shifLink.swap.dto.UpdateSwapRequestStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/swap-requests")
public class SwapRequestController {

    private final SwapRequestService swapRequestService;

    public SwapRequestController(SwapRequestService swapRequestService) {
        this.swapRequestService = swapRequestService;
    }

    @GetMapping
    public List<SwapRequestResponse> getAll() {
        return swapRequestService.getAll();
    }

    @PostMapping
    public ResponseEntity<SwapRequestResponse> create(
            @Valid @RequestBody CreateSwapRequest request
    ) {
        SwapRequestResponse response =
                swapRequestService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/{id}/status")
    public SwapRequestResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSwapRequestStatus request
    ) {
        return swapRequestService.updateStatus(id, request);
    }
}