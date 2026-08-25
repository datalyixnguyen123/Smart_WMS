
// Date created: 2026-08-17
// Date last modified: 2026-08-17
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity;
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
@Table(name = "velocity_class", schema = "master")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class VelocityClass extends BaseEntity {
    @Id
    @NotNull(message = "Velocity code is required!")
    @Column(name = "velocity_code", nullable = false, length = 20)
    @ToString.Include
    @EqualsAndHashCode.Include
    private String velocityCode;

    @NotBlank(message = "Display name is required!")
    @Size(max = 50, message = "Display name must be <= 50 characters!")
    @Column(name = "display_name", nullable = false, length = 50)
    @ToString.Include
    private String displayName;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @DecimalMin(value = "0", inclusive = false, message = "Min daily pick must be > 0")
    @Column(name = "min_daily_pick", precision = 10, scale = 2)
    private BigDecimal minDailyPick;

    @DecimalMin(value = "0", inclusive = false, message = "Max daily pick must be > 0")
    @Column(name = "max_daily_pick", precision = 10, scale = 2)
    private BigDecimal maxDailyPick;

    @DecimalMin(value = "0", inclusive = false, message = "Min monthly pick must be > 0")
    @Column(name = "min_monthly_pick", precision = 10, scale = 2)
    private BigDecimal minMonthlyPick;

    @DecimalMin(value = "0", inclusive = false, message = "Max monthly pick must be > 0")
    @Column(name = "max_monthly_pick", precision = 10, scale = 2)
    private BigDecimal maxMonthlyPick;

    @Min(value = 1, message = "Priority must be greater than 0")
    @Column(name = "priority", nullable = false)
    private Short priority;

    @Column(name = "color_code", length = 20)
    private String colorCode;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @AssertTrue(message = "Minimum daily pick cannot be greater than maximum daily pick!")
    public boolean isValidDailyPickRange() {
        if (minDailyPick == null || maxDailyPick == null) return true;
        return minDailyPick.compareTo(maxDailyPick) <= 0;
    }

    @AssertTrue(message = "Minimum monthly pick cannot be greater than maximum monthly pick!")
    public boolean isValidMonthlyPickRange() {
        if (minMonthlyPick == null || maxMonthlyPick == null) return true;
        return minMonthlyPick.compareTo(maxMonthlyPick) <= 0;
    }
}
