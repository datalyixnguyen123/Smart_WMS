
// Date created: 2026-08-17
// Date last modified: 2026-08-17
// Author: Dat Nguyen
package com.datnguyen.smartwms.entity.location;
import com.datnguyen.smartwms.entity.base.AuditedEntity;
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
@Table(name = "storage_policy", schema = "master")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)

public class StoragePolicy extends AuditedEntity {
    @Id
    @NotNull(message = "Policy ID is required!")
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "policy_id", nullable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID policyId;

    @NotBlank(message = "Policy code is required!")
    @Size(max = 50, message = "Policy code must be <= 50 characters!")
    @Column(name = "policy_code", nullable = false, unique = true, length = 50)
    @ToString.Include
    private String policyCode;

    @Column(name = "policy_name", nullable = false, length = 100)
    private String policyName;

    public enum PickingStrategy {
        FIFO, LIFO, FEFO
    }
    @NotNull(message = "Picking strategy is required!")
    @Enumerated(EnumType.STRING)
    @Column(name = "picking_strategy", nullable = false, length = 50)
    private PickingStrategy pickingStrategy;

    public enum PutawayStrategy {
        FIXED_LOCATION, CONSOLIDATION, EMPTY_LOCATION, FAST_MOVING
    }
    @NotNull(message = "Putaway strategy is required!")
    @Enumerated(EnumType.STRING)
    @Column(name = "putaway_strategy", nullable = false, length = 50)
    private PutawayStrategy putawayStrategy;

    @NotNull(message = "Max utilization is required!")
    @DecimalMin(value = "0", inclusive = false, message = "Max utilization must be greater than 0%")
    @DecimalMax(value = "100", inclusive = true, message = "Max utilization must be less than or equal to 100%")
    @Column(name = "max_utilization", nullable = false, precision = 5, scale = 2)
    private BigDecimal maxUtilization;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
