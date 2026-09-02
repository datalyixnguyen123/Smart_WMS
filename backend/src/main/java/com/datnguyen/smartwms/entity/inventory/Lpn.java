

// Date created: 2026-08-31
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity.inventory;

import com.datnguyen.smartwms.entity.base.BaseEntity;
import com.datnguyen.smartwms.entity.location.Location;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "lpn", schema = "inventory")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class Lpn extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "lpn_id", nullable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID lpnId;

    @Column(name = "lpn_code", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Lpn code is required!")
    @Size(max = 50, message = "Lpn code must be <= 50 characters!")
    @ToString.Include
    private String lpnCode;

    public enum LpnStatus {
        AVAILABLE, ALLOCATED, STAGED, IN_TRANSIT
    }
    @Column(name = "lpn_status", nullable = false, length = 50)
    @NotNull(message = "Lpn status is required!")
    @Size(max = 50, message = "Lpn status must be <= 50 characters!")
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private LpnStatus lpnStatus = LpnStatus.AVAILABLE;

    public enum LpnType {
        PALLET, BOX, TOTE
    }
    @Column(name = "lpn_type", nullable = false, length = 30)
    @NotNull(message = "Lpn type is required!")
    @Size(max = 30, message = "Lpn type must be <= 30 characters!")
    @Enumerated(EnumType.STRING)
    private LpnType lpnType;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "location_id", foreignKey = @ForeignKey(name = "fk_lpn_location_id"))
    private Location location;
}
