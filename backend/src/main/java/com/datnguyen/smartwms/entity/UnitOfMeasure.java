
// Date created: 2026-08-17
// Date last modified: 2026-08-17
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "unit_of_measure", schema = "master")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false) // for ID hashing

public class UnitOfMeasure extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uom_id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID uomId;

    @NotBlank(message = "Unit of measure code is required!")
    @Size(max = 20, message = "Unit of measure code must be <= 20 characters!")
    @Column(name = "uom_code", length = 20, nullable = false, unique = true)
    @ToString.Include
    private String uomCode;

    @NotBlank(message = "Unit of measure name is required!")
    @Size(max = 100, message = "Unit of measure name must be <= 100 characters!")
    @Column(name = "uom_name", length = 100, nullable = false)
    private String uomName;

    public enum UomType {
        WEIGHT, VOLUME, LENGTH, QUANTITY, DIMENSION, TIME, AREA, TEMPERATURE
    }
    @NotNull(message = "Unit of measure type is required!")
    @Enumerated(EnumType.STRING)
    @Column(name = "uom_type", length = 30, nullable = false)
    private UomType uomType;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "symbol", length = 10)
    private String symbol;

    @Column(name = "decimal_places")
    private Integer decimalPlaces;

    @Column(name = "description", length = 255)
    private String description;
}


