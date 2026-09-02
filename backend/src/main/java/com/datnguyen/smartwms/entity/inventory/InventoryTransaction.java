
// Date created: 2026-09-01
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity.inventory;

import com.datnguyen.smartwms.entity.base.AuditedEntity;
import com.datnguyen.smartwms.entity.location.Location;
import com.datnguyen.smartwms.entity.product.Sku;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "inventory_transaction", schema = "inventory")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class InventoryTransaction extends AuditedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "transaction_id", nullable = false, updatable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID transactionId;

    @Column(name = "reference_doc_id")
    private UUID referenceDocId;

    @Column(name = "operator_id")
    private UUID operatorId;

    @NotBlank(message = "Transaction code is required!")
    @Size(max = 50, message = "Transaction code must be <= 50 characters!")
    @Column(name = "transaction_code", nullable = false, unique = true, length = 50)
    @ToString.Include
    private String transactionCode;

    public enum TransactionType {
        RECEIPT, ISSUE, TRANSFER, ADJUSTMENT, COUNT, RETURN, QC, REPLENISHMENT, HOLD, RELEASE
    }
    @NotNull(message = "Transaction type is required!")
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 50)
    private TransactionType transactionType;

    public enum TransactionSubtype {
        PURCHASE_RECEIPT, SHIPMENT, PICKING, PUTAWAY, MOVING, DAMAGE, LOST, FOUND, CYCLE_COUNT, RETURN_SUPPLIER, EXPIRED
    }
    @NotNull(message = "Transaction subtype is required!")
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_subtype", nullable = false, length = 50)
    private TransactionSubtype transactionSubtype;

    // Quantity change can get both positive and negative values
    @NotNull(message = "Quantity change is required!")
    @Column(name = "quantity_change", nullable = false, precision = 18, scale = 4)
    private BigDecimal quantityChange;

    public enum ReferenceDocType {
        PO, SO, TO, CC, RO
    }
    @Enumerated(EnumType.STRING)
    @Column(name = "reference_doc_type", length = 30)
    private ReferenceDocType referenceDocType;

    @CreationTimestamp
    @Column(name = "transaction_time", nullable = false, updatable = false)
    private OffsetDateTime transactionTime;

    @Column(name = "remarks", columnDefinition = "text")
    private String remarks;

    @NotNull(message = "Sku is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sku_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transaction_sku_id"))
    private Sku sku;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lot_id", foreignKey = @ForeignKey(name = "fk_transaction_lot_id"))
    private InventoryLot lot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lpn_id", foreignKey = @ForeignKey(name = "fk_transaction_lpn_id"))
    private Lpn lpn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_location_id", foreignKey = @ForeignKey(name = "fk_transaction_from_location_id"))
    private Location fromLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_location_id", foreignKey = @ForeignKey(name = "fk_transaction_to_location_id"))
    private Location toLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reason_id", foreignKey = @ForeignKey(name = "fk_transaction_reason_id"))
    private DiscrepancyReason reason;

    @AssertTrue(message = "At least one of the attributes from_location / to_location must be set!")
    public boolean isLocationValid() {
        if (fromLocation == null && toLocation == null) return false;
        if (fromLocation != null && toLocation != null) {
            return !fromLocation.getLocationId().equals(toLocation.getLocationId());
        }
        return true;
    }

    @AssertTrue(message = "Invalid location configuration for transaction type!")
    public boolean isLocationConfigValid() {
        if (transactionType == null) return true;

        boolean hasFrom = fromLocation != null;
        boolean hasTo = toLocation != null;

        return switch (transactionType) {
            case RECEIPT -> !(hasFrom) && hasTo;
            case ISSUE-> hasFrom && !(hasTo);
            case TRANSFER, REPLENISHMENT -> hasFrom && hasTo && !fromLocation.getLocationId().equals(toLocation.getLocationId());
            case ADJUSTMENT, COUNT, QC, HOLD, RELEASE -> !(hasFrom) && !(hasTo);
            case RETURN-> true;
        };
    }

    @AssertTrue(message = "Invalid transaction subtype for transaction type!")
    public boolean isTransactionSubtypeValid() {
        if (transactionType == null || transactionSubtype == null) return true;
        return switch (transactionType) {
            case RECEIPT -> transactionSubtype == TransactionSubtype.PURCHASE_RECEIPT;
            case ISSUE -> transactionSubtype == TransactionSubtype.SHIPMENT || transactionSubtype == TransactionSubtype.PICKING;
            case TRANSFER -> transactionSubtype == TransactionSubtype.MOVING;
            case ADJUSTMENT -> transactionSubtype == TransactionSubtype.DAMAGE || transactionSubtype == TransactionSubtype.LOST || transactionSubtype == TransactionSubtype.FOUND || transactionSubtype == TransactionSubtype.EXPIRED;
            case COUNT -> transactionSubtype == TransactionSubtype.CYCLE_COUNT;
            case RETURN -> transactionSubtype == TransactionSubtype.RETURN_SUPPLIER;
            case QC -> transactionSubtype == TransactionSubtype.DAMAGE;
            case REPLENISHMENT -> transactionSubtype == TransactionSubtype.PUTAWAY;
            case HOLD, RELEASE -> true;
        };
    }

    @AssertTrue(message = "Reference document type and ID must be provided together!")
    public boolean isReferenceValid() {
        if (referenceDocType == null) return referenceDocId == null;
        return referenceDocId != null;
    }
}
