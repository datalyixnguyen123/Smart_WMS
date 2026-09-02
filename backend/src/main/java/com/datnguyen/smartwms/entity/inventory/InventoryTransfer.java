
// Date created: 2026-09-01
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity.inventory;

import com.datnguyen.smartwms.entity.base.AuditedEntity;
import com.datnguyen.smartwms.entity.location.Location;
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
@Table(name = "inventory_transfer", schema = "inventory")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class InventoryTransfer extends AuditedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transfer_id", nullable = false, updatable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID transferId;

    @NotBlank(message = "Transfer code is required!")
    @Size(max = 50, message = "Transfer code must be <= 50 characters!")
    @Column(name = "transfer_code", nullable = false, unique = true, length = 50)
    @ToString.Include
    private String transferCode;

    public enum TransferType {
        INTERNAL_LOCATION, INTER_WAREHOUSE, REPLENISHMENT
    }
    @NotNull(message = "Transfer type is required!")
    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_type", nullable = false, length = 50)
    private TransferType transferType;

    @Builder.Default
    @Min(value = 1, message = "Priority must be > 0!")
    @Column(name = "priority", nullable = false)
    private Integer priority = 1;

    public enum TransferStatus {
        DRAFT, PLANNED, RELEASED, IN_PROGRESS, COMPLETED, CANCELLED
    }
    @Builder.Default
    @NotNull(message = "Transfer status is required!")
    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_status", nullable = false, length = 50)
    private TransferStatus transferStatus = TransferStatus.DRAFT;

    @NotNull(message = "Scheduled date is required!")
    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @NotBlank(message = "Requested by is required!")
    @Size(max = 100, message = "Requested by must be <= 100 characters!")
    @Column(name = "requested_by", nullable = false, length = 100)
    private String requestedBy;

    @Size(max = 100, message = "Assigned to must be <= 100 characters!")
    @Column(name = "assigned_to", length = 100)
    private String assignedTo;

    @Size(max = 100, message = "Approved by must be <= 100 characters!")
    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;

    @NotNull(message = "Source warehouse is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_warehouse_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transfer_from_warehouse_id"))
    private Warehouse fromWarehouse;

    @NotNull(message = "Destination warehouse is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_warehouse_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transfer_to_warehouse_id"))
    private Warehouse toWarehouse;

    @NotNull(message = "Source location is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_location_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transfer_from_location_id")
    )
    private Location fromLocation;

    @NotNull(message = "Destination location is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_location_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transfer_to_location_id")
    )
    private Location toLocation;

    @AssertTrue(message = "Invalid warehouse/location configuration for transfer type!")
    public boolean isTransferConfigValid() {
        if (transferType == null || fromWarehouse == null || toWarehouse == null || fromLocation == null || toLocation == null) {
            return true;
        }
        UUID fromWarehouseId = fromWarehouse.getWarehouseId();
        UUID toWarehouseId = toWarehouse.getWarehouseId();
        UUID fromLocationId = fromLocation.getLocationId();
        UUID toLocationId = toLocation.getLocationId();

        if (fromLocationId.equals(toLocationId)) return false;
        return switch (transferType) {
            case INTERNAL_LOCATION -> fromWarehouseId.equals(toWarehouseId);
            case INTER_WAREHOUSE -> !fromWarehouseId.equals(toWarehouseId);
            case REPLENISHMENT -> fromWarehouseId.equals(toWarehouseId);
        };
    }

    private boolean isValidTransition(TransferStatus current, TransferStatus next) {
        return switch (current) {
            case DRAFT -> next == TransferStatus.PLANNED || next == TransferStatus.CANCELLED;
            case PLANNED -> next == TransferStatus.RELEASED || next == TransferStatus.CANCELLED;
            case RELEASED -> next == TransferStatus.IN_PROGRESS || next == TransferStatus.CANCELLED;
            case IN_PROGRESS -> next == TransferStatus.COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }

    @AssertTrue(message = "Released or started transfers must be approved!")
    public boolean isApprovalStateValid() {
        if (transferStatus == null) return true;
        boolean approved = approvedBy != null && approvedAt != null;
        return switch (transferStatus) {
            case DRAFT, PLANNED, CANCELLED -> true;
            case RELEASED, IN_PROGRESS, COMPLETED -> approved;
        };
    }

    @AssertTrue(message = "The attributes approved_by and approved_at must both be set or both be null!")
    public boolean isApprovalValid() {
        return (approvedBy == null) == (approvedAt == null);
    }

    @AssertTrue(message = "Transfer timestamps do not match transfer status!")
    public boolean isTimelineValid() {
        if (transferStatus == null) return true;
        return switch (transferStatus) {
            case DRAFT, PLANNED, RELEASED -> startedAt == null && completedAt == null;
            case IN_PROGRESS -> startedAt != null && completedAt == null;
            case COMPLETED -> startedAt != null && completedAt != null && completedAt.isAfter(startedAt);
            case CANCELLED -> completedAt == null;
        };
    }
}
