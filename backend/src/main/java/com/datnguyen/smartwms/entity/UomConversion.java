
// Date created: 2026-08-19
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "unit_of_measure_conversion", schema = "master", uniqueConstraints = @UniqueConstraint(name = "uk_uom_conversion", columnNames = {"sku_id", "from_uom_id", "to_uom_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)

public class UomConversion extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "conversion_id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID conversionId;

    @NotNull(message = "Conversion factor is required!")
    @DecimalMin(value = "0", inclusive = false, message = "Conversion factor must be > 0")
    @Column(name = "conversion_factor", precision = 18, scale = 6, nullable = false)
    private BigDecimal conversionFactor;

    @DecimalMin(value = "0", inclusive = false, message = "Package length must be > 0")
    @Column(name = "package_length", precision = 10, scale = 4)
    private BigDecimal packageLength;

    @DecimalMin(value = "0", inclusive = false, message = "Package width must be > 0")
    @Column(name = "package_width", precision = 10, scale = 4)
    private BigDecimal packageWidth;

    @DecimalMin(value = "0", inclusive = false, message = "Package height must be > 0")
    @Column(name = "package_height", precision = 10, scale = 4)
    private BigDecimal packageHeight;

    @DecimalMin(value = "0", inclusive = false, message = "Package weight must be > 0")
    @Column(name = "package_weight", precision = 10, scale = 4)
    private BigDecimal packageWeight;

    @DecimalMin(value = "0", inclusive = false, message = "Package volume must be > 0")
    @Column(name = "package_volume", precision = 10, scale = 4)
    private BigDecimal packageVolume;

    @NotNull(message = "From UOM is required!")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_uom_id", nullable = false, foreignKey = @ForeignKey(name = "fk_uom_from_uom_id"))
    private UnitOfMeasure fromUom;

    @NotNull(message = "To UOM is required!")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_uom_id", nullable = false, foreignKey = @ForeignKey(name = "fk_uom_to_uom_id"))
    private UnitOfMeasure toUom;

    @NotNull(message = "Sku is required!")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku_id", nullable = false, foreignKey = @ForeignKey(name = "fk_uom_conversion_sku_id"))
    private Sku sku;
}



