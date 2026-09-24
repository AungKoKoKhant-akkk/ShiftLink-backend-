package com.aungkokokhant.shifLink.shift;

import com.aungkokokhant.shifLink.employee.Employee;
import com.aungkokokhant.shifLink.employee.EmployeeService;
import com.aungkokokhant.shifLink.employee.EmployeeType;
import com.aungkokokhant.shifLink.shift.dto.ShiftRequest;
import com.aungkokokhant.shifLink.shift.dto.ShiftResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShiftService {

    private static final int STUDENT_HOUR_LIMIT_MINUTES = 28 * 60;

    private final ShiftRepository shiftRepository;
    private final EmployeeService employeeService;

    public List<ShiftResponse> findAll() {
        return shiftRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ShiftResponse create(ShiftRequest request) {
        Employee employee = employeeService.findById(request.employeeId());

        validateStudentHourLimit(employee, request, null);

        Shift shift = Shift.builder()
                .employee(employee)
                .shiftDate(request.shiftDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .breakMinutes(request.breakMinutes())
                .status(ShiftStatus.SCHEDULED)
                .build();

        return toResponse(shiftRepository.save(shift));
    }

    @Transactional
    public ShiftResponse update(Long id, ShiftRequest request) {
        Shift shift = findShiftById(id);
        Employee employee = employeeService.findById(request.employeeId());

        validateStudentHourLimit(employee, request, id);

        shift.setEmployee(employee);
        shift.setShiftDate(request.shiftDate());
        shift.setStartTime(request.startTime());
        shift.setEndTime(request.endTime());
        shift.setBreakMinutes(request.breakMinutes());

        return toResponse(shift);
    }

    @Transactional
    public void delete(Long id) {
        shiftRepository.delete(findShiftById(id));
    }

    private Shift findShiftById(Long id) {
        return shiftRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Shift not found: " + id)
                );
    }

    public void validateEmployeeCanTakeShift(
            Employee employee,
            Shift shift
    ) {
        ShiftRequest request = new ShiftRequest(
                employee.getId(),
                shift.getShiftDate(),
                shift.getStartTime(),
                shift.getEndTime(),
                shift.getBreakMinutes()
        );

        validateStudentHourLimit(employee, request, shift.getId());
    }

    private void validateStudentHourLimit(
            Employee employee,
            ShiftRequest request,
            Long excludedShiftId
    ) {
        if (employee.getType() != EmployeeType.STUDENT) {
            return;
        }

        LocalDate proposedDate = request.shiftDate();
        int proposedWorkingMinutes = ShiftCalculator.calculateWorkingMinutes(
                request.startTime(),
                request.endTime(),
                request.breakMinutes()
        );

        List<Shift> nearbyShifts = shiftRepository
                .findByEmployeeIdAndShiftDateBetween(
                        employee.getId(),
                        proposedDate.minusDays(6),
                        proposedDate.plusDays(6)
                )
                .stream()
                .filter(shift -> !shift.getId().equals(excludedShiftId))
                .toList();

        for (int offset = -6; offset <= 0; offset++) {
            LocalDate windowStart = proposedDate.plusDays(offset);
            LocalDate windowEnd = windowStart.plusDays(6);

            int totalWorkingMinutes = proposedWorkingMinutes;

            for (Shift shift : nearbyShifts) {
                boolean isInsideWindow = !shift.getShiftDate().isBefore(windowStart)
                        && !shift.getShiftDate().isAfter(windowEnd);

                if (isInsideWindow) {
                    totalWorkingMinutes += ShiftCalculator.calculateWorkingMinutes(
                            shift.getStartTime(),
                            shift.getEndTime(),
                            shift.getBreakMinutes()
                    );
                }
            }

            if (totalWorkingMinutes > STUDENT_HOUR_LIMIT_MINUTES) {
                double totalHours = totalWorkingMinutes / 60.0;

                throw new IllegalArgumentException(
                        "Student work hours would become "
                                + totalHours
                                + " hours in a rolling 7-day period. "
                                + "The limit is 28 hours."
                );
            }
        }
    }

    private ShiftResponse toResponse(Shift shift) {
        double workingHours = ShiftCalculator.calculateWorkingHours(
                shift.getStartTime(),
                shift.getEndTime(),
                shift.getBreakMinutes()
        );

        return new ShiftResponse(
                shift.getId(),
                shift.getEmployee().getId(),
                shift.getEmployee().getName(),
                shift.getEmployee().getType().name(),
                shift.getShiftDate(),
                shift.getStartTime(),
                shift.getEndTime(),
                shift.getBreakMinutes(),
                workingHours,
                shift.getStatus()
        );
    }
}