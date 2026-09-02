
// Date created: 2026-08-22
// Author: Dat Nguyen

package com.datnguyen.smartwms.entity.partner;
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
@Table(name = "business_partner", schema = "master")
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

public class BusinessPartner extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "partner_id", nullable = false, updatable = false)
    @ToString.Include
    @EqualsAndHashCode.Include
    private UUID partnerId;

    @NotBlank(message = "Partner code is required!")
    @Size(max = 50, message = "Partner code must be <= 50 characters!")
    @Column(name = "partner_code", nullable = false, unique = true, length = 50)
    private String partnerCode;

    @NotBlank(message = "Partner name is required!")
    @Size(max = 255, message = "Partner name must be <= 255 characters!")
    @Column(name = "partner_name", nullable = false, length = 255)
    private String partnerName;

    public enum PartnerType {
        SUPPLIER, CARRIER, CUSTOMER, BOTH
    }
    @NotNull(message = "Partner type is required!")
    @Enumerated(EnumType.STRING)
    @Column(name = "partner_type", nullable = false, length = 50)
    private PartnerType partnerType;

    @Column(name = "tax_code", length = 50)
    private String taxCode;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "delivery_address", columnDefinition = "text")
    private String deliveryAddress;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}


