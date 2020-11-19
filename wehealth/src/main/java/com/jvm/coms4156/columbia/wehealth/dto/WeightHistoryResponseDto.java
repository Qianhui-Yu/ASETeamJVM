package com.jvm.coms4156.columbia.wehealth.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeightHistoryResponseDto {

  private List<WeightHistoryDetailsDto> weightHistoryList;

}
