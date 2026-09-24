package com.aungkokokhant.shifLink.swap;

import com.aungkokokhant.shifLink.swap.dto.CreateSwapRequest;
import com.aungkokokhant.shifLink.swap.dto.SwapRequestResponse;
import com.aungkokokhant.shifLink.swap.dto.UpdateSwapRequestStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/swap-requests")
public class SwapRequestController {
    private final SwapRequestService swapRequestService;
    public SwapRequestController(SwapRequestService swapRequestService) { this.swapRequestService = swapRequestService; }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<SwapRequestResponse> getAllRequests() { return swapRequestService.getAll(); }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public List<SwapRequestResponse> getMyRequests(Authentication authentication) { return swapRequestService.getMyRequests(authentication.getName()); }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<SwapRequestResponse> create(@Valid @RequestBody CreateSwapRequest request, Authentication authentication) { return ResponseEntity.status(HttpStatus.CREATED).body(swapRequestService.create(request, authentication.getName())); }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public SwapRequestResponse updateStatus(@PathVariable Long id, @Valid @RequestBody UpdateSwapRequestStatus request) { return swapRequestService.updateStatus(id, request); }
}
