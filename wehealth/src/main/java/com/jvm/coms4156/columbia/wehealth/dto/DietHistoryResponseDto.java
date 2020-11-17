package com.jvm.coms4156.columbia.wehealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class DietHistoryResponseDto {

    private List<DietHistoryDetailsDto> dietHistoryList;

    public DietHistoryResponseDto() {
        this.dietHistoryList = new ArrayList<>();
    }

}
