
// auditable entities(created_by, updated_by etc.)
package com.datnguyen.smartwms.entity.base;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass

public abstract class AuditedEntity extends BaseEntity {
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;
}
