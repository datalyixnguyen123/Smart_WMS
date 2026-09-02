
// Date created: 2026-08-31
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity.inventory;

import com.datnguyen.smartwms.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "discrepancy_reason", schema = "inventory")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class DiscrepancyReason extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "reason_id", nullable = false, updatable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID reasonId;

    @NotNull(message = "Reason code is required!")
    @Size(max = 50, message = "Reason code must be <= 50 characters!")
    @Column(name = "reason_code", nullable = false, unique = true, length = 50)
    private String reasonCode;

    @NotNull(message = "Reason name is required!")
    @Size(max = 100, message = "Reason name must be <= 100 characters!")
    @Column(name = "reason_name", nullable = false, unique = true, length = 100)
    private String reasonName;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;
}


