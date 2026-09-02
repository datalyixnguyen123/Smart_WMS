
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
@Table(name = "product_brand", schema = "master")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class ProductBrand extends BaseEntity {
    @Id
    @NotNull(message = "Brand ID is required!")
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "brand_id", nullable = false, updatable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID brandId;

    @NotBlank(message = "Brand code is required!")
    @Size(max = 50, message = "Brand code must be <= 50 characters!")
    @Column(name = "brand_code", nullable = false, unique = true, length = 50)
    private String brandCode;

    @NotBlank(message = "Brand name is required!")
    @Size(max = 255, message = "Brand name must be <= 255 characters!")
    @Column(name = "brand_name", nullable = false, length = 255)
    private String brandName;

    @Column(name = "logo_url", columnDefinition = "text")
    private String logoUrl;

    @Column(name = "website_url", columnDefinition = "text")
    private String websiteUrl;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
