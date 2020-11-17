package com.jvm.coms4156.columbia.wehealth.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DietHistoryResponseDto {

  private List<DietHistoryDetailsDto> dietHistoryList;

  public DietHistoryResponseDto() {
    this.dietHistoryList = new ArrayList<>();
  }

}
