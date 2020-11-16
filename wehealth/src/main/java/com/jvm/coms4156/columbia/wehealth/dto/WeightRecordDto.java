package com.jvm.coms4156.columbia.wehealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeightRecordDto {
    private Integer userId;
    private Double weight;
    private String unit;
}
