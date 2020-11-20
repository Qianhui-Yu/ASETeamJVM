package com.jvm.coms4156.columbia.wehealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietRecordDto {
  //private String time;
  private Integer dietTypeId;
  private String dietTypeName;
  private Double weight;
  private String unit;
  private Double protein;
  private Double fat;
  private Double carbs;
  private Double calories;
}
