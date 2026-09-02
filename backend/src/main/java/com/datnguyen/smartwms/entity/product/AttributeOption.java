
// Date created: 2026-08-19
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity.product;
import com.datnguyen.smartwms.entity.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "attribute_option", schema = "master", uniqueConstraints = {@UniqueConstraint(name = "uk_attribute_option", columnNames = {"attribute_id", "option_code"}), @UniqueConstraint(name = "uk_attribute_display_name", columnNames = {"attribute_id", "display_name"})})
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class AttributeOption extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ToString.Include
    @Column(name = "option_id", nullable = false, updatable = false)
    private UUID optionId;

    @ToString.Include
    @NotBlank(message = "Option code is required")
    @Size(max = 50, message = "Option code must be <= 50 characters")
    @Column(name = "option_code", nullable = false, length = 50)
    private String optionCode;

    @NotBlank(message = "Display name is required")
    @Size(max = 100, message = "Display name must be <= 100 characters")
    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @NotNull(message = "Display order is required")
    @PositiveOrZero(message = "Display order must be >= 0")
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @NotNull(message = "Attribute is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false, foreignKey = @ForeignKey(name = "fk_option_attribute_id"))
    private Attribute attribute;
}


