
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
@Table(name = "product", schema = "master")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class Product extends AuditedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id", nullable = false, updatable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID productId;

    @NotBlank(message = "Product code is required!")
    @Size(max = 50, message = "Product code must be <= 50 characters!")
    @Column(name = "product_code", nullable = false, unique = true, length = 50)
    private String productCode;

    @NotBlank(message = "Product name is required!")
    @Size(max = 255, message = "Product name must be <= 255 characters!")
    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @NotBlank(message = "Country of origin is required!")
    @Size(max = 100, message = "Country of origin must be <= 100 characters!")
    @Column(name = "country_of_origin", nullable = false, length = 100)
    private String countryOfOrigin;

    @NotBlank(message = "Manufacturer is required!")
    @Size(max = 100, message = "Manufacturer must be <= 100 characters!")
    @Column(name = "manufacturer", nullable = false, length = 100)
    private String manufacturer;

    @NotBlank(message = "HS code is required!")
    @Size(max = 50, message = "HS code must be <= 50 characters!")
    @Column(name = "hs_code", nullable = false, length = 50)
    private String hsCode;

    public enum ProductStatus {
        ACTIVE, INACTIVE, DISCONTINUED
    }
    @NotNull(message = "Product status is required!")
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "product_status", nullable = false, length = 30)
    private ProductStatus status = ProductStatus.ACTIVE;

    @PositiveOrZero(message = "Shelf life days must be >= 0")
    @Column(name = "shelf_life_days")
    private Integer shelfLifeDays;

    @Column(name = "image_url", columnDefinition = "text")
    private String imageUrl;

    @NotNull(message = "Product category is required!")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_product_category_id"))
    private ProductCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", foreignKey = @ForeignKey(name = "fk_product_brand_id"))
    private ProductBrand brand;

}
