package com.jvm.coms4156.columbia.wehealth.service;

import static org.mockito.Mockito.when;

import com.jvm.coms4156.columbia.wehealth.domain.AuthenticatedUser;
import com.jvm.coms4156.columbia.wehealth.dto.AdviceDto;
import com.jvm.coms4156.columbia.wehealth.dto.DietHistoryDetailsDto;
import com.jvm.coms4156.columbia.wehealth.dto.DietHistoryResponseDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
    dietHistory.setTotalProtein(10.0 * dietHistoryId);
    dietHistory.setTotalFat(5.0 * dietHistoryId);
    dietHistory.setTotalCarbs(5.0 * dietHistoryId);
    dietHistory.setTotalCalories(10.0 * dietHistoryId);
    return dietHistory;
  }

  private DietHistoryResponseDto getValidDiestHistory(Integer length) {
    List<DietHistoryDetailsDto> dietHistoryList = new ArrayList<>();
    DietHistoryResponseDto dietHistoryResponseDto = new DietHistoryResponseDto();
    for (int i = 0; i < length; i++) {
      dietHistoryList.add(validDietHistory(i, i));
    }
    dietHistoryResponseDto.setDietHistoryList(dietHistoryList);
    return dietHistoryResponseDto;
  }

  @Test
  public void getAdviceInvalidTest() {
    when(dietService.getDietHistory(Mockito.any(AuthenticatedUser.class),
            Mockito.any(Optional.class), Mockito.any(Optional.class)))
            .thenReturn(new DietHistoryResponseDto());
    AuthenticatedUser au = new AuthenticatedUser(1L);
    AdviceDto adviceDto = adviceService.getAdvice(au);
    Assertions.assertEquals(true, adviceDto.getIsEmpty());
  }

  @Test
  public void getAdviceValid1Test() {
    when(dietService.getDietHistory(Mockito.any(AuthenticatedUser.class),
            Mockito.any(Optional.class), Mockito.any(Optional.class)))
            .thenReturn(getValidDiestHistory(10));
    AuthenticatedUser au = new AuthenticatedUser(1L);
    AdviceDto adviceDto = adviceService.getAdvice(au);
    Assertions.assertEquals(false, adviceDto.getIsEmpty());
  }

  @Test
  public void getAdviceValid2Test() {
    when(dietService.getDietHistory(Mockito.any(AuthenticatedUser.class),
            Mockito.any(Optional.class), Mockito.any(Optional.class)))
            .thenReturn(getValidDiestHistory(1));
    AuthenticatedUser au = new AuthenticatedUser(1L);
    AdviceDto adviceDto = adviceService.getAdvice(au);
    Assertions.assertEquals(false, adviceDto.getIsEmpty());
  }

  @Test
  public void getAdviceValid3Test() {
    when(dietService.getDietHistory(Mockito.any(AuthenticatedUser.class),
            Mockito.any(Optional.class), Mockito.any(Optional.class)))
            .thenReturn(getValidDiestHistory(100));
    AuthenticatedUser au = new AuthenticatedUser(1L);
    AdviceDto adviceDto = adviceService.getAdvice(au);
    Assertions.assertEquals(false, adviceDto.getIsEmpty());
  }

}
