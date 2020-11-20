package com.jvm.coms4156.columbia.wehealth.service;

import com.jvm.coms4156.columbia.wehealth.domain.AuthenticatedUser;
import com.jvm.coms4156.columbia.wehealth.dto.AdviceDto;
import com.jvm.coms4156.columbia.wehealth.dto.DietHistoryDetailsDto;
import com.jvm.coms4156.columbia.wehealth.dto.DietHistoryResponseDto;
import com.jvm.coms4156.columbia.wehealth.dto.DietStatsDto;
import com.jvm.coms4156.columbia.wehealth.entity.*;
import com.jvm.coms4156.columbia.wehealth.repository.*;
import org.assertj.core.api.Assert;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.jvm.coms4156.columbia.wehealth.common.Constants.*;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.CARBS;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AdviceServiceTests {
  @InjectMocks
  private AdviceService adviceService;

  @Mock
  private DietService dietService;

  private DietHistoryDetailsDto validDietHistory(Integer dietHistoryId, Integer dietTypeId) {
    DietHistoryDetailsDto dietHistory = new DietHistoryDetailsDto();
    dietHistory.setDietHistoryId(dietHistoryId);
    dietHistory.setDietTypeId(dietTypeId);
    dietHistory.setTotalProtein(10.0);
    dietHistory.setTotalFat(10.0);
    dietHistory.setTotalCarbs(10.0);
    dietHistory.setTotalCalories(100.0);
    return dietHistory;
  }

  private DietHistoryResponseDto getValidDiestHistory(Integer length) {
    List<DietHistoryDetailsDto> dietHistoryList = new ArrayList<>();
    DietHistoryResponseDto dietHistoryResponseDto = new DietHistoryResponseDto();
    for (int i = 0; i < length; i++) {
      dietHistoryList.add(validDietHistory(i, 1));
    }
    dietHistoryResponseDto.setDietHistoryList(dietHistoryList);
    return dietHistoryResponseDto;
  }

  @Test
  public void getAdviceInvalidTest() {
    when(dietService.getDietHistory(Mockito.any(AuthenticatedUser.class), Mockito.any(Optional.class),
            Mockito.any(Optional.class))).thenReturn(new DietHistoryResponseDto());
    AuthenticatedUser au = new AuthenticatedUser(1L);
    AdviceDto adviceDto = adviceService.getAdvice(au);
    Assertions.assertEquals(true, adviceDto.getIsEmpty());
  }

  @Test
  public void getAdviceValid1Test() {
    when(dietService.getDietHistory(Mockito.any(AuthenticatedUser.class), Mockito.any(Optional.class),
            Mockito.any(Optional.class))).thenReturn(getValidDiestHistory(10));
    AuthenticatedUser au = new AuthenticatedUser(1L);
    AdviceDto adviceDto = adviceService.getAdvice(au);
    Assertions.assertEquals(false, adviceDto.getIsEmpty());
  }

  @Test
  public void getAdviceValid2Test() {
    when(dietService.getDietHistory(Mockito.any(AuthenticatedUser.class), Mockito.any(Optional.class),
            Mockito.any(Optional.class))).thenReturn(getValidDiestHistory(1));
    AuthenticatedUser au = new AuthenticatedUser(1L);
    AdviceDto adviceDto = adviceService.getAdvice(au);
    Assertions.assertEquals(false, adviceDto.getIsEmpty());
  }

}
