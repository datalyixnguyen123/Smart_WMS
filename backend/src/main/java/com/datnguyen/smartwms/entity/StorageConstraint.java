
// Date created: 2026-08-17
// Date last modified: 2026-08-17
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
@Table(name = "storage_constraint", schema = "master")
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)

public class StorageConstraint extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "constraint_id", nullable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID constraintId;

    @NotBlank(message = "Constraint code is required!")
    @Size(max = 50, message = "Constraint code must be <= 50 characters!")
    @Column(name = "constraint_code", nullable = false, length = 50)
    @ToString.Include
    private String constraintCode;

    @NotBlank(message = "Constraint name is required!")
    @Size(max = 100, message = "Constraint name must be <= 100 characters!")
    @Column(name = "constraint_name", nullable = false, length = 100)
    private String constraintName;

    @Column(name = "temp_min", precision = 4, scale = 1)
    private BigDecimal tempMin;

    @Column(name = "temp_max", precision = 4, scale = 1)
    private BigDecimal tempMax;

    @DecimalMin(value = "0", inclusive = true, message = "Maximum humidity must be >= 0%")
    @DecimalMax(value = "100", inclusive = true, message = "Maximum humidity must be <= 100%")
    @Column(name = "humidity_max", precision = 4, scale = 1)
    private BigDecimal humidityMax;

    @DecimalMin(value = "0", inclusive = false, message = "Max stack weight must be greater than 0")
    @Column(name = "max_stack_weight", precision = 10, scale = 3)
    private BigDecimal maxStackWeight;

    @DecimalMin(value = "0", inclusive = false, message = "Max stack height must be greater than 0")
    @Column(name = "max_stack_height", precision = 10, scale = 3)
    private BigDecimal maxStackHeight;

    @Builder.Default
    @Column(name = "is_allowed_stacking", nullable = false)
    private boolean AllowedStacking = true;

    @Column(name = "special_instructions", columnDefinition = "text")
    private String specialInstructions;

    public enum StorageType {
        RACK, SHELF, BULK, BIN, FLOW_RACK, CANTILEVER, FLOOR, HAZMAT_RACK, SECURE_VAULT
    }
    @NotNull(message = "Storage type is required!")
    @Enumerated(EnumType.STRING)
    @Column(name = "storage_type", nullable = false, length = 30)
    private StorageType storageType;

    // cross-field validation
    @AssertTrue(message = "Minimum temperature cannot be greater than maximum temperature!")
    public boolean isValidTemperatureRange() {
        if (tempMin == null || tempMax == null) return true;
        return tempMin.compareTo(tempMax) <= 0;
    }
}
