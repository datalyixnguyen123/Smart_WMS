
// Date created: 2026-08-22
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity.product;
import com.datnguyen.smartwms.entity.partner.BusinessPartner;
import com.datnguyen.smartwms.entity.base.BaseEntity;
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
@Table(name = "sku_supplier", schema = "master", uniqueConstraints = @UniqueConstraint(name = "uk_sku_supplier_sku_partner", columnNames = {"sku_id", "partner_id"}))
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class SkuSupplier extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "sku_supplier_id", nullable = false, updatable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID skuSupplierId;

    @Size(max = 100, message = "Contract number must be <= 100 characters")
    @Column(name = "contract_number", length = 100)
    private String contractNumber;

    @PositiveOrZero(message = "Lead time days must be >= 0")
    @Column(name = "lead_time_days", nullable = false)
    private Integer leadTimeDays;

    @DecimalMin(value = "0", inclusive = false, message = "Minimum order quantity must be > 0")
    @Column(name = "minimum_order_qty", precision = 12, scale = 4, nullable = false)
    private BigDecimal minimumOrderQty;

    @Positive(message = "Priority must be > 0")
    @NotNull(message = "Priority is required!")
    @Column(name = "priority", nullable = false)
    private Short priority = 1;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @NotNull(message = "Sku is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sku_id", nullable = false, foreignKey = @ForeignKey(name = "fk_sku_supplier_id"))
    private Sku sku;

    @NotNull(message = "Business partner is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partner_id", nullable = false, foreignKey = @ForeignKey(name = "fk_sku_supplier_partner_id"))
    private BusinessPartner partner;
}


