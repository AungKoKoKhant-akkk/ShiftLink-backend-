package com.aungkokokhant.shifLink.shift;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findByEmployeeIdAndShiftDateBetween(
            Long employeeId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Shift> findByEmployee_EmployeeCodeOrderByShiftDateAscStartTimeAsc(
            String employeeCode
    );
}