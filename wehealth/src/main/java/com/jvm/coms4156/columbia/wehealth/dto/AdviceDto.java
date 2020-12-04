package com.jvm.coms4156.columbia.wehealth.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdviceDto {
  private String caloriesAdvice;
  private String fatAdvice;
  private String proteinAdvice;
  private String carbsAdvice;
  private String exerciseAdvice;
  private Boolean isEmpty;

  private Double avgCalories;
  private Double avgFat;
  private Double avgProtein;
  private Double avgCarbs;
  private Double avgDuration;
  private Double avgExerciseCal;

  private List<DietByDayDto> dietByDate;
  private List<ExerciseByDayDto> exerciseByDate;
  private List<WeightHistoryDetailsDto> weightByDate;
}
