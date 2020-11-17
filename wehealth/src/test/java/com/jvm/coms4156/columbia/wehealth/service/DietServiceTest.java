package com.jvm.coms4156.columbia.wehealth.service;

import static com.jvm.coms4156.columbia.wehealth.common.Constants.*;
import static org.mockito.Mockito.when;

import com.jvm.coms4156.columbia.wehealth.dto.DietRecordDto;
import com.jvm.coms4156.columbia.wehealth.dto.UserIdDto;
import com.jvm.coms4156.columbia.wehealth.entity.*;
import com.jvm.coms4156.columbia.wehealth.exception.BadRequestException;
import com.jvm.coms4156.columbia.wehealth.exception.NotFoundException;
import com.jvm.coms4156.columbia.wehealth.repository.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class DietServiceTest {

  @InjectMocks
  private DietService dietService;

  @Mock
  private DBUserRepository dbUserRepoMock;

  @Mock
  private DietHistoryRepository dietHistoryRepoMock;

  @Mock
  private DietTypeRepository dietTypeRepoMock;

  @Mock
  private DietNutrientMappingRepository dietNutrientMappingRepoMock;

  private DBUser validUser(Long userId) {
    DBUser dbUser = new DBUser("1", "1");
    dbUser.setUserId(userId);
    return dbUser;
  }

  private DietType validDiet(Integer dietTypeId) {
    DietType dietType = new DietType();
    dietType.setDietTypeId(dietTypeId);
    dietType.setDietTypeName("test");
    return dietType;
  }


  private NutrientType validNutrient(Integer nutrientTypeId) {
    NutrientType nutrientType = new NutrientType();
    nutrientType.setNutrientTypeId(nutrientTypeId);
    nutrientType.setNutrientTypeName("test");
    nutrientType.setUnit("gram");
    return nutrientType;
  }

  private DietHistory validDietHistory(Integer dietHistoryId, Integer dietTypeId) {
    DietHistory dietHistory = new DietHistory();
    dietHistory.setDietHistoryId(dietHistoryId);
    dietHistory.setDietType(validDiet(dietTypeId));
    dietHistory.setUser(validUser(1L));
    return dietHistory;
  }

  private List<DietHistory> validDietHistoryList(Long userId) {
    List<DietHistory> dietHistoryList = new ArrayList<>();
    for (Integer i = 1; i < 10; ++i) {
      dietHistoryList.add(validDietHistory(i, 1));
    }
    return dietHistoryList;
  }

  private DietNutrientMapping validDietNutrientMapping(Integer dietNutrientMappingId,
                                                       Integer dietTypeId,
                                                       Integer nutrientTypeId) {
    DietNutrientMapping dietNutrientMapping = new DietNutrientMapping();
    dietNutrientMapping.setDietNutrientMappingId(dietNutrientMappingId);
    dietNutrientMapping.setDietType(validDiet(dietTypeId));
    dietNutrientMapping.setNutrientType(validNutrient(nutrientTypeId));
    dietNutrientMapping.setValue(100.0);
    return dietNutrientMapping;
  }

  private List<DietNutrientMapping> validDietNutrientMappingList(Integer dietTypeId) {
    List<DietNutrientMapping> dietNutrientMappingList = new ArrayList<>();
    dietNutrientMappingList.add(validDietNutrientMapping(1, 1, PROTEIN));
    dietNutrientMappingList.add(validDietNutrientMapping(2, 1, CALORIES));
    dietNutrientMappingList.add(validDietNutrientMapping(3, 1, FAT));
    dietNutrientMappingList.add(validDietNutrientMapping(4, 1, CARBS));
    return dietNutrientMappingList;
  }

  @Test
  public void addDietTypeTest() {
    dietService.addDietType(1, "test_diet");
  }

  @Test
  public void addDietRecordValidTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class)))
            .thenReturn(Optional.of(validUser(1L)));
    when(dietTypeRepoMock.findByDietTypeId(Mockito.any(Integer.class)))
            .thenReturn(Optional.of(validDiet(1)));
    DietRecordDto dietRecordDto = new DietRecordDto(1L, 1, "test", 10.0, "gram",
            10.0, 10.0, 10.0, 10.0);
    dietService.addDietRecordToDB(dietRecordDto);
  }

  @Test
  public void addDietRecordValid2Test() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class)))
            .thenReturn(Optional.of(validUser(1L)));
    when(dietTypeRepoMock.findByDietTypeId(Mockito.any(Integer.class)))
            .thenReturn(Optional.of(validDiet(1)));
    DietRecordDto dietRecordDto = new DietRecordDto(1L, 1, "test", 10.0,
            "pound", 10.0, 10.0, 10.0, 10.0);
    dietService.addDietRecordToDB(dietRecordDto);
  }

  @Test
  public void addDietRecordInvalidTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.empty());
    when(dietTypeRepoMock.findByDietTypeId(Mockito.any(Integer.class)))
            .thenReturn(Optional.of(validDiet(1)));
    DietRecordDto dietRecordDto = new DietRecordDto(1L, 1, "test", 10.0,
            "gram", 10.0, 10.0, 10.0, 10.0);
    Assertions.assertThrows(NotFoundException.class, () -> {
      dietService.addDietRecordToDB(dietRecordDto);
    });
  }

  @Test
  public void addDietRecordInvalid2Test() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class)))
            .thenReturn(Optional.of(validUser(1L)));
    when(dietTypeRepoMock.findByDietTypeId(Mockito.any(Integer.class)))
            .thenReturn(Optional.empty());
    DietRecordDto dietRecordDto = new DietRecordDto(1L, 1, "test", 10.0,
            "gram", 10.0, 10.0, 10.0, 10.0);
    Assertions.assertThrows(BadRequestException.class, () -> {
      dietService.addDietRecordToDB(dietRecordDto);
    });
  }

  @Test
  public void addDietRecordInvalid3Test() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class)))
            .thenReturn(Optional.of(validUser(1L)));
    when(dietTypeRepoMock.findByDietTypeId(Mockito.any(Integer.class)))
            .thenReturn(Optional.of(validDiet(1)));
    DietRecordDto dietRecordDto = new DietRecordDto(1L, 1, "test", 10.0,
            "random", 10.0, 10.0, 10.0, 10.0);
    Assertions.assertThrows(BadRequestException.class, () -> {
      dietService.addDietRecordToDB(dietRecordDto);
    });
  }

  @Test
  public void addDietNutrientMappingValidTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class)))
            .thenReturn(Optional.of(validUser(1L)));
    when(dietTypeRepoMock.findByDietTypeId(Mockito.any(Integer.class)))
            .thenReturn(Optional.of(validDiet(1)));
    DietRecordDto dietRecordDto = new DietRecordDto(1L, 1, "test", 10.0,
            "gram", 10.0, 10.0, 10.0, 10.0);
    dietService.addAllNutrientsInfoToDietNutrientMapping(dietRecordDto);
  }

  @Test
  public void addDietNutrientMappingInvalidTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class)))
            .thenReturn(Optional.of(validUser(1L)));
    when(dietTypeRepoMock.findByDietTypeId(Mockito.any(Integer.class)))
            .thenReturn(Optional.empty());
    DietRecordDto dietRecordDto = new DietRecordDto(1L, 1, "test", 10.0,
            "gram", 10.0, 10.0, 10.0, 10.0);
    Assertions.assertThrows(BadRequestException.class, () -> {
      dietService.addDietRecordToDB(dietRecordDto);
    });
  }

  @Test
  public void getDietHistoryValid() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class)))
            .thenReturn(Optional.of(validUser(1L)));
    when(dietHistoryRepoMock.findAllByUser(Mockito.any(DBUser.class)))
            .thenReturn(validDietHistoryList(1L));
    when(dietNutrientMappingRepoMock
            .findAllByDietTypeOrderByNutrientType(Mockito.any(DietType.class)))
            .thenReturn(validDietNutrientMappingList(1));

    UserIdDto userIdDto = new UserIdDto(1L);
    String unit = "all";
    Integer length = 1;
    dietService.getDietHistory(userIdDto, Optional.of(unit), Optional.of(length));
  }

  @Test
  public void getDietHistoryInvalidTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.empty());
    UserIdDto userIdDto = new UserIdDto(1L);
    String unit = "all";
    Integer length = 1;
    Assertions.assertThrows(BadRequestException.class, () -> {
      dietService.getDietHistory(userIdDto, Optional.of(unit), Optional.of(length));
    });
  }

  @Test
  public void getDietHistoryInvalid2Test() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.empty());
    UserIdDto userIdDto = new UserIdDto(1L);
    String unit = "all";
    Integer length = -1;
    Assertions.assertThrows(BadRequestException.class, () -> {
      dietService.getDietHistory(userIdDto, Optional.of(unit), Optional.of(length));
    });
  }

  @Test
  public void updateDietHistoryTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class)))
            .thenReturn(Optional.of(validUser(1L)));
    when(dietHistoryRepoMock.findByDietHistoryId(Mockito.any(Integer.class)))
            .thenReturn(Optional.of(validDietHistory(1, 1)));
    DietRecordDto dietRecordDto = new DietRecordDto(1L, 1, "test", 10.0,
            "gram", 10.0, 10.0, 10.0, 10.0);
    dietService.updateDietHistory(1, dietRecordDto);
  }

  @Test
  public void updateDietHistoryInvalidTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.empty());
    when(dietHistoryRepoMock.findByDietHistoryId(Mockito.any(Integer.class)))
            .thenReturn(Optional.of(validDietHistory(1, 1)));
    DietRecordDto dietRecordDto = new DietRecordDto(1L, 1, "test", 10.0,
            "gram", 10.0, 10.0, 10.0, 10.0);
    Assertions.assertThrows(BadRequestException.class, () -> {
      dietService.updateDietHistory(1, dietRecordDto);
    });

  }

  @Test
  public void updateDietHistoryInvalid2Test() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class)))
            .thenReturn(Optional.empty());
    when(dietHistoryRepoMock.findByDietHistoryId(Mockito.any(Integer.class)))
            .thenReturn(Optional.empty());
    DietRecordDto dietRecordDto = new DietRecordDto(1L, 1, "test", 10.0,
            "gram", 10.0, 10.0, 10.0, 10.0);
    Assertions.assertThrows(BadRequestException.class, () -> {
      dietService.updateDietHistory(1, dietRecordDto);
    });

  }


  @Test
  public void deleteDietHistoryValidTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class)))
            .thenReturn(Optional.of(validUser(1L)));
    when(dietHistoryRepoMock.findByDietHistoryId(Mockito.any(Integer.class)))
            .thenReturn(Optional.of(validDietHistory(1, 1)));
    UserIdDto userIdDto = new UserIdDto(1L);
    dietService.deleteDietHistory(1, userIdDto);
  }

  @Test
  public void deleteDietHistoryInvalidTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class)))
            .thenReturn(Optional.of(validUser(1L)));
    when(dietHistoryRepoMock.findByDietHistoryId(Mockito.any(Integer.class)))
            .thenReturn(Optional.empty());
    UserIdDto userIdDto = new UserIdDto(1L);
    Assertions.assertThrows(BadRequestException.class, () -> {
      dietService.deleteDietHistory(1, userIdDto);
    });
  }

  @Test
  public void deleteDietHistoryInvalid2Test() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class)))
            .thenReturn(Optional.empty());
    when(dietHistoryRepoMock.findByDietHistoryId(Mockito.any(Integer.class)))
            .thenReturn(Optional.empty());
    UserIdDto userIdDto = new UserIdDto(1L);
    Assertions.assertThrows(BadRequestException.class, () -> {
      dietService.deleteDietHistory(1, userIdDto);
    });
  }





}