package com.jvm.coms4156.columbia.wehealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietHistoryDetailsDto {
    private Integer dietHistoryId;
    private String dietTypeName;
    private Double weight;
    private String unit;
    private String time;
    private Double totalProtein;
    private Double totalFat;
    private Double totalCarbs;
    private Double totalCalories;
}
