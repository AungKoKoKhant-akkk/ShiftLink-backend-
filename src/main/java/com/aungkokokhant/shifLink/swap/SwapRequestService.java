package com.aungkokokhant.shifLink.swap;

import com.aungkokokhant.shifLink.employee.Employee;
import com.aungkokokhant.shifLink.employee.EmployeeService;
import com.aungkokokhant.shifLink.employee.EmployeeStatus;
import com.aungkokokhant.shifLink.shift.Shift;
import com.aungkokokhant.shifLink.shift.ShiftRepository;
import com.aungkokokhant.shifLink.shift.ShiftService;
import com.aungkokokhant.shifLink.swap.dto.CreateSwapRequest;
import com.aungkokokhant.shifLink.swap.dto.SwapRequestResponse;
import com.aungkokokhant.shifLink.swap.dto.UpdateSwapRequestStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SwapRequestService {

    private final SwapRequestRepository swapRequestRepository;
    private final ShiftRepository shiftRepository;
    private final EmployeeService employeeService;
    private final ShiftService shiftService;

    @Transactional(readOnly = true)
    public List<SwapRequestResponse> getAll() {
        return swapRequestRepository.findAllByOrderByRequestedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public SwapRequestResponse create(CreateSwapRequest request) {
        Shift shift = shiftRepository.findById(request.shiftId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Shift not found.")
                );

        boolean alreadyPending =
                swapRequestRepository.existsByShift_IdAndStatus(
                        shift.getId(),
                        SwapRequestStatus.PENDING
                );

        if (alreadyPending) {
            throw new IllegalArgumentException(
                    "A pending swap request already exists for this shift."
            );
        }

        SwapRequest swapRequest = new SwapRequest();
        swapRequest.setShift(shift);
        swapRequest.setRequesterEmployee(shift.getEmployee());
        swapRequest.setReason(request.reason().trim());

        return toResponse(swapRequestRepository.save(swapRequest));
    }

    public SwapRequestResponse updateStatus(
            Long id,
            UpdateSwapRequestStatus request
    ) {
        SwapRequest swapRequest = swapRequestRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Swap request not found.")
                );

        if (swapRequest.getStatus() != SwapRequestStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Only pending swap requests can be updated."
            );
        }

        if (request.status() == SwapRequestStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Status must be APPROVED or REJECTED."
            );
        }

        if (request.status() == SwapRequestStatus.APPROVED) {
            approveSwapRequest(swapRequest, request.replacementEmployeeId());
        }

        swapRequest.setStatus(request.status());

        return toResponse(swapRequest);
    }

    private void approveSwapRequest(
            SwapRequest swapRequest,
            Long replacementEmployeeId
    ) {
        if (replacementEmployeeId == null) {
            throw new IllegalArgumentException(
                    "A replacement employee is required for approval."
            );
        }

        Employee replacementEmployee =
                employeeService.findById(replacementEmployeeId);

        if (replacementEmployee.getStatus() != EmployeeStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Replacement employee must be active."
            );
        }

        Shift shift = swapRequest.getShift();

        if (shift.getEmployee().getId().equals(replacementEmployee.getId())) {
            throw new IllegalArgumentException(
                    "Replacement employee must be different from the current employee."
            );
        }

        // Student replacement employee: validates rolling 7-day 28-hour limit.
        shiftService.validateEmployeeCanTakeShift(
                replacementEmployee,
                shift
        );

        shift.setEmployee(replacementEmployee);
        swapRequest.setReplacementEmployee(replacementEmployee);
    }

    private SwapRequestResponse toResponse(SwapRequest request) {
        Shift shift = request.getShift();

        Long replacementEmployeeId =
                request.getReplacementEmployee() != null
                        ? request.getReplacementEmployee().getId()
                        : null;

        String replacementEmployeeName =
                request.getReplacementEmployee() != null
                        ? request.getReplacementEmployee().getName()
                        : null;

        String requesterName = request.getRequesterEmployee() != null
                ? request.getRequesterEmployee().getName()
                : shift.getEmployee().getName();

        return new SwapRequestResponse(
                request.getId(),
                shift.getId(),
                requesterName,
                replacementEmployeeId,
                replacementEmployeeName,
                shift.getShiftDate(),
                shift.getStartTime(),
                shift.getEndTime(),
                request.getReason(),
                request.getStatus(),
                request.getRequestedAt()
        );
    }
}