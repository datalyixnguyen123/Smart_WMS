
// Date created: 2026-08-18
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
@Table(name = "product_category", schema = "master")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class ProductCategory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "category_id", nullable = false, updatable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID categoryId;

    @NotBlank(message = "Category code is required!")
    @Size(max = 50, message = "Category code must be <= 50 characters!")
    @Column(name = "category_code", nullable = false, unique = true, length = 50)
    private String categoryCode;

    @NotBlank(message = "Category name is required!")
    @Size(max = 100, message = "Category name must be <= 100 characters!")
    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    public enum CategoryStatus{
        ACTIVE, INACTIVE
    }
    @NotNull(message = "Category status is required!")
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "category_status", nullable = false, length = 30)
    private CategoryStatus status = CategoryStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id", foreignKey = @ForeignKey(name = "fk_product_category_parent"))
    private ProductCategory parentCategoryId;

}
