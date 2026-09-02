
// Date created: 2026-08-31
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity.inventory;

import com.datnguyen.smartwms.entity.base.BaseEntity;
import com.datnguyen.smartwms.entity.product.Sku;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "lpn_detail", schema = "inventory", uniqueConstraints = {@UniqueConstraint(name = "uk_lpn_detail_lpn_lot_sku", columnNames = {"lpn_id", "lot_id", "sku_id"})})
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class LpnDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "lpn_detail_id", nullable = false, updatable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID lpnDetailId;

    @NotNull(message = "Quantity contained is required!")
    @DecimalMin(value = "0", inclusive = false, message = "Quantity contained must be > 0")
    @Column(name = "quantity_contained", nullable = false, precision = 18, scale = 4)
    private BigDecimal quantityContained;

    @NotNull(message = "Lpn is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lpn_id", nullable = false, foreignKey = @ForeignKey(name = "fk_lpn_detail_lpn_id"))
    private Lpn lpn;

    @NotNull(message = "Lot is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lot_id", nullable = false, foreignKey = @ForeignKey(name = "fk_lpn_detail_lot_id"))
    private InventoryLot lot;

    @NotNull(message = "Sku is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sku_id", nullable = false, foreignKey = @ForeignKey(name = "fk_lpn_detail_lot_id"))
    private Sku sku;
}
