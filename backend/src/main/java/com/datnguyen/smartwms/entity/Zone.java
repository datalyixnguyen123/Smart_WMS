

// Date created: 2026-08-18
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
@Table(name = "zone", schema = "master", uniqueConstraints = {
    @UniqueConstraint(name = "uk_zone_warehouse_code", columnNames = {"warehouse_id", "zone_code"})
})
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class Zone extends BaseEntity {
    @Id
    @NotNull(message = "Zone ID is required!")
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "zone_id", nullable = false, updatable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID zoneId;

    @NotBlank(message = "Zone code is required!")
    @Size(max = 50, message = "Zone code must be <= 50 characters!")
    @Column(name = "zone_code", nullable = false, length = 50)
    @ToString.Include
    private String zoneCode;

    @NotBlank(message = "Zone name is required!")
    @Size(max = 255, message = "Zone name must be <= 255 characters!")
    @Column(name = "zone_name", nullable = false, length = 255)
    private String zoneName;

    public enum ZoneType{
        RECEIVING, STORAGE, PICKING, PACKING, SHIPPING, RETURN
    }
    @NotBlank(message = "Zone type is required!")
    @Size(max = 30, message = "Zone type must be <= 30 characters!")
    @Enumerated(EnumType.STRING)
    @Column(name = "zone_type", nullable = false, length = 30)
    private ZoneType zoneType;

    public enum ZoneStatus{
        ACTIVE, LOCKED, MAINTENANCE
    }
    @NotBlank(message = "Zone status is required!")
    @Size(max = 30, message = "Zone status must be <= 30 characters!")
    @Enumerated(EnumType.STRING)
    @Column(name = "zone_status", nullable = false, length = 30)
    private ZoneStatus zoneStatus = ZoneStatus.ACTIVE;

    public enum TemperatureType{
        AMBIENT, CHILLED, FROZEN, DEEP_FROZEN, CONTROLLED
    }
    @NotBlank(message = "Temperature type is required!")
    @Size(max = 50, message = "Temperature type must be <= 50 characters!")
    @Enumerated(EnumType.STRING)
    @Column(name = "temperature_type", nullable = false, length = 50)
    private TemperatureType temperatureType = TemperatureType.AMBIENT;

    @Column(name = "default_temperature", precision = 5, scale = 2)
    private BigDecimal defaultTemperature;

    @Builder.Default
    @Column(name = "allow_mixing", nullable = false)
    private boolean allowMixing = false;

    @Min(value = 1, message = "Priority must be >= 0")
    @Column(name = "priority")
    private Short priority;

    @NotNull(message = "Warehouse is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false, foreignKey = @ForeignKey(name = "fk_zone_warehouse_id"))
    private Warehouse warehouse;

    @NotNull(message = "Storage constraint is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "constraint_id", nullable = false, foreignKey = @ForeignKey(name = "fk_zone_constraint_id"))
    private StorageConstraint storageConstraint;
}
