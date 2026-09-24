package com.aungkokokhant.shifLink.shift;

import com.aungkokokhant.shifLink.shift.dto.ShiftRequest;
import com.aungkokokhant.shifLink.shift.dto.ShiftResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ShiftController {
    private final ShiftService shiftService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<ShiftResponse> findAll() { return shiftService.findAll(); }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public List<ShiftResponse> findMyShifts(Authentication authentication) { return shiftService.findMyShifts(authentication.getName()); }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ShiftResponse> create(@Valid @RequestBody ShiftRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(shiftService.create(request)); }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ShiftResponse update(@PathVariable("id") Long id, @Valid @RequestBody ShiftRequest request) { return shiftService.update(id, request); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) { shiftService.delete(id); return ResponseEntity.noContent().build(); }
}
