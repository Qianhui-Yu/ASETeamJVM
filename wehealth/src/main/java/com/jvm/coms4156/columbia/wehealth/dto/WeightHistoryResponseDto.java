package com.jvm.coms4156.columbia.wehealth.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class WeightHistoryResponseDto {

  private List<WeightHistoryDetailsDto> weightHistoryList;

  public WeightHistoryResponseDto() {
    this.weightHistoryList = new ArrayList<>();
  }

}
