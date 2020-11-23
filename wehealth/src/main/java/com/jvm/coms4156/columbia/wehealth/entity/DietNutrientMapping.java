package com.jvm.coms4156.columbia.wehealth.entity;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Data;

@Entity
@Table(schema = "wehealth", name = "diet_nutrient_mapping")

@Data
public class DietNutrientMapping implements Serializable {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Column(name = "diet_nutrient_mapping_id")
  private int dietNutrientMappingId;

  @JoinColumn(name = "diet_type_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private DietType dietType;

  @JoinColumn(name = "nutrient_type_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private NutrientType nutrientType;

  @Column(name = "value")
  private double value;

  @Column(name = "created_time")
  private String createdTime;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "updated_time")
  private String updatedTime;

  @Column(name = "updated_by")
  private String updatedBy;
}
