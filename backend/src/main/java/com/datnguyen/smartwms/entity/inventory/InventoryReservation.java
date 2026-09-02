
// Date created: 2026-09-01
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity.inventory;
import com.datnguyen.smartwms.entity.base.AuditedEntity;
import com.datnguyen.smartwms.entity.location.Location;
import com.datnguyen.smartwms.entity.location.Warehouse;
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
@Table(name = "inventory_reservation", schema = "inventory")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class InventoryReservation extends AuditedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "reservation_id", nullable = false, updatable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID reservationId;

    @NotBlank(message = "Reservation code is required!")
    @Size(max = 50, message = "Reservation code must be <= 50 characters!")
    @Column(name = "reservation_code", nullable = false, unique = true, length = 50)
    @ToString.Include
    private String reservationCode;

    public enum ReservationType{
        CART_RESERVATION, PICKING_ALLOCATION, REPLENISHMENT_BLOCK, INTERNAL_QC_HOLD
    }
    @NotNull(message = "Reservation type is required!")
    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_type", nullable = false, length = 50)
    @ToString.Include
    private ReservationType reservationType;

    public enum ReservationStatus{
        ACTIVE, RELEASED, FULFILLED, CANCELLED, HOLD
    }
    @NotNull(message = "Reservation status is required!")
    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status", nullable = false, length = 50)
    @ToString.Include
    @Builder.Default
    private ReservationStatus reservationStatus = ReservationStatus.ACTIVE;

    @Column(name = "reserved_quantity", nullable = false, precision = 12, scale = 4)
    @NotNull(message = "Reserved quantity is required!")
    @DecimalMin(value = "0", inclusive = true, message = "Reserved quantity must be >= 0!")
    private BigDecimal reservedQuantity;

    @Column(name = "allocated_quantity", nullable = false, precision = 12, scale = 4)
    @NotNull(message = "Allocated quantity is required!")
    @DecimalMin(value = "0", inclusive = true, message = "Reserved quantity must be >= 0!")
    private BigDecimal allocatedQuantity;

    @Column(name = "picked_quantity", nullable = false, precision = 12, scale = 4)
    @NotNull(message = "Picked quantity is required!")
    @DecimalMin(value = "0", inclusive = true, message = "Reserved quantity must be >= 0!")
    private BigDecimal pickedQuantity;

    //**
    @Column(name = "reference_doc_id")
    private UUID referenceDocId;

    public enum ReferenceDocType{
        SO, TO
    }
    @NotNull(message = "Reference doc type is required!")
    @Enumerated(EnumType.STRING)
    @Column(name = "reference_doc_type", nullable = false, length = 30)
    @ToString.Include
    private ReferenceDocType referenceDocType;

    @Column(name = "reserved_at", nullable = false)
    private OffsetDateTime reservedAt;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "released_at")
    private OffsetDateTime releasedAt;

    @Column(name = "fulfilled_at")
    private OffsetDateTime fulfilledAt;

    @NotNull(message = "Warehouse is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reservation_warehouse_id"))
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", foreignKey = @ForeignKey(name = "fk_reservation_location_id"))
    private Location location;

    @NotNull(message = "Sku is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sku_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reservation_sku_id"))
    private Sku sku;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lot_id", foreignKey = @ForeignKey(name = "fk_reservation_lot_id"))
    private InventoryLot lot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lpn_id", foreignKey = @ForeignKey(name = "fk_reservation_lpn_id"))
    private Lpn lpn;

    @AssertTrue(message = "Invalid reservation type!")
    public boolean isValidReservationType() {
        if(reservationType == null) return true;
        boolean hasLocation = location != null;
        boolean hasExpiry = expiresAt != null;

        return switch(reservationType) {
            case CART_RESERVATION -> hasExpiry && !hasLocation;
            case PICKING_ALLOCATION-> !hasExpiry && hasLocation;
            case REPLENISHMENT_BLOCK -> hasLocation;
            case INTERNAL_QC_HOLD -> hasLocation;
        };
    }

    @AssertTrue(message = "Invalid reservation quantities!")
    public boolean isValidQuantities() {
        if (reservedQuantity == null || allocatedQuantity == null || pickedQuantity == null) {
            return true;
        }
        return allocatedQuantity.compareTo(reservedQuantity) <= 0 && pickedQuantity.compareTo(allocatedQuantity) <= 0;
    }
}








