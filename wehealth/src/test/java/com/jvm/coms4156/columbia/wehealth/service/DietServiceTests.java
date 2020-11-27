package com.jvm.coms4156.columbia.wehealth.service;

import static com.jvm.coms4156.columbia.wehealth.common.Constants.CALORIES;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.CARBS;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.FAT;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.PROTEIN;
import static org.mockito.Mockito.when;

import com.jvm.coms4156.columbia.wehealth.domain.AuthenticatedUser;
import com.jvm.coms4156.columbia.wehealth.dto.DietRecordDto;
import com.jvm.coms4156.columbia.wehealth.entity.DbUser;
import com.jvm.coms4156.columbia.wehealth.entity.DietHistory;
import com.jvm.coms4156.columbia.wehealth.entity.DietNutrientMapping;
import com.jvm.coms4156.columbia.wehealth.entity.DietType;
import com.jvm.coms4156.columbia.wehealth.entity.NutrientType;
import com.jvm.coms4156.columbia.wehealth.exception.BadRequestException;
import com.jvm.coms4156.columbia.wehealth.exception.NotFoundException;
import com.jvm.coms4156.columbia.wehealth.repository.DbUserRepository;
import com.jvm.coms4156.columbia.wehealth.repository.DietHistoryRepository;
import com.jvm.coms4156.columbia.wehealth.repository.DietNutrientMappingRepository;
import com.jvm.coms4156.columbia.wehealth.repository.DietTypeRepository;
import com.jvm.coms4156.columbia.wehealth.repository.NutrientTypeRepository;
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
public class DietServiceTests {

  @InjectMocks
  private DietService dietService;

  @Mock
  private DbUserRepository dbUserRepoMock;

  @Mock
  private DietHistoryRepository dietHistoryRepoMock;

  @Mock
  private DietTypeRepository dietTypeRepoMock;

  @Mock
  private NutrientTypeRepository nutrientTypeRepoMock;

  @Mock
  private DietNutrientMappingRepository dietNutrientMappingRepoMock;

  private DbUser validUser(Long userId) {
    DbUser dbUser = new DbUser("1", "1");
    dbUser.setUserId(userId);
    return dbUser;
  }

