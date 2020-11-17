package com.jvm.coms4156.columbia.wehealth.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(schema = "wehealth", name = "nutrient_type")
@Data
public class NutrientType implements Serializable {
  @Id
  @Column(name = "nutrient_type_id")
  private int nutrientTypeId;

  @Column(name = "nutrient_type_name")
  private String nutrientTypeName;

  @Column(name = "unit")
  private String unit;

  @Column(name = "created_time")
  private String createdTime;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "updated_time")
  private String updatedTime;

  @Column(name = "updated_by")
  private String updatedBy;
}
