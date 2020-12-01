package com.jvm.coms4156.columbia.wehealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietStatsDto {
  private Double avgCalories;
  private Double avgFat;
  private Double avgProtein;
  private Double avgCarbs;

  private Double avgDuration;
  private Double avgExerciseCal;

}