  private AuthenticatedUser getValidAU() {
    AuthenticatedUser au = new AuthenticatedUser(1L, 1, "a");
    return au;
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
    for (Integer i = 1; i < 5; ++i) {
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
    AuthenticatedUser au = getValidAU();
    DietRecordDto dietRecordDto = new DietRecordDto(1, "test", 10.0, "gram",
            10.0, 10.0, 10.0, 10.0);
    dietService.addDietRecordToDb(au, dietRecordDto);
  }

  @Test
  public void addDietRecordValid2Test() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class)))
            .thenReturn(Optional.of(validUser(1L)));
    when(dietTypeRepoMock.findByDietTypeId(Mockito.any(Integer.class)))
            .thenReturn(Optional.of(validDiet(1)));
    AuthenticatedUser au = getValidAU();
    DietRecordDto dietRecordDto = new DietRecordDto(1, "test", 10.0,
            "pound", 10.0, 10.0, 10.0, 10.0);
    dietService.addDietRecordToDb(au, dietRecordDto);
  }

  @Test
  public void addDietRecordInvalidTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.empty());
    when(dietTypeRepoMock.findByDietTypeId(Mockito.any(Integer.class)))
            .thenReturn(Optional.of(validDiet(1)));
    AuthenticatedUser au = getValidAU();
    DietRecordDto dietRecordDto = new DietRecordDto(1, "test", 10.0,
            "gram", 10.0, 10.0, 10.0, 10.0);
    Assertions.assertThrows(NotFoundException.class, () -> {
      dietService.addDietRecordToDb(au, dietRecordDto);
    });
  }

  @Test
  public void addDietRecordInvalid2Test() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class)))
            .thenReturn(Optional.of(validUser(1L)));
    when(dietTypeRepoMock.findByDietTypeId(Mockito.any(Integer.class)))
            .thenReturn(Optional.empty());
    AuthenticatedUser au = getValidAU();
    DietRecordDto dietRecordDto = new DietRecordDto(1, "test", 10.0,
            "gram", 10.0, 10.0, 10.0, 10.0);
    Assertions.assertThrows(BadRequestException.class, () -> {
      dietService.addDietRecordToDb(au, dietRecordDto);
    });
  }

  @Test
  public void addDietRecordInvalid3Test() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class)))
            .thenReturn(Optional.of(validUser(1L)));
    when(dietTypeRepoMock.findByDietTypeId(Mockito.any(Integer.class)))
            .thenReturn(Optional.of(validDiet(1)));
    AuthenticatedUser au = getValidAU();
    DietRecordDto dietRecordDto = new DietRecordDto(1, "test", 10.0,
            "random", 10.0, 10.0, 10.0, 10.0);
    Assertions.assertThrows(BadRequestException.class, () -> {
      dietService.addDietRecordToDb(au, dietRecordDto);
    });
  }

  @Test
  public void addDietNutrientMappingValidTest() {
    when(nutrientTypeRepoMock.findByNutrientTypeId(Mockito.any(Integer.class)))
            .thenReturn(validNutrient(FAT));
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class)))
            .thenReturn(Optional.of(validUser(1L)));
    when(dietTypeRepoMock.findByDietTypeId(Mockito.any(Integer.class)))
            .thenReturn(Optional.of(validDiet(1)));

    DietRecordDto dietRecordDto = new DietRecordDto(1, "test", 10.0,
            "gram", 10.0, 10.0, 10.0, 10.0);
    dietService.addAllNutrientsInfoToDietNutrientMapping(dietRecordDto);
  }

  @Test
  public void addDietNutrientMappingInvalidTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class)))
            .thenReturn(Optional.of(validUser(1L)));
    when(dietTypeRepoMock.findByDietTypeId(Mockito.any(Integer.class)))
            .thenReturn(Optional.empty());
    AuthenticatedUser au = getValidAU();
    DietRecordDto dietRecordDto = new DietRecordDto(1, "test", 10.0,
            "gram", 10.0, 10.0, 10.0, 10.0);
    Assertions.assertThrows(BadRequestException.class, () -> {
      dietService.addDietRecordToDb(au, dietRecordDto);
    });
  }

  @Test
  public void getDietHistoryValid() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class)))
            .thenReturn(Optional.of(validUser(1L)));
    when(dietHistoryRepoMock.findAllByUser(Mockito.any(DbUser.class)))
            .thenReturn(validDietHistoryList(1L));
    when(dietNutrientMappingRepoMock
            .findAllByDietTypeOrderByNutrientType(Mockito.any(DietType.class)))
            .thenReturn(validDietNutrientMappingList(1));

    AuthenticatedUser au = new AuthenticatedUser(1L);
    String unit = "all";
    Integer length = 1;
    dietService.getDietHistory(au, Optional.of(unit), Optional.of(length));
  }

  @Test
  public void getDietHistoryInvalidTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.empty());
    AuthenticatedUser au = new AuthenticatedUser(1L);
    String unit = "all";
    Integer length = 1;
    Assertions.assertThrows(BadRequestException.class, () -> {
      dietService.getDietHistory(au, Optional.of(unit), Optional.of(length));
    });
  }

  @Test
  public void getDietHistoryInvalid2Test() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.empty());
    AuthenticatedUser au = new AuthenticatedUser(1L);
    String unit = "all";
    Integer length = -1;
    Assertions.assertThrows(BadRequestException.class, () -> {
      dietService.getDietHistory(au, Optional.of(unit), Optional.of(length));
    });
  }

  @Test
  public void updateDietHistoryTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class)))
            .thenReturn(Optional.of(validUser(1L)));
    when(dietHistoryRepoMock.findByDietHistoryId(Mockito.any(Integer.class)))
            .thenReturn(Optional.of(validDietHistory(1, 1)));
    AuthenticatedUser au = getValidAU();
    DietRecordDto dietRecordDto = new DietRecordDto(1, "test", 10.0,
            "gram", 10.0, 10.0, 10.0, 10.0);
    dietService.updateDietHistory(au, 1, dietRecordDto);
  }

  @Test
  public void updateDietHistoryInvalidTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.empty());
    when(dietHistoryRepoMock.findByDietHistoryId(Mockito.any(Integer.class)))
            .thenReturn(Optional.of(validDietHistory(1, 1)));
    AuthenticatedUser au = getValidAU();
    DietRecordDto dietRecordDto = new DietRecordDto(1, "test", 10.0,
            "gram", 10.0, 10.0, 10.0, 10.0);
    Assertions.assertThrows(BadRequestException.class, () -> {
      dietService.updateDietHistory(au, 1, dietRecordDto);
    });

  }

  @Test
  public void updateDietHistoryInvalid2Test() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class)))
            .thenReturn(Optional.empty());
    when(dietHistoryRepoMock.findByDietHistoryId(Mockito.any(Integer.class)))
            .thenReturn(Optional.empty());
    AuthenticatedUser au = getValidAU();
    DietRecordDto dietRecordDto = new DietRecordDto(1, "test", 10.0,
            "gram", 10.0, 10.0, 10.0, 10.0);
    Assertions.assertThrows(BadRequestException.class, () -> {
      dietService.updateDietHistory(au, 1, dietRecordDto);
    });

  }


  @Test
  public void deleteDietHistoryValidTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class)))
            .thenReturn(Optional.of(validUser(1L)));
    when(dietHistoryRepoMock.findByDietHistoryId(Mockito.any(Integer.class)))
            .thenReturn(Optional.of(validDietHistory(1, 1)));
    AuthenticatedUser au = getValidAU();
    dietService.deleteDietHistory(au, 1);
  }

  @Test
  public void deleteDietHistoryInvalidTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class)))
            .thenReturn(Optional.of(validUser(1L)));
    when(dietHistoryRepoMock.findByDietHistoryId(Mockito.any(Integer.class)))
            .thenReturn(Optional.empty());
    AuthenticatedUser au = getValidAU();
    Assertions.assertThrows(BadRequestException.class, () -> {
      dietService.deleteDietHistory(au, 1);
    });
  }

  @Test
  public void deleteDietHistoryInvalid2Test() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class)))
            .thenReturn(Optional.empty());
    when(dietHistoryRepoMock.findByDietHistoryId(Mockito.any(Integer.class)))
            .thenReturn(Optional.empty());
    AuthenticatedUser au = getValidAU();
    Assertions.assertThrows(BadRequestException.class, () -> {
      dietService.deleteDietHistory(au, 1);
    });
  }





}