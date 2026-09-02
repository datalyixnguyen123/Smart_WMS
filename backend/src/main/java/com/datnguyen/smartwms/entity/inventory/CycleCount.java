
// Date created: 2026-08-31
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity.inventory;

import com.datnguyen.smartwms.entity.base.AuditedEntity;

import com.datnguyen.smartwms.entity.location.Warehouse;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cycle_count", schema = "inventory")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class CycleCount extends AuditedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "cycle_count_id", nullable = false, updatable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID cycleCountId;

    @NotBlank(message = "Cycle count code is required!")
    @Size(max = 50, message = "Cycle count code must be <= 50 characters!")
    @ToString.Include
    @Column(name = "cycle_count_code", nullable = false, unique = true, length = 50)
    private String cycleCountCode;

    public enum CountType{
        ABC_BASED, LOCATION_BASED, SKU_BASED, RANDOM, DISCREPANCY_TRIGGERED
    }
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Count type is required!")
    @Column(name = "count_type", nullable = false, length = 30)
    @ToString.Include
    private CountType countType;

    public enum CountStatus{
        PLANNED, IN_PROGRESS, PENDING_APPROVAL, APPROVED, COMPLETED, CANCELLED
    }
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Count status is required!")
    @Column(name = "count_status", nullable = false, length = 30)
    @ToString.Include
    private CountStatus countStatus;

    @Column(name = "scheduled_date", nullable = false)
    @NotNull(message = "Scheduled date is required!")
    @ToString.Include
    private LocalDate scheduledDate;

    @Column(name = "started_at")
    @ToString.Include
    private OffsetDateTime startedAt;

    @Column(name = "completed_at")
    @ToString.Include
    private OffsetDateTime completedAt;

    @Column(name = "approved_at")
    @ToString.Include
    private OffsetDateTime approvedAt;

    @Column(name = "assigned_to", length = 100)
    @Size(max = 100, message = "Assigned to must be <= 100 characters!")
    @ToString.Include
    private String assignedTo;

    @Column(name = "approved_by", length = 100)
    @Size(max = 100, message = "Approved by must be <= 100 characters!")
    @ToString.Include
    private String approvedBy;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;

    @NotNull(message = "Warehouse is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cycle_count_warehouse_id"))
    private Warehouse warehouse;

    @AssertTrue(message = "The attributes approved_by and approved_at must both be set or both be null!")
    public boolean isValidApproval() { return (approvedBy == null) == (approvedAt == null); }

    @AssertTrue(message = "Invalid cycle count timestamps!")
    public boolean isValidTimeline() {
        if (startedAt != null && approvedAt != null && startedAt.isAfter(approvedAt)) return false;
        if (approvedAt != null && completedAt != null && approvedAt.isAfter(completedAt))  return false;
        if (startedAt != null && completedAt != null && startedAt.isAfter(completedAt)) return false;
        return true;
    }
}



