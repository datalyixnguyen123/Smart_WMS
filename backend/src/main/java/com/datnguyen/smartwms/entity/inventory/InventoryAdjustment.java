
// Date created: 2026-08-31
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity.inventory;

import com.datnguyen.smartwms.entity.base.AuditedEntity;
import com.datnguyen.smartwms.entity.location.Warehouse;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "inventory_adjustment", schema = "inventory")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class InventoryAdjustment extends AuditedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "adjustment_id", nullable = false, updatable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID adjustmentId;

    @Column(name = "reference_id")
    private UUID referenceId;

    @NotBlank(message = "Adjustment code is required!")
    @Size(max = 50, message = "Adjustment code must be <= 50 characters!")
    @Column(name = "adjustment_code", nullable = false, unique = true, length = 50)
    private String adjustmentCode;

    public enum AdjustmentType {
        CYCLE_COUNT_DISCREPANCY, DAMAGE_SCRAP, THEFT_LOSS, ADMIN_CORRECTION
    }
    @NotNull(message = "Adjustment type is required!")
    @Enumerated(EnumType.STRING)
    @Column(name = "adjustment_type", nullable = false, length = 50)
    private AdjustmentType adjustmentType;

    public enum AdjustmentStatus {
        DRAFT, PENDING_APPROVAL, APPROVED, REJECTED, POSTED, CANCELLED
    }
    @Builder.Default
    @NotNull(message = "Adjustment status is required!")
    @Enumerated(EnumType.STRING)
    @Column(name = "adjustment_status", nullable = false, length = 50)
    private AdjustmentStatus adjustmentStatus = AdjustmentStatus.DRAFT;

    @Column(name = "adjustment_time")
    private OffsetDateTime adjustmentTime;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    public enum ReferenceType {
        CYCLE_COUNT, DAMAGE_REPORT, QA_INSPECTION, ERP_SYNC, NONE
    }
    @NotNull(message = "Reference type is required!")
    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type", nullable = false, length = 30)
    private ReferenceType referenceType;

    @NotBlank(message = "Requested by is required!")
    @Size(max = 50, message = "Requested by must be <= 50 characters!")
    @Column(name = "requested_by", nullable = false, length = 50)
    private String requestedBy;

    @Size(max = 50, message = "Approved by must be <= 50 characters!")
    @Column(name = "approved_by", length = 50)
    private String approvedBy;

    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;

    @NotNull(message = "Warehouse is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false, foreignKey = @ForeignKey(name = "fk_adjustment_warehouse_id"))
    private Warehouse warehouse;

    @NotNull(message = "Discrepancy reason is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reason_id", nullable = false, foreignKey = @ForeignKey(name = "fk_adjustment_reason_id"))
    private DiscrepancyReason reason;

    @AssertTrue(message = "The attributes approved_by and approved_at must both be set or both be null!")
    public boolean isApproval() { return (approvedBy == null) == (approvedAt == null); }

    @AssertTrue(message = "Reference ID must be null when reference type is 'NONE', otherwise it is required.")
    public boolean isReference() {
        if (referenceType == null) return true;
        if (referenceType == ReferenceType.NONE) return referenceId == null;
        return referenceId != null;
    }
}
