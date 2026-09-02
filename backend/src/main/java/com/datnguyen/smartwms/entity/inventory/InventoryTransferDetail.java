
// Date created: 2026-09-02
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity.inventory;

import com.datnguyen.smartwms.entity.base.BaseEntity;
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
@Table(name = "inventory_transfer_detail", schema = "inventory")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class InventoryTransferDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "detail_id", nullable = false, updatable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID detailId;

    @NotNull(message = "Transfer is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transfer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transfer_detail_transfer_id"))
    private InventoryTransfer transfer;

    @NotNull(message = "Requested quantity is required!")
    @DecimalMin(value = "0", inclusive = false, message = "Requested quantity must be > 0!")
    @Column(name = "requested_quantity", nullable = false, precision = 18, scale = 4)
    private BigDecimal requestedQuantity;

    @NotNull(message = "Transferred quantity is required!")
    @DecimalMin(value = "0", inclusive = true, message = "Transferred quantity must be >= 0!")
    @Builder.Default
    @Column(name = "transferred_quantity", nullable = false, precision = 18, scale = 4)
    private BigDecimal transferredQuantity = BigDecimal.ZERO;

    public enum DetailStatus {
        PENDING, IN_PROGRESS, COMPLETED, CANCELLED, PARTIALLY_TRANSFERRED
    }
    @Builder.Default
    @NotNull(message = "Detail status is required!")
    @Enumerated(EnumType.STRING)
    @Column(name = "detail_status", nullable = false, length = 30)
    private DetailStatus detailStatus = DetailStatus.PENDING;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;

    @Size(max = 100, message = "Transferred by must be <= 100 characters!")
    @Column(name = "transferred_by", length = 100)
    private String transferredBy;

    @Column(name = "transferred_at")
    private OffsetDateTime transferredAt;

    @NotNull(message = "From location is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_location_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transfer_detail_from_location_id"))
    private Location fromLocation;

    @NotNull(message = "To location is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_location_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transfer_detail_to_location_id"))
    private Location toLocation;

    @NotNull(message = "Sku is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sku_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transfer_detail_sku_id"))
    private Sku sku;

    @NotNull(message = "Lot is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lot_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transfer_detail_lot_id"))
    private InventoryLot lot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_lpn_id", foreignKey = @ForeignKey(name = "fk_transfer_detail_from_lpn_id"))
    private Lpn fromLpn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_lpn_id", foreignKey = @ForeignKey(name = "fk_transfer_detail_to_lpn_id"))
    private Lpn toLpn;

    @AssertTrue(message = "From location and to location must be different!")
    public boolean isLocationValid() {
        if (fromLocation == null || toLocation == null) return true;
        return !fromLocation.getLocationId().equals(toLocation.getLocationId());
    }

    @AssertTrue(message = "Lot must belong to the specified Sku!")
    public boolean isLotSkuValid() {
        if (sku == null || lot == null) return true;
        return lot.getSku().getSkuId().equals(sku.getSkuId());
    }

    @AssertTrue(message = "From LPN must be at the from location!")
    public boolean isFromLpnLocationValid() {
        if (fromLpn == null || fromLocation == null) return true;
        if (fromLpn.getLocation() == null) return true;
        return fromLpn.getLocation().getLocationId().equals(fromLocation.getLocationId());
    }

    @AssertTrue(message = "Detail status is inconsistent with transferred quantity!")
    public boolean isStatusQuantityValid() {
        if (detailStatus == null || requestedQuantity == null || transferredQuantity == null) {
            return true;
        }
        int quantityCompare = transferredQuantity.compareTo(requestedQuantity);
        return switch (detailStatus) {
            case PENDING -> transferredQuantity.compareTo(BigDecimal.ZERO) == 0;
            case IN_PROGRESS -> transferredQuantity.compareTo(BigDecimal.ZERO) >= 0 && quantityCompare <= 0;
            case PARTIALLY_TRANSFERRED -> transferredQuantity.compareTo(BigDecimal.ZERO) > 0 && quantityCompare < 0;
            case COMPLETED -> quantityCompare == 0;
            case CANCELLED -> quantityCompare < 0;
        };
    }

    @AssertTrue(message = "Transfer execution data is inconsistent with detail status!")
    public boolean isTransferExecutionValid() {
        if (detailStatus == null) return true;
        boolean hasExecutionData = transferredBy != null && transferredAt != null;
        return switch (detailStatus) {
            case PENDING -> !(hasExecutionData);
            case IN_PROGRESS, PARTIALLY_TRANSFERRED, COMPLETED -> hasExecutionData;
            case CANCELLED -> true;
        };
    }
}
