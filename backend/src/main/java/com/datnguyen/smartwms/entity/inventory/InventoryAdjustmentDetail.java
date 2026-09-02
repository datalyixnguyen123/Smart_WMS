
// Date created: 2026-08-31
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity.inventory;

import com.datnguyen.smartwms.entity.base.BaseEntity;
import com.datnguyen.smartwms.entity.location.Location;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "inventory_adjustment_detail", schema = "inventory")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class InventoryAdjustmentDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "adjustment_detail_id", nullable = false, updatable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID adjustmentDetailId;

    @NotNull(message = "Quantity before is required!")
    @DecimalMin(value = "0", inclusive = true, message = "Quantity before must be >= 0")
    @Column(name = "quantity_before", nullable = false, precision = 12, scale = 4)
    private BigDecimal quantityBefore;

    @NotNull(message = "Quantity after is required!")
    @DecimalMin(value = "0", inclusive = true, message = "Quantity after must be >= 0")
    @Column(name = "quantity_after", nullable = false, precision = 12, scale = 4)
    private BigDecimal quantityAfter;

    @NotNull(message = "Adjusted quantity is required!")
    @DecimalMin(value = "0", inclusive = false, message = "Adjusted quantity must be > 0")
    @Column(name = "adjusted_quantity", nullable = false, precision = 12, scale = 4)
    private BigDecimal adjustedQuantity;

    @NotNull(message = "Inventory adjustment is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "adjustment_id", nullable = false, foreignKey = @ForeignKey(name = "fk_adjustment_detail_adjustment_id"))
    private InventoryAdjustment inventoryAdjustment;

    @NotNull(message = "Location is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false, foreignKey = @ForeignKey(name = "fk_adjustment_detail_location_id"))
    private Location location;

    @NotNull(message = "Lot is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lot_id", nullable = false, foreignKey = @ForeignKey(name = "fk_adjustment_detail_lot_id"))
    private InventoryLot lot;

    @NotNull(message = "LPN is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lpn_id", nullable = false, foreignKey = @ForeignKey(name = "fk_adjustment_detail_lpn_id"))
    private Lpn lpn;
}


