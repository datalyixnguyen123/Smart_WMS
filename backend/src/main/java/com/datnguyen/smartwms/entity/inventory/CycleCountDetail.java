
// Date created: 2026-09-01
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity.inventory;

import com.datnguyen.smartwms.entity.location.Location;
import com.datnguyen.smartwms.entity.product.Sku;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cycle_count_detail", schema = "inventory")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class CycleCountDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "detail_id", nullable = false, updatable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID detailId;

    @NotNull(message = "System quantity is required!")
    @DecimalMin(value = "0", inclusive = true, message = "System quantity must be >= 0!")
    @Column(name = "system_quantity", nullable = false, precision = 18, scale = 4)
    private BigDecimal systemQuantity;

    @DecimalMin(value = "0", inclusive = true, message = "Counted quantity must be >= 0!")
    @Column(name = "counted_quantity", precision = 18, scale = 4)
    private BigDecimal countedQuantity;

    @DecimalMin(value = "0", inclusive = true, message = "Recounted quantity must be >= 0!")
    @Column(name = "recounted_quantity", precision = 18, scale = 4)
    private BigDecimal recountedQuantity;

    // Variance quantity = recounted_quantity - system_quantity (can be negative)
    @Column(name = "variance_quantity", precision = 18, scale = 4)
    private BigDecimal varianceQuantity;

    @Column(name = "system_quantity_snapshot_at", nullable = false)
    @NotNull(message = "System quantity snapshot at is required!")
    private OffsetDateTime systemQuantitySnapshotAt;

    public enum DetailStatus{
        PENDING, COUNTED, RECOUNT_REQUESTED, APPROVED
    }
    @NotNull(message = "Detail status is required!")
    @Enumerated(EnumType.STRING)
    @Column(name = "detail_status", nullable = false, length = 30)
    @ToString.Include
    private DetailStatus detailStatus;

    @Size(max = 100, message = "Counted by must be <= 100 characters!")
    @Column(name = "counted_by", length = 100)
    private String countedBy;

    @Column(name = "counted_at")
    private OffsetDateTime countedAt;

    @NotNull(message = "Cycle count is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cycle_count_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cycle_count_detail_cycle_count_id"))
    private CycleCount cycleCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reason_id", foreignKey = @ForeignKey(name = "fk_cycle_count_detail_reason_id"))
    private DiscrepancyReason reason;

    @NotNull(message = "Location is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cycle_count_detail_location_id"))
    private Location location;

    @NotNull(message = "Lot is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lot_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cycle_count_detail_lot_id"))
    private InventoryLot lot;

    @NotNull(message = "LPN is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lpn_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cycle_count_detail_lpn_id"))
    private Lpn lpn;

    @NotNull(message = "Sku is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sku_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cycle_count_detail_sku_id"))
    private Sku sku;
}
