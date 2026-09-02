
// Date created: 2026-08-31
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity.inventory;

import com.datnguyen.smartwms.entity.base.AuditedEntity;
import com.datnguyen.smartwms.entity.location.Location;
import com.datnguyen.smartwms.entity.product.Sku;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "inventory_balance", schema = "inventory", uniqueConstraints = @UniqueConstraint(name = "uk_inventory_balance", columnNames = {"sku_id", "location_id", "lot_id", "lpn_id", "inventory_status"}))
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class InventoryBalance extends AuditedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "balance_id", nullable = false, updatable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID balanceId;

    @NotNull(message = "Quantity on hand is required!")
    @DecimalMin(value = "0", inclusive = true, message = "Quantity on hand must be >= 0")
    @Column(name = "quantity_on_hand", nullable = false, precision = 12, scale = 4)
    private BigDecimal quantityOnHand;

    @NotNull(message = "Quantity allocated is required!")
    @DecimalMin(value = "0", inclusive = true, message = "Quantity allocated must be >= 0")
    @Column(name = "quantity_allocated", nullable = false, precision = 12, scale = 4)
    private BigDecimal quantityAllocated;

    @NotNull(message = "Quantity picked is required!")
    @DecimalMin(value = "0", inclusive = true, message = "Quantity picked must be >= 0")
    @Column(name = "quantity_picked", nullable = false, precision = 12, scale = 4)
    private BigDecimal quantityPicked;

    @NotNull(message = "Quantity in transit is required!")
    @DecimalMin(value = "0", inclusive = true, message = "Quantity in transit must be >= 0")
    @Column(name = "quantity_in_transit", nullable = false, precision = 12, scale = 4)
    private BigDecimal quantityInTransit;

    @NotNull(message = "Quantity hold is required!")
    @DecimalMin(value = "0", inclusive = true, message = "Quantity hold must be >= 0")
    @Column(name = "quantity_hold", nullable = false, precision = 12, scale = 4)
    private BigDecimal quantityHold;

    @NotNull(message = "Quantity damaged is required!")
    @DecimalMin(value = "0", inclusive = true, message = "Quantity damaged must be >= 0")
    @Column(name = "quantity_damaged", nullable = false, precision = 12, scale = 4)
    private BigDecimal quantityDamaged;

    @NotNull(message = "Quantity blocked is required!")
    @DecimalMin(value = "0", inclusive = true, message = "Quantity blocked must be >= 0")
    @Column(name = "quantity_blocked", nullable = false, precision = 12, scale = 4)
    private BigDecimal quantityBlocked;

    public enum InventoryStatus {
        AVAILABLE, HOLD, QC, DAMAGED, RETURN, BLOCKED, EXPIRED
    }
    @Builder.Default
    @NotNull(message = "Inventory status is required!")
    @Size(max = 30, message = "Inventory status must be <= 30 characters!")
    @Column(name = "inventory_status", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private InventoryStatus inventoryStatus = InventoryStatus.AVAILABLE;

    @Column(name = "received_date")
    private OffsetDateTime receivedDate;

    @Column(name = "last_movement_date")
    private OffsetDateTime lastMovementDate;

    @NotNull(message = "Sku is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sku_id", nullable = false, foreignKey = @ForeignKey(name = "fk_balance_sku_id"))
    private Sku sku;

    @NotNull(message = "Location is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false, foreignKey = @ForeignKey(name = "fk_balance_location_id"))
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lot_id", foreignKey = @ForeignKey(name = "fk_balance_lot_id"))
    private InventoryLot lot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lpn_id", foreignKey = @ForeignKey(name = "fk_balance_lpn_id"))
    private Lpn lpn;
}
