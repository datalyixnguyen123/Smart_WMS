
// Date created: 2026-09-01
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity.inventory;
import com.datnguyen.smartwms.entity.base.BaseEntity;
import com.datnguyen.smartwms.entity.location.Location;
import com.datnguyen.smartwms.entity.location.Warehouse;
import com.datnguyen.smartwms.entity.product.Sku;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "inventory_snapshot", schema = "inventory", uniqueConstraints = {@UniqueConstraint(name = "uk_inventory_snapshot", columnNames = {"sku_id", "lot_id", "lpn_id", "location_id", "snapshot_date", "snapshot_type"})})
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class InventorySnapshot extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "snapshot_id", nullable = false, updatable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID snapshotId;

    @Column(name = "snapshot_date",nullable = false)
    @NotNull(message = "Snapshot date is required!")
    private LocalDate snapshotDate;

    @Column(name = "snapshot_timestamp", nullable = false)
    @NotNull(message = "Snapshot timestamp is required!")
    private OffsetDateTime snapshotTimestamp;

    public enum SnapshotType{
        DAILY_CLOSING, MONTH_END, YEAR_END, CYCLE_COUNT, STOCK_TAKE, SHIFT_CHANGE, ON_DEMAND, PRE_MIGRATION
    }
    @NotNull(message = "Snapshot type is required!")
    @Enumerated(EnumType.STRING)
    @Column(name = "snapshot_type", nullable = false, length = 30)
    @ToString.Include
    private SnapshotType snapshotType;

    @Column(name = "quantity_on_hand", nullable = false, precision = 18, scale = 4)
    @NotNull(message = "Quantity on hand is required!")
    @DecimalMin(value = "0", inclusive = true, message = "Quantity on hand must be >= 0!")
    private BigDecimal quantityOnHand;

    @Column(name = "allocated_quantity", nullable = false, precision = 18, scale = 4)
    @NotNull(message = "Allocated quantity is required!")
    @DecimalMin(value = "0", inclusive = true, message = "Allocated quantity must be >= 0!")
    private BigDecimal allocatedQuantity;

    @Column(name = "available_quantity", nullable = false, precision = 18, scale = 4)
    @NotNull(message = "Available quantity is required!")
    @DecimalMin(value = "0", inclusive = true, message = "Available quantity must be >= 0!")
    private BigDecimal availableQuantity;

    @Column(name = "hold_quantity", nullable = false, precision = 18, scale = 4)
    @NotNull(message = "Hold quantity is required!")
    @DecimalMin(value = "0", inclusive = true, message = "Hold quantity must be >= 0!")
    private BigDecimal holdQuantity;

    @Column(name = "damaged_quantity", nullable = false, precision = 18, scale = 4)
    @NotNull(message = "Damaged quantity is required!")
    @DecimalMin(value = "0", inclusive = true, message = "Damaged quantity must be >= 0!")
    private BigDecimal damagedQuantity;

    @NotNull(message = "Warehouse is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false, foreignKey = @ForeignKey(name = "fk_snapshot_warehouse_id"))
    private Warehouse warehouse;

    @NotNull(message = "Location is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false, foreignKey = @ForeignKey(name = "fk_snapshot_location_id"))
    private Location location;

    @NotNull(message = "Sku is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sku_id", nullable = false, foreignKey = @ForeignKey(name = "fk_snapshot_sku_id"))
    private Sku sku;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", foreignKey = @ForeignKey(name = "fk_snapshot_lot_id"))
    private InventoryLot lot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lpn_id", foreignKey = @ForeignKey(name = "fk_snapshot_lpn_id"))
    private Lpn lpn;
}
