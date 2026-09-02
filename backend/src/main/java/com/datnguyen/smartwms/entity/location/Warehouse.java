
// Date created: 2026-08-17
// Date last modified: 2026-08-17
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity.location;
import com.datnguyen.smartwms.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "warehouse", schema = "master")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class Warehouse extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "warehouse_id", nullable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID warehouseId;

    @NotBlank(message = "Warehouse code is required!")
    @Size(max = 50, message = "Warehouse code must be <= 50 characters!")
    @Column(name = "warehouse_code", nullable = false, unique = true, length = 50)
    @ToString.Include
    private String warehouseCode;

    @NotBlank(message = "Warehouse name is required!")
    @Size(max = 255, message = "Warehouse name must be <= 255 characters!")
    @Column(name = "warehouse_name", nullable = false, length = 255)
    @ToString.Include
    private String warehouseName;

    public enum WarehouseType {
        DRY, COLD, BONDED, CROSS_DOCK, HAZMAT
    }
    @NotBlank(message = "Warehouse type is required!")
    @Size(max = 30, message = "Warehouse type must be <= 30 characters!")
    @Enumerated(EnumType.STRING)
    @Column(name = "warehouse_type", nullable = false, length = 30)
    private WarehouseType warehouseType;

    @Column(name = "warehouse_address", columnDefinition = "text")
    private String warehouseAddress;

    public enum WarehouseStatus {
        ACTIVE, INACTIVE, MAINTENANCE
    }
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "warehouse_status", nullable = false, length = 30)
    private WarehouseStatus warehouseStatus = WarehouseStatus.ACTIVE;

    @NotBlank(message = "Timezone is required!")
    @Size(max = 30, message = "Timezone must be <= 30 characters!")
    @Column(name = "timezone", nullable = false, length = 30)
    private String timezone;

    @DecimalMin(value = "0", inclusive = true, message = "Total area must be >= 0!")
    @Column(name = "total_area", precision = 12, scale = 2)
    private BigDecimal totalArea;

    @DecimalMin(value = "0", inclusive = true, message = "Gross volume must be >= 0")
    @Column(name = "gross_volume", precision = 12, scale = 2)
    private BigDecimal grossVolume;

    @DecimalMin(value = "0", inclusive = true, message = "Usable capacity volume must be >= 0")
    @Column(name = "usable_capacity_volume", precision = 12, scale = 2)
    private BigDecimal usableCapacityVolume;

    @DecimalMin(value = "-90", inclusive = true, message = "Latitude must be >= -90")
    @DecimalMax(value = "90", inclusive = true, message = "Latitude must be <= 90")
    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    @DecimalMin(value = "-180", inclusive = true, message = "Longitude must be >= -180")
    @DecimalMax(value = "180", inclusive = true, message = "Longitude must be <= 180")
    @Column(name = "longitude", precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "operating_start_time")
    private LocalTime operatingStartTime;

    @Column(name = "operating_end_time")
    private LocalTime operatingEndTime;

    @AssertTrue(message = "Operating start time must be before operating end time!")
    public boolean isValidOperatingTime() {
        if (operatingStartTime == null || operatingEndTime == null) return true;
        return operatingStartTime.isBefore(operatingEndTime);
    }

    @AssertTrue(message = "Usable capacity volume cannot exceed gross volume!")
    public boolean isValidCapacity() {
        if (grossVolume == null || usableCapacityVolume == null) return true;
        return usableCapacityVolume.compareTo(grossVolume) <= 0;
    }
}
