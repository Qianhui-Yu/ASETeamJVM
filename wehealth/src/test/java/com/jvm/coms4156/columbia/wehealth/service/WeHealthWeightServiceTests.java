package com.jvm.coms4156.columbia.wehealth.service;

import static org.mockito.Mockito.when;

import com.jvm.coms4156.columbia.wehealth.common.Constants;
import com.jvm.coms4156.columbia.wehealth.domain.AuthenticatedUser;
import com.jvm.coms4156.columbia.wehealth.dto.UserIdDto;
import com.jvm.coms4156.columbia.wehealth.dto.WeightHistoryResponseDto;
import com.jvm.coms4156.columbia.wehealth.dto.WeightRecordDto;
import com.jvm.coms4156.columbia.wehealth.entity.DbUser;
import com.jvm.coms4156.columbia.wehealth.entity.WeightHistory;
import com.jvm.coms4156.columbia.wehealth.exception.BadRequestException;
import com.jvm.coms4156.columbia.wehealth.exception.NotFoundException;
import com.jvm.coms4156.columbia.wehealth.repository.DbUserRepository;
import com.jvm.coms4156.columbia.wehealth.repository.WeightHistoryRepository;
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
public class WeHealthWeightServiceTests {

  @InjectMocks
  private WeightService weightService;

  @Mock
  private DbUserRepository dbUserRepoMock;

  @Mock
  private WeightHistoryRepository dbWeightHistoryRepoMock;

  private DbUser getValidUser() {
    DbUser dbUser = new DbUser("1", "1");
    dbUser.setUserId((long) 1);
    return dbUser;
  }

  private AuthenticatedUser getValidUserAu() {
    AuthenticatedUser au = new AuthenticatedUser((long)1, 1, "1");
    return au;
  }

  private Optional<DbUser> validUser() {
    DbUser user = new DbUser("a", "a");
    return Optional.of(user);
  }

  private Optional<DbUser> invalidUser() {
    return Optional.empty();
  }

  private List<WeightHistory> getHistories(int num) {
    List<WeightHistory> list = new ArrayList<>();
    for(int i = 0; i < num; i++) {
      list.add(new WeightHistory());
    }
    return list;
  }

  private Optional<WeightHistory> validHistoryId() {
    WeightHistory weightHistory = new WeightHistory();
    DbUser dbUser = getValidUser();
    weightHistory.setUser(dbUser);
    return Optional.of(weightHistory);
  }

  private Optional<WeightHistory> invalidHistoryId() {
    return Optional.empty();
  }

  // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
  @Test
  public void addWeightRecordToDBInvalidUserTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(invalidUser());
    AuthenticatedUser au = getValidUserAu();
    Assertions.assertThrows(NotFoundException.class, () ->
            weightService.addWeightRecordToDb(au, new WeightRecordDto())
    );
  }

  // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
  @Test
  public void addWeightRecordToDBInvalidUnitTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(validUser());
    WeightRecordDto weightRecordDto = new WeightRecordDto(60000.0, "unit");
    AuthenticatedUser au = getValidUserAu();
    Assertions.assertThrows(BadRequestException.class, () ->
            weightService.addWeightRecordToDb(au, weightRecordDto)
    );
  }

  @Test
  public void addWeightRecordToDBInvalidWeightTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(validUser());
    WeightRecordDto weightRecordDto = new WeightRecordDto(-60000.0, "gram");
    AuthenticatedUser au = getValidUserAu();
    Assertions.assertThrows(BadRequestException.class, () ->
        weightService.addWeightRecordToDb(au, weightRecordDto)
    );
  }

  // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
  @Test
  public void addWeightRecordToDBGramTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(validUser());
    WeightRecordDto weightRecordDto = new WeightRecordDto(60000.0, Constants.GRAM);
    AuthenticatedUser au = getValidUserAu();
    weightService.addWeightRecordToDb(au, weightRecordDto);
  }

  // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
  @Test
  public void addWeightRecordToDBPoundTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(validUser());
    WeightRecordDto weightRecordDto = new WeightRecordDto(60000.0, Constants.POUND);
    AuthenticatedUser au = getValidUserAu();
    weightService.addWeightRecordToDb(au, weightRecordDto);
  }

  @Test
  public void getWeightHistoryInvalidUserTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(invalidUser());
    UserIdDto userIdDto = new UserIdDto();
    AuthenticatedUser au = getValidUserAu();
    Assertions.assertThrows(NotFoundException.class, () ->
            weightService.getWeightHistory(au, Optional.of(Constants.ALL), Optional.of(1))
    );
  }

  @Test
  public void getWeightHistoryInvalidTimeLengthTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(validUser());
    AuthenticatedUser au = getValidUserAu();
    Assertions.assertThrows(BadRequestException.class, () ->
            weightService.getWeightHistory(au, Optional.of(Constants.ALL), Optional.of(-1))
    );
  }

  @Test
  public void getWeightHistoryTimeLengthAllTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(validUser());
    when(dbWeightHistoryRepoMock.findAllByUser(Mockito.any(DbUser.class)))
            .thenReturn(getHistories(10));
    AuthenticatedUser au = getValidUserAu();
    WeightHistoryResponseDto weightHistoryResponseDto =
            weightService.getWeightHistory(au, Optional.of(Constants.ALL), Optional.of(1));
    Assertions.assertEquals(weightHistoryResponseDto.getWeightHistoryList().size(), 10);
  }

  @Test
  public void getWeightHistoryTimeLengthNotAllTest() {
    when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(validUser());
    when(dbWeightHistoryRepoMock.findAllByUserAndCreatedTimeAfter(
            Mockito.any(DbUser.class), Mockito.any(String.class)))
            .thenReturn(getHistories(5));
    AuthenticatedUser au = getValidUserAu();
    WeightHistoryResponseDto weightHistoryResponseDto =
            weightService.getWeightHistory(au, Optional.of(Constants.WEEK), Optional.of(1));
    Assertions.assertEquals(weightHistoryResponseDto.getWeightHistoryList().size(), 5);
  }

  // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
  @Test
  public void editWeightRecordInvalidWeightIdTest() {
    when(dbWeightHistoryRepoMock.findByWeightHistoryId(Mockito.any(Integer.class)))
            .thenReturn(invalidHistoryId());
    AuthenticatedUser au = getValidUserAu();
    Assertions.assertThrows(NotFoundException.class, () ->
            weightService.editWeightRecord(au, 1, new WeightRecordDto())
    );
  }

  @Test
  public void editWeightRecordInvalidWeightTest() {
    when(dbWeightHistoryRepoMock.findByWeightHistoryId(Mockito.any(Integer.class)))
        .thenReturn(validHistoryId());
    AuthenticatedUser au = getValidUserAu();
    Assertions.assertThrows(BadRequestException.class, () ->
        weightService.editWeightRecord(au, 1, new WeightRecordDto(-60000.0, Constants.GRAM))
    );
  }

  // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
  @Test
  public void editWeightRecordNotBelongedTest() {
    when(dbWeightHistoryRepoMock.findByWeightHistoryId(Mockito.any(Integer.class)))
            .thenReturn(validHistoryId());
    AuthenticatedUser au = getValidUserAu();
    au.setUserId((long) 2);
    WeightRecordDto weightRecordDto = new WeightRecordDto();
    Assertions.assertThrows(BadRequestException.class, () ->
            weightService.editWeightRecord(au, 1, weightRecordDto)
    );
  }

  // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
  @Test
  public void editWeightRecordValidGRAMTest() {
    when(dbWeightHistoryRepoMock.findByWeightHistoryId(Mockito.any(Integer.class)))
            .thenReturn(validHistoryId());
    AuthenticatedUser au = getValidUserAu();
    WeightRecordDto weightRecordDto = new WeightRecordDto(60000.0, Constants.GRAM);
    weightService.editWeightRecord(au, 1, weightRecordDto);
  }

  @Test
  public void editWeightRecordValidPOUNDTest() {
    when(dbWeightHistoryRepoMock.findByWeightHistoryId(Mockito.any(Integer.class)))
        .thenReturn(validHistoryId());
    AuthenticatedUser au = getValidUserAu();
    WeightRecordDto weightRecordDto = new WeightRecordDto(60000.0, Constants.POUND);
    weightService.editWeightRecord(au, 1, weightRecordDto);
  }

  // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
  @Test
  public void deleteWeightRecordInvalidWeightIdTest() {
    when(dbWeightHistoryRepoMock.findByWeightHistoryId(Mockito.any(Integer.class)))
            .thenReturn(invalidHistoryId());
    AuthenticatedUser au = getValidUserAu();
    Assertions.assertThrows(NotFoundException.class, () ->
            weightService.deleteWeightRecord(au, 1)
    );
  }

  // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
  @Test
  public void deleteWeightRecordNotBelongedTest() {
    when(dbWeightHistoryRepoMock.findByWeightHistoryId(Mockito.any(Integer.class)))
            .thenReturn(validHistoryId());
    AuthenticatedUser au = getValidUserAu();
    au.setUserId((long) 2);
    Assertions.assertThrows(BadRequestException.class, () ->
            weightService.deleteWeightRecord(au, 1)
    );
  }

  // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
  @Test
  public void deleteWeightRecordTest() {
    when(dbWeightHistoryRepoMock.findByWeightHistoryId(Mockito.any(Integer.class)))
            .thenReturn(validHistoryId());
    AuthenticatedUser au = getValidUserAu();
    weightService.deleteWeightRecord(au, 1);
  }


}
