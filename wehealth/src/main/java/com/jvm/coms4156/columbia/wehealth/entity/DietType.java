package com.jvm.coms4156.columbia.wehealth.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Table(schema = "wehealth", name = "diet_type")
@Data
public class DietType {
    @Id
    @Column(name = "diet_type_id")
    private int dietTypeId;

    @Column(name = "diet_type_name")
    private String dietTypeName;

    @Column(name = "created_time")
    private String createdTime;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_time")
    private String updatedTime;

    @Column(name = "updated_by")
    private String updatedBy;
}
