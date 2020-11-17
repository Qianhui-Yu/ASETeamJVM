package com.jvm.coms4156.columbia.wehealth.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExerciseHistoryResponseDto {

  private List<ExerciseHistoryDetailsDto> exerciseHistoryList;

  public ExerciseHistoryResponseDto() {
    this.exerciseHistoryList = new ArrayList<>();
  }

}
