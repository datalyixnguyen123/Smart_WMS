
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "storage_configuration", schema = "master")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class StorageConfig extends AuditedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "config_id", nullable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID configId;

    @NotBlank(message = "Configuration key is required!")
    @Size(max = 100, message = "Configuration key must be <= 100 characters!")
    @Column(name = "config_key", nullable = false, unique = true, length = 100)
    @ToString.Include
    private String configKey;

    @NotBlank(message = "Configuration value is required!")
    @Column(name = "config_value", nullable = false, columnDefinition = "text")
    private String configValue;

    public enum ConfigGroup {
        SYSTEM, INBOUND, OUTBOUND, INVENTORY, INTEGRATION
    }
    @NotNull(message = "Configuration group is required!")
    @Enumerated(EnumType.STRING)
    @Column(name = "config_group", nullable = false, length = 50)
    private ConfigGroup configGroup;

    public enum ValueType {
        STRING, INTEGER, DECIMAL, BOOLEAN
    }
    @NotNull(message = "Value type is required!")
    @Enumerated(EnumType.STRING)
    @Column(name = "value_type", nullable = false, length = 20)
    private ValueType valueType;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Builder.Default
    @Column(name = "is_editable", nullable = false)
    private boolean editable = true;
}


