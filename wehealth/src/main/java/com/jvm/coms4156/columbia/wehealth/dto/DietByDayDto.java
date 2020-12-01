package com.jvm.coms4156.columbia.wehealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietByDayDto {
  private String date;
  private Double totalCalories;
  private Double totalFat;
  private Double totalProtein;
  private Double totalCarbs;

}
