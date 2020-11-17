package com.jvm.coms4156.columbia.wehealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseHistoryDetailsDto {
  private Integer exerciseHistoryId;
  private String exerciseTypeName;
  private Double duration;
  private String time;
  private Double totalCalories;
}
