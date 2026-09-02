
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
@Table(name = "sku_attribute", schema = "master", uniqueConstraints = {@UniqueConstraint(name = "uk_sku_attribute", columnNames = {"sku_id", "attribute_id"})})
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class SkuAttribute extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ToString.Include
    @EqualsAndHashCode.Include
    @Column(name = "sku_attribute_id", nullable = false, updatable = false)
    private UUID skuAttributeId;

    @ToString.Include
    @Size(max = 255, message = "Attribute value must be <= 255 characters!")
    @Column(name = "attribute_value", length = 255)
    private String attributeValue;

    @NotNull(message = "Attribute is required!")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attribute_id", nullable = false, foreignKey = @ForeignKey(name = "fk_sku_attribute_attribute_id"))
    private Attribute attribute;

    @NotNull(message = "Sku is required!")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku_id", nullable = false, foreignKey = @ForeignKey(name = "fk_sku_attribute_sku_id"))
    private Sku sku;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_option_id", foreignKey = @ForeignKey(name = "fk_sku_attribute_attribute_option_id"))
    private AttributeOption attributeOption;

    @AssertTrue(message = "Invalid attribute value configuration!")
    private boolean isValid() {
        if(attribute == null) return true;
        if(attribute.getDataType() == Attribute.DataType.ENUM) return attributeOption != null && attributeValue == null;
        return attributeOption == null && attributeValue != null && !attributeValue.isEmpty();
    }
}




