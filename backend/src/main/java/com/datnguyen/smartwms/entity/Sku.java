
// Date created: 2026-08-19
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity;
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
@Table(name = "sku", schema = "master")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class Sku extends AuditedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "sku_id", nullable = false, updatable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID skuId;

    @NotBlank(message = "Sku code is required!")
    @Size(max = 50, message = "Sku code must be <= 50 characters!")
    @Column(name = "sku_code", nullable = false, unique = true, length = 50)
    private String skuCode;

    @NotBlank(message = "Sku name is required!")
    @Size(max = 255, message = "Sku name must be <= 255 characters!")
    @Column(name = "sku_name", nullable = false, length = 255)
    private String skuName;

    @Column(name = "barcode", unique = true, length = 50)
    private String barcode;

    @DecimalMin(value = "0", inclusive = false, message = "Sku length must be > 0")
    @Column(name = "sku_length", precision = 10, scale = 3)
    private BigDecimal skuLength;

    @DecimalMin(value = "0", inclusive = false, message = "Sku width must be > 0")
    @Column(name = "sku_width", precision = 10, scale = 3)
    private BigDecimal skuWidth;

    @DecimalMin(value = "0", inclusive = false, message = "Sku height must be > 0")
    @Column(name = "sku_height", precision = 10, scale = 3)
    private BigDecimal skuHeight;

    @DecimalMin(value = "0", inclusive = false, message = "Sku weight must be > 0")
    @Column(name = "sku_weight", precision = 10, scale = 3)
    private BigDecimal skuWeight;

    @DecimalMin(value = "0", inclusive = false, message = "Sku volume must be > 0")
    @Column(name = "sku_volume", precision = 10, scale = 3)
    private BigDecimal skuVolume;

    public enum SkuStatus {
        ACTIVE, INACTIVE, DISCONTINUED
    }
    @NotNull(message = "Sku status is required!")
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "sku_status", nullable = false, length = 30)
    private SkuStatus status = SkuStatus.ACTIVE;

    @Column(name = "packaging_material", length = 50)
    private String packagingMaterial;

    // The number of cases per layer
    @Positive(message = "Tie must be > 0")
    @Column(name = "tie")
    private Integer tie;

    // The number of layers per pallet
    @Positive(message = "High must be > 0")
    @Column(name = "high")
    private Integer high;

    @PositiveOrZero(message = "Stacking limit must be >= 0")
    @Column(name = "stacking_limit")
    private Integer stackingLimit;

    @Builder.Default
    @Column(name = "is_fragile", nullable = false)
    private boolean fragile = false;

    @Builder.Default
    @Column(name = "is_hazardous", nullable = false)
    private boolean hazardous = false;

    public enum PackagingLevel {
        BASE_UNIT, INNER_PACK, CASE, PALLET, DISPLAY
    }
    @NotNull(message = "Packaging level is required!")
    @Enumerated(EnumType.STRING)
    @Column(name = "packaging_level", length = 30)
    private PackagingLevel packagingLevel;

    @NotNull(message = "Product is required!")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", foreignKey = @ForeignKey(name = "fk_sku_product_id"), nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", foreignKey = @ForeignKey(name = "fk_sku_policy_id"))
    private StoragePolicy policy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "constraint_id", foreignKey = @ForeignKey(name = "fk_sku_constraint_id"))
    private StorageConstraint constraint;

    @NotNull(message = "Base unit of measure is required!")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_uom_id", foreignKey = @ForeignKey(name = "fk_sku_uom_id"), nullable = false)
    private UnitOfMeasure baseUom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "velocity_code", foreignKey = @ForeignKey(name = "fk_sku_velocity_code"))
    private VelocityClass velocity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "abc_code", foreignKey = @ForeignKey(name = "fk_sku_abc_code"))
    private ABCClass abc;
}


