package com.jvm.coms4156.columbia.wehealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

  private List<DietByDayDto> dietByDate;
  private List<ExerciseByDayDto> exerciseByDate;
}
