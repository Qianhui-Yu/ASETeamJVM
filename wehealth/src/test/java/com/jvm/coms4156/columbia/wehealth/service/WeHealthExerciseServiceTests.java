package com.jvm.coms4156.columbia.wehealth.service;

import static com.jvm.coms4156.columbia.wehealth.common.Constants.MONTH;
import static org.mockito.Mockito.when;

import com.jvm.coms4156.columbia.wehealth.dto.ExerciseHistoryDetailsDto;
import com.jvm.coms4156.columbia.wehealth.dto.ExerciseHistoryResponseDto;
import com.jvm.coms4156.columbia.wehealth.dto.ExerciseRecordDto;
import com.jvm.coms4156.columbia.wehealth.dto.UserIdDto;
import com.jvm.coms4156.columbia.wehealth.entity.DbUser;
import com.jvm.coms4156.columbia.wehealth.entity.ExerciseHistory;
import com.jvm.coms4156.columbia.wehealth.entity.ExerciseType;
import com.jvm.coms4156.columbia.wehealth.exception.BadAuthException;
import com.jvm.coms4156.columbia.wehealth.exception.BadRequestException;
import com.jvm.coms4156.columbia.wehealth.exception.MissingDataException;
import com.jvm.coms4156.columbia.wehealth.exception.NotFoundException;
import com.jvm.coms4156.columbia.wehealth.repository.DbUserRepository;
import com.jvm.coms4156.columbia.wehealth.repository.ExerciseHistoryRepository;
import com.jvm.coms4156.columbia.wehealth.repository.ExerciseTypeRepository;
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
public class WeHealthExerciseServiceTests {

  @InjectMocks
  private ExerciseService exerciseService;

  @Mock
  private DbUserRepository dbUserRepoMock;

  @Mock
  private ExerciseHistoryRepository dbExerciseHistoryRepoMock;

  @Mock
  private ExerciseTypeRepository dbExerciseTypeRepoMock;

  private DbUser validUser(Long userId) {
    DbUser dbUser = new DbUser("1", "1");
    dbUser.setUserId(userId);
    return dbUser;
  }


  private List<ExerciseHistory> validExerciseHistoryList() {
    List<ExerciseHistory> exerciseHistoryList = new ArrayList<>();
    for (long i = 1; i < 10; ++i) {
      exerciseHistoryList.add(validExerciseHistory(i).get());
    }
    return exerciseHistoryList;
  }

  private Optional<ExerciseHistory> validExerciseHistory(Long userId) {
    ExerciseHistory exerciseHistory = new ExerciseHistory();
    DbUser dbUser = validUser(userId);
    exerciseHistory.setUser(dbUser);
    exerciseHistory.setExerciseType(new ExerciseType());
    return Optional.of(exerciseHistory);
  }

