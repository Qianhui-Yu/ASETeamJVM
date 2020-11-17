package com.jvm.coms4156.columbia.wehealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeightHistoryDetailsDto {
    private Integer weightHistoryId;
    private Double weight;
    private String unit;
    private String time;
}
