
// Date created: 2026-08-31
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity.inventory;

import com.datnguyen.smartwms.entity.product.Sku;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "inventory_lot", schema = "inventory")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class InventoryLot {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "lot_id", nullable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID lotId;

    @Column(name = "lot_number", nullable = false, length = 50)
    @NotBlank(message = "Lot number is required!")
    @Size(max = 50, message = "Lot number must be <= 50 characters!")
    @ToString.Include
    private String lotNumber;

    @Column(name = "manufacturing_date")
    private LocalDate manufacturingDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @NotNull(message = "Sku is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sku_id", nullable = false, foreignKey = @ForeignKey(name = "fk_lot_sku_id"))
    private Sku sku;
}
