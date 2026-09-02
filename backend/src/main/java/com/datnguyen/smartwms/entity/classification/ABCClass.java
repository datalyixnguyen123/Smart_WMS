
// Date created: 2026-08-17
// Date last modified: 2026-08-17
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity.classification;
import com.datnguyen.smartwms.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "abc_class", schema = "master")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class ABCClass extends BaseEntity {
    @Id
    @NotNull(message = "ABC code is required!")
    @Column(name = "abc_code", nullable = false, length = 10)
    @ToString.Include
    @EqualsAndHashCode.Include
    private String abcCode;

    @NotBlank(message = "Display name is required!")
    @Size(max = 50, message = "Display name must be <= 50 characters!")
    @Column(name = "display_name", nullable = false, length = 50)
    @ToString.Include
    private String displayName;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @DecimalMin(value = "0", inclusive = true, message = "Min percentage must be >= 0%")
    @DecimalMax(value = "100", inclusive = true, message = "Min percentage must be <= 100%")
    @Column(name = "min_percentage", precision = 5, scale = 2)
    private BigDecimal minPercentage;

    @DecimalMin(value = "0", inclusive = true, message = "Max percentage must be >= 0%")
    @DecimalMax(value = "100", inclusive = true, message = "Max percentage must be <= 100%")
    @Column(name = "max_percentage", precision = 5, scale = 2)
    private BigDecimal maxPercentage;

    @Min(value = 1, message = "Priority must be greater than 0!")
    @Column(name = "priority", nullable = false)
    private Short priority;

    @Column(name = "color_code", length = 20)
    private String colorCode;

    @Min(value = 1, message = "Cycle count frequency days must be greater than 0!")
    @Column(name = "cycle_count_frequency_days", nullable = false)
    private Integer cycleCountFrequencyDays;

    @DecimalMin(value = "0", inclusive = true, message = "Target service level must be >= 0%")
    @DecimalMax(value = "100", inclusive = true, message = "Target service level must be <= 100%")
    @Column(name = "target_service_level", precision = 5, scale = 2)
    private BigDecimal targetServiceLevel;

    @Min(value = 0, message = "Max pick distance must be greater than or equal to 0!")
    @Column(name = "max_pick_distance")
    private Integer maxPickDistance;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @AssertTrue(message = "Minimum percentage cannot be greater than maximum percentage!")
    public boolean isValidPercentageRange() {
        if (minPercentage == null || maxPercentage == null) return true;
        return minPercentage.compareTo(maxPercentage) <= 0;
    }

}
