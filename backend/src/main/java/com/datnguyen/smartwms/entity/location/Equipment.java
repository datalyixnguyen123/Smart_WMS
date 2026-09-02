
// Date created: 2026-08-22
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity.location;
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
@Table(name = "equipment", schema = "master")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class Equipment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "equipment_id", nullable = false, updatable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID equipmentId;

    @NotBlank(message = "Equipment code is required")
    @Size(max = 50, message = "Equipment code must be <= 50 characters!")
    @Column(name = "equipment_code", nullable = false, length = 50)
    private String equipmentCode;

    public enum EquipmentType {
        FORKLIFT, PALLET_JACK, AGV, AMR, TROLLEY, REACH_TRUCK, RF_SCANNER
    }
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Equipment type is required")
    @Size(max = 50, message = "Equipment type must be <= 50 characters!")
    @Column(name = "equipment_type", nullable = false, length = 50)
    private EquipmentType equipmentType;

    @NotBlank(message = "Equipment name is required")
    @Size(max = 255, message = "Equipment name must be <= 255 characters!")
    @Column(name = "equipment_name", nullable = false, length = 255)
    private String equipmentName;

    @DecimalMin(value = "0", inclusive = false, message = "Max weight capacity must be >= 0!")
    @Column(name = "max_weight_capacity", precision = 10, scale = 2)
    private BigDecimal maxWeightCapacity;

    @DecimalMin(value = "0", inclusive = false, message = "Max lift height must be >= 0!")
    @Column(name = "max_lift_height", precision = 5, scale = 2)
    private BigDecimal maxLiftHeight;

    public enum EquipmentStatus {
        AVAILABLE, MAINTENANCE, IN_USE, OUT_OF_SERVICE
    }
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @NotNull(message = "Equipment status is required")
    @Size(max = 30, message = "Equipment status must be <= 30 characters!")
    @Column(name = "equipment_status", nullable = false, length = 30)
    private EquipmentStatus equipmentStatus = EquipmentStatus.AVAILABLE;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @NotNull(message = "Zone is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "zone_id", nullable = false, foreignKey = @ForeignKey(name = "fk_equipment_zone_id"))
    private Zone zone;
}