  @Test
  public void validateUserInvalidUserTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.empty());
    Assertions.assertThrows(NotFoundException.class, () ->
            exerciseService.validateUser(1L, Optional.of(2L))
    );
  }

  @Test
  public void validateUserTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.of(validUser(1L)));
    exerciseService.validateUser(1L, Optional.of(1L));
  }


  @Test
  public void validateUserDifferentUserTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.of(validUser(1L)));
    Assertions.assertThrows(BadAuthException.class, () ->
            exerciseService.validateUser(1L, Optional.of(2L))
    );
  }

  @Test
  public void addExerciseRecordToDBInvalidTypeTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.of(validUser(1L)));
    when(dbExerciseTypeRepoMock.findByExerciseTypeName(Mockito.any(String.class))).thenReturn(Optional.empty());
    ExerciseRecordDto exerciseRecordDto = new ExerciseRecordDto(1L, "TestExerciseType", 1000.0);
    Assertions.assertThrows(MissingDataException.class, () ->
            exerciseService.addExerciseRecordToDb(exerciseRecordDto)
    );
  }

  @Test
  public void addExerciseRecordToDBTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.of(validUser(1L)));
    when(dbExerciseTypeRepoMock.findByExerciseTypeName(Mockito.any(String.class))).thenReturn(Optional.of(new ExerciseType()));
    ExerciseRecordDto exerciseRecordDto = new ExerciseRecordDto(1L, "TestExerciseType", 1000.0);
    exerciseService.addExerciseRecordToDb(exerciseRecordDto);
  }

  @Test
  public void getExerciseHistoryNegativeDurationTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.of(validUser(1L)));
    Assertions.assertThrows(BadRequestException.class, () ->
            exerciseService.getExerciseHistory(new UserIdDto(1L), Optional.empty(), Optional.of(-100))
    );
  }

  @Test
  public void getExerciseHistoryAllTest() {
    DbUser user = validUser(1L);
    List<ExerciseHistory> historyDetailList = validExerciseHistoryList();
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.of(validUser(1L)));
    when(dbExerciseHistoryRepoMock.findAllByUser(user)).thenReturn(historyDetailList);
    ExerciseHistoryResponseDto retDto = exerciseService.getExerciseHistory(new UserIdDto(1L), Optional.empty(), Optional.empty());
    List<ExerciseHistoryDetailsDto> historyDetailDtoList = retDto.getExerciseHistoryList();
    Assertions.assertEquals(historyDetailDtoList.size(), historyDetailList.size());
    for (int i = 0; i < historyDetailDtoList.size(); ++i) {
      Assertions.assertEquals(historyDetailDtoList.get(i).getExerciseHistoryId(), historyDetailList.get(i).getExerciseHistoryId());
    }
  }

  @Test
  public void getExerciseHistoryDurationTest() {
    List<ExerciseHistory> historyDetailList = validExerciseHistoryList();
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.of(validUser(1L)));
    when(dbExerciseHistoryRepoMock.findAllByUserAndCreatedTimeAfter(Mockito.any(DbUser.class), Mockito.any(String.class))).thenReturn(historyDetailList);
    ExerciseHistoryResponseDto retDto = exerciseService.getExerciseHistory(new UserIdDto(1L), Optional.of(MONTH), Optional.of(1));
    List<ExerciseHistoryDetailsDto> historyDetailDtoList = retDto.getExerciseHistoryList();
    Assertions.assertEquals(historyDetailList.size(), historyDetailDtoList.size());
    for (int i = 0; i < historyDetailDtoList.size(); ++i) {
      Assertions.assertEquals(historyDetailDtoList.get(i).getExerciseHistoryId(), historyDetailList.get(i).getExerciseHistoryId());
    }
  }

  @Test
  public void editExerciseRecordInvalidExerciseIdTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.of(validUser(1L)));
    when(dbExerciseHistoryRepoMock.findByExerciseHistoryId(Mockito.any(Integer.class))).thenReturn(Optional.empty());
    Assertions.assertThrows(MissingDataException.class, () ->
            exerciseService.editExerciseRecordAtDb(Optional.of(1), new ExerciseRecordDto())
    );
  }

  @Test
  public void editExerciseRecordInvalidTypeNameTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.of(validUser(1L)));
    when(dbExerciseHistoryRepoMock.findById(Mockito.any(Integer.class))).thenReturn(validExerciseHistory(1L));
    when(dbExerciseTypeRepoMock.findByExerciseTypeName(Mockito.any(String.class))).thenReturn(Optional.empty());
    Assertions.assertThrows(MissingDataException.class, () -> {
      ExerciseRecordDto newRecord = new ExerciseRecordDto();
      newRecord.setUserId(1L);
      newRecord.setExerciseTypeName("TestExerciseType");
      exerciseService.editExerciseRecordAtDb(Optional.of(1), newRecord);
    });
  }

  @Test
  public void editExerciseRecordTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.of(validUser(1L)));
    when(dbExerciseHistoryRepoMock.findByExerciseHistoryId(Mockito.any(Integer.class))).thenReturn(validExerciseHistory(1L));
    when(dbExerciseHistoryRepoMock.save(Mockito.any(ExerciseHistory.class))).thenReturn(validExerciseHistory(1L).get());
    when(dbExerciseTypeRepoMock.findByExerciseTypeName(Mockito.any(String.class))).thenReturn(Optional.of(new ExerciseType()));
    exerciseService.editExerciseRecordAtDb(Optional.of(1), new ExerciseRecordDto(1L, "TestExerciseType", 10.0));
  }

  @Test
  public void deleteExerciseRecordInvalidExerciseIdTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.of(validUser(1L)));
    when(dbExerciseHistoryRepoMock.findByExerciseHistoryId(Mockito.any(Integer.class))).thenReturn(Optional.empty());
    Assertions.assertThrows(MissingDataException.class, () ->
            exerciseService.deleteExerciseRecordInDb(Optional.of(1), new UserIdDto(1L))
    );
  }

  @Test
  public void deleteExerciseRecordNotBelongedTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(Optional.of(validUser(1L)));
    when(dbExerciseHistoryRepoMock.findByExerciseHistoryId(Mockito.any(Integer.class))).thenReturn(validExerciseHistory(1L));
    exerciseService.deleteExerciseRecordInDb(Optional.of(1), new UserIdDto(1L));
  }


}