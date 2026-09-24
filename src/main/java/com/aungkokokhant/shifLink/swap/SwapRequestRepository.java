package com.aungkokokhant.shifLink.swap;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SwapRequestRepository
        extends JpaRepository<SwapRequest, Long> {

    List<SwapRequest> findAllByOrderByRequestedAtDesc();

    boolean existsByShift_IdAndStatus(
            Long shiftId,
            SwapRequestStatus status
    );

    List<SwapRequest> findByRequesterEmployee_EmployeeCodeOrderByRequestedAtDesc(
            String employeeCode
    );
}