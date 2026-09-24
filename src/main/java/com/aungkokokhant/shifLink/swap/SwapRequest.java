package com.aungkokokhant.shifLink.swap;

import com.aungkokokhant.shifLink.employee.Employee;
import com.aungkokokhant.shifLink.shift.Shift;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "swap_requests")
public class SwapRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_employee_id")
    private Employee requesterEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "replacement_employee_id")
    private Employee replacementEmployee;

    @Column(nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SwapRequestStatus status;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    @PrePersist
    void onCreate() {
        requestedAt = LocalDateTime.now();

        if (status == null) {
            status = SwapRequestStatus.PENDING;
        }
    }


}