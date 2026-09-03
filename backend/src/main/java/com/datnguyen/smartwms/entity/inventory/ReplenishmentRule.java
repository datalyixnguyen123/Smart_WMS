
// Date created: 2026-09-02
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity.inventory;

import com.datnguyen.smartwms.entity.base.BaseEntity;
import com.datnguyen.smartwms.entity.location.Location;
import com.datnguyen.smartwms.entity.location.Zone;
import com.datnguyen.smartwms.entity.product.Sku;
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
@Table(name = "replenishment_rule", schema = "inventory")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class ReplenishmentRule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "rule_id", nullable = false, updatable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID ruleId;

    @NotBlank(message = "Rule code is required!")
    @Size(max = 50, message = "Rule code must be <= 50 characters!")
    @Column(name = "rule_code", nullable = false, unique = true, length = 50)
    @ToString.Include
    private String ruleCode;

    @NotBlank(message = "Rule name is required!")
    @Size(max = 100, message = "Rule name must be <= 100 characters!")
    @Column(name = "rule_name", nullable = false, length = 100)
    private String ruleName;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    public enum ReplenishmentType {
        MIN_MAX, TOP_OFF, DEMAND_DRIVEN
    }
    @Builder.Default
    @NotNull(message = "Replenishment type is required!")
    @Enumerated(EnumType.STRING)
    @Column(name = "replenishment_type", nullable = false, length = 30)
    private ReplenishmentType replenishmentType = ReplenishmentType.MIN_MAX;

    @NotNull(message = "Minimum quantity is required!")
    @DecimalMin(value = "0", inclusive = false, message = "Min quantity must be > 0!")
    @Column(name = "min_quantity", nullable = false, precision = 12, scale = 4)
    private BigDecimal minQuantity;

    @NotNull(message = "Maximum quantity is required!")
    @DecimalMin(value = "0", inclusive = false, message = "Max quantity must be > 0!")
    @Column(name = "max_quantity", nullable = false, precision = 12, scale = 4)
    private BigDecimal maxQuantity;

    public enum TriggerType {
        PERCENTAGE, MIN_QUANTITY
    }
    @NotNull(message = "Trigger type is required!")
    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", nullable = false, length = 30)
    private TriggerType triggerType;

    @DecimalMin(value = "0", inclusive = false, message = "Trigger percentage must be > 0!")
    @DecimalMax(value = "100", inclusive = true, message = "Trigger percentage must be <= 100!")
    @Column(name = "trigger_percentage", precision = 5, scale = 2)
    private BigDecimal triggerPercentage;

    @DecimalMin(value = "0", inclusive = false, message = "Target percentage must be > 0!")
    @DecimalMax(value = "100", inclusive = true, message = "Target percentage must be <= 100!")
    @Column(name = "target_percentage", precision = 5, scale = 2)
    private BigDecimal targetPercentage;

    @Builder.Default
    @Min(value = 1, message = "Priority must be > 0!")
    @Column(name = "priority", nullable = false)
    private Short priority = 1;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @NotNull(message = "Sku is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sku_id", nullable = false, foreignKey = @ForeignKey(name = "fk_replenish_sku_id"))
    private Sku sku;

    @NotNull(message = "Source zone is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_zone_id", nullable = false, foreignKey = @ForeignKey(name = "fk_replenish_source_zone_id"))
    private Zone sourceZone;

    @NotNull(message = "Target zone is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_zone_id", nullable = false, foreignKey = @ForeignKey(name = "fk_replenish_target_zone_id"))
    private Zone targetZone;

    @NotNull(message = "Target location is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_location_id", nullable = false, foreignKey = @ForeignKey(name = "fk_replenish_target_location_id"))
    private Location targetLocation;

    @AssertTrue(message = "Source zone and target zone must be different!")
    public boolean isSourceTargetZoneValid() {
        if (sourceZone == null || targetZone == null) return true;
        UUID sourceZoneId = sourceZone.getZoneId();
        UUID targetZoneId = targetZone.getZoneId();

        if (sourceZoneId == null || targetZoneId == null) return true;
        return !sourceZoneId.equals(targetZoneId);
    }

    @AssertTrue(message = "Min quantity must be < max quantity!")
    public boolean isQuantityRangeValid() {
        if (minQuantity == null || maxQuantity == null) return true;
        return minQuantity.compareTo(maxQuantity) < 0;
    }

    @AssertTrue(message = "Target percentage must be greater than trigger percentage!")
    public boolean isPercentageRangeValid() {
        if (triggerPercentage == null || targetPercentage == null) return true;
        return triggerPercentage.compareTo(targetPercentage) < 0 && targetPercentage.compareTo(BigDecimal.valueOf(100)) <= 0;
    }

    @AssertTrue(message = "Replenishment parameters are inconsistent with replenishment type!")
    public boolean isReplenishmentParameterValid() {
        if (replenishmentType == null) return true;
        return switch (replenishmentType) {
            case MIN_MAX -> minQuantity != null && maxQuantity != null && triggerPercentage == null && targetPercentage == null;
            case TOP_OFF -> minQuantity == null && maxQuantity == null && triggerPercentage != null && targetPercentage != null;
            case DEMAND_DRIVEN -> true;
        };
    }

    @AssertTrue(message = "Trigger percentage must be set when trigger type is 'PERCENTAGE'!")
    public boolean isTriggerPercentageValid() {
        if (triggerType == null) return true;
        if (triggerType == TriggerType.PERCENTAGE) return triggerPercentage != null;
        return triggerPercentage == null;
    }

    @AssertTrue(message = "Target location must belong to target zone!")
    public boolean isTargetLocationValid() {
        if (targetZone == null || targetLocation == null) return true;
        if (targetZone.getZoneId() == null || targetLocation.getZone() == null || targetLocation.getZone().getZoneId() == null) {
            return true;
        }
        return targetZone.getZoneId().equals(targetLocation.getZone().getZoneId());
    }

    @AssertTrue(message = "Source zone and target zone must belong to the same warehouse!")
    public boolean isWarehouseScopeValid() {
        if (sourceZone == null || targetZone == null) return true;
        if (sourceZone.getWarehouse() == null || targetZone.getWarehouse() == null || sourceZone.getWarehouse().getWarehouseId() == null || targetZone.getWarehouse().getWarehouseId() == null) {
            return true;
        }
        return sourceZone.getWarehouse().getWarehouseId().equals(targetZone.getWarehouse().getWarehouseId());
    }
}



