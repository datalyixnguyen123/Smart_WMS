
// Date created: 2026-08-18
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity.location;
import com.datnguyen.smartwms.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "location", schema = "master")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class Location extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "location_id", nullable = false, updatable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID locationId;

    @NotBlank(message = "Location code is required!")
    @Size(max = 50, message = "Location code must be <= 50 characters!")
    @Column(name = "location_code", nullable = false, unique = true, length = 50)
    private String locationCode;

    @NotBlank(message = "Location name is required!")
    @Size(max = 255, message = "Location name must be <= 255 characters!")
    @Column(name = "location_name", nullable = false, length = 255)
    private String locationName;

    public enum LocationStatus{
        AVAILABLE, OCCUPIED, BLOCKED, MAINTENANCE, RESERVED, DAMAGED, FULL
    }
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "location_status", nullable = false, length = 30)
    private LocationStatus locationStatus = LocationStatus.AVAILABLE;

    @NotBlank(message = "Aisle code is required!")
    @Size(max = 20, message = "Aisle code must be <= 20 characters!")
    @Column(name = "aisle_code", nullable = false, length = 20)
    private String aisleCode;

    @NotBlank(message = "Rack code is required!")
    @Size(max = 20, message = "Rack code must be <= 20 characters!")
    @Column(name = "rack_code", nullable = false, length = 20)
    private String rackCode;

    @NotBlank(message = "Shelf code is required!")
    @Size(max = 20, message = "Shelf code must be <= 20 characters!")
    @Column(name = "shelf_code", nullable = false, length = 20)
    private String shelfCode;

    @NotBlank(message = "Bin code is required!")
    @Size(max = 20, message = "Bin code must be <= 20 characters!")
    @Column(name = "bin_code", nullable = false, length = 20)
    private String binCode;

    @DecimalMin(value = "0", inclusive = true, message = "X coordinate must be >= 0")
    @Column(name = "coord_x", precision = 8, scale = 2)
    private BigDecimal coordX;

    @DecimalMin(value = "0", inclusive = true, message = "Y coordinate must be >= 0")
    @Column(name = "coord_y", precision = 8, scale = 2)
    private BigDecimal coordY;

    @DecimalMin(value = "0", inclusive = true, message = "Z coordinate must be >= 0")
    @Column(name = "coord_z", precision = 8, scale = 2)
    private BigDecimal coordZ;

    @DecimalMin(value = "0", inclusive = true,message = "Capacity volume must be >= 0")
    @Column(name = "capacity_volume", precision = 12, scale = 4, nullable = false)
    private BigDecimal capacityVolume;

    @DecimalMin(value = "0", message = "Capacity weight must be >= 0")
    @Column(name = "capacity_weight", precision = 12, scale = 3, nullable = false)
    private BigDecimal capacityWeight;

    @DecimalMin(value = "0", message = "Occupied volume must be >= 0")
    @Column(name = "occupied_volume", precision = 12, scale = 4, nullable = false)
    private BigDecimal occupiedVolume;

    @DecimalMin(value = "0", message = "Occupied weight must be >= 0")
    @Column(name = "occupied_weight", precision = 12, scale = 4, nullable = false)
    private BigDecimal occupiedWeight;

    @Column(name = "is_pickable", nullable = false)
    private boolean isPickable = true;

    @Column(name = "is_putaway", nullable = false)
    private boolean isPutaway = true;

    @Column(name = "last_inventory_date")
    private LocalDate lastInventoryDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "zone_id", nullable = false, foreignKey = @ForeignKey(name = "fk_location_zone_id"))
    private Zone zone;
}
