
// Date created: 2026-08-19
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity;
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
@Table(name = "attribute", schema = "master")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class Attribute extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "attribute_id", nullable = false, updatable = false)
    private UUID attributeId;

    @NotBlank(message = "Attribute code is required")
    @Size(max = 50, message = "Attribute code must be <= 50 characters")
    @Column(name = "attribute_code", nullable = false, unique = true, length = 50)
    @ToString.Include
    private String attributeCode;

    @NotBlank(message = "Attribute name is required")
    @Size(max = 100, message = "Attribute name must be <= 100 characters")
    @Column(name = "attribute_name", nullable = false, length = 100)
    @ToString.Include
    private String attributeName;

    public enum DataType {
        STRING, INTEGER, DECIMAL, DATE, BOOLEAN, DATETIME, ENUM, JSON
    }
    @NotNull(message = "Data type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", nullable = false, length = 30)
    @ToString.Include
    private DataType dataType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Column(name = "is_required", nullable = false)
    private Boolean required = false;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

}
