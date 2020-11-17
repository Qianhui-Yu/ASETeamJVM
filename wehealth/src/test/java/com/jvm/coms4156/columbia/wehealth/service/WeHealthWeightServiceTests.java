package com.jvm.coms4156.columbia.wehealth;

import static org.mockito.Mockito.when;

import com.jvm.coms4156.columbia.wehealth.common.Constants;
import com.jvm.coms4156.columbia.wehealth.dto.UserIdDto;
import com.jvm.coms4156.columbia.wehealth.dto.WeightHistoryResponseDto;
import com.jvm.coms4156.columbia.wehealth.dto.WeightRecordDto;
import com.jvm.coms4156.columbia.wehealth.entity.DBUser;
import com.jvm.coms4156.columbia.wehealth.entity.WeightHistory;
import com.jvm.coms4156.columbia.wehealth.exception.BadRequestException;
import com.jvm.coms4156.columbia.wehealth.exception.NotFoundException;
import com.jvm.coms4156.columbia.wehealth.repository.DBUserRepository;
import com.jvm.coms4156.columbia.wehealth.repository.WeightHistoryRepository;
import com.jvm.coms4156.columbia.wehealth.service.WeightService;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WeHealthWeightServiceTests {

    @InjectMocks
    private WeightService weightService;

    @Mock
    private DBUserRepository dbUserRepoMock;

    @Mock
    private WeightHistoryRepository dbWeightHistoryRepoMock;

    private DBUser getValidUser() {
        DBUser dbUser = new DBUser("1", "1");
        dbUser.setUserId((long) 1);
        return dbUser;
    }

    private Optional<DBUser> validUser() {
        DBUser user = new DBUser("a", "a");
        return Optional.of(user);
    }

    private Optional<DBUser> invalidUser() {
        return Optional.empty();
    }

    private List<WeightHistory> getHistories(int num) {
        List<WeightHistory> list = new ArrayList<WeightHistory>();
        for(int i = 0; i < num; i++) {
            list.add(new WeightHistory());
        }
        return list;
    }

    private Optional<WeightHistory> validHistoryId() {
        WeightHistory weightHistory = new WeightHistory();
        DBUser dbUser = getValidUser();
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
        Assertions.assertThrows(NotFoundException.class, () -> {
            weightService.addWeightRecordToDB(new WeightRecordDto());
        });
    }

    // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
    @Test
    public void addWeightRecordToDBInvalidUnitTest() {
        when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(validUser());
        WeightRecordDto weightRecordDto = new WeightRecordDto((long) 1, 60000.0, "unit");
        Assertions.assertThrows(BadRequestException.class, () -> {
            weightService.addWeightRecordToDB(weightRecordDto);
        });
    }

    // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
    @Test
    public void addWeightRecordToDBGramTest() {
        when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(validUser());
        WeightRecordDto weightRecordDto = new WeightRecordDto((long) 1, 60000.0, Constants.GRAM);
        weightService.addWeightRecordToDB(weightRecordDto);
    }

    // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
    @Test
    public void addWeightRecordToDBPoundTest() {
        when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(validUser());
        WeightRecordDto weightRecordDto = new WeightRecordDto((long) 1, 60000.0, Constants.POUND);
        weightService.addWeightRecordToDB(weightRecordDto);
    }

    @Test
    public void getWeightHistoryInvalidUserTest() {
        when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(invalidUser());
        UserIdDto userIdDto = new UserIdDto();
        Assertions.assertThrows(NotFoundException.class, () -> {
            weightService.getWeightHistory(userIdDto, Optional.of(Constants.ALL), Optional.of(1));
        });
    }

    @Test
    public void getWeightHistoryInvalidTimeLengthTest() {
        when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(validUser());
        UserIdDto userIdDto = new UserIdDto();
        DBUser dbUser = getValidUser();
        userIdDto.setUserId(dbUser.getUserId());
        Assertions.assertThrows(BadRequestException.class, () -> {
            weightService.getWeightHistory(userIdDto, Optional.of(Constants.ALL), Optional.of(-1));
        });
    }

    @Test
    public void getWeightHistoryTimeLengthAllTest() {
        when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(validUser());
        when(dbWeightHistoryRepoMock.findAllByUser(Mockito.any(DBUser.class)))
                .thenReturn(getHistories(10));
        UserIdDto userIdDto = new UserIdDto();
        DBUser dbUser = getValidUser();
        userIdDto.setUserId(dbUser.getUserId());
        WeightHistoryResponseDto weightHistoryResponseDto =
                weightService.getWeightHistory(userIdDto, Optional.of(Constants.ALL), Optional.of(1));
        Assertions.assertEquals(weightHistoryResponseDto.getWeightHistoryList().size(), 10);
    }

    @Test
    public void getWeightHistoryTimeLengthNotAllTest() {
        when(dbUserRepoMock.findByUserId(Mockito.any(Long.class))).thenReturn(validUser());
        when(dbWeightHistoryRepoMock.findAllByUserAndCreatedTimeAfter(
                Mockito.any(DBUser.class), Mockito.any(String.class)))
                .thenReturn(getHistories(5));
        UserIdDto userIdDto = new UserIdDto();
        DBUser dbUser = getValidUser();
        userIdDto.setUserId(dbUser.getUserId());
        WeightHistoryResponseDto weightHistoryResponseDto =
                weightService.getWeightHistory(userIdDto, Optional.of(Constants.WEEK), Optional.of(1));
        Assertions.assertEquals(weightHistoryResponseDto.getWeightHistoryList().size(), 5);
    }

    // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
    @Test
    public void editWeightRecordInvalidWeightIdTest() {
        when(dbWeightHistoryRepoMock.findByWeightHistoryId(Mockito.any(Integer.class)))
                .thenReturn(invalidHistoryId());
        Assertions.assertThrows(NotFoundException.class, () -> {
            weightService.editWeightRecord(1, new WeightRecordDto());
        });
    }

    // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
    @Test
    public void editWeightRecordNotBelongedTest() {
        when(dbWeightHistoryRepoMock.findByWeightHistoryId(Mockito.any(Integer.class)))
                .thenReturn(validHistoryId());
        DBUser dbUser = getValidUser();
        dbUser.setUserId((long) 2);
        WeightRecordDto weightRecordDto = new WeightRecordDto();
        weightRecordDto.setUserId(dbUser.getUserId());
        Assertions.assertThrows(BadRequestException.class, () -> {
            weightService.editWeightRecord(1, weightRecordDto);
        });
    }

    // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
    @Test
    public void editWeightRecordTest() {
        when(dbWeightHistoryRepoMock.findByWeightHistoryId(Mockito.any(Integer.class)))
                .thenReturn(validHistoryId());
        DBUser dbUser = getValidUser();
        WeightRecordDto weightRecordDto = new WeightRecordDto(dbUser.getUserId(),
                60000.0,
                Constants.GRAM);
        weightService.editWeightRecord(1, weightRecordDto);
    }

    // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
    @Test
    public void deleteWeightRecordInvalidWeightIdTest() {
        when(dbWeightHistoryRepoMock.findByWeightHistoryId(Mockito.any(Integer.class)))
                .thenReturn(invalidHistoryId());
        DBUser dbUser = getValidUser();
        dbUser.setUserId((long) 2);
        UserIdDto userIdDto = new UserIdDto();
        userIdDto.setUserId(dbUser.getUserId());
        Assertions.assertThrows(NotFoundException.class, () -> {
            weightService.deleteWeightRecord(1, userIdDto);
        });
    }

    // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
    @Test
    public void deleteWeightRecordNotBelongedTest() {
        when(dbWeightHistoryRepoMock.findByWeightHistoryId(Mockito.any(Integer.class)))
                .thenReturn(validHistoryId());
        DBUser dbUser = getValidUser();
        dbUser.setUserId((long) 2);
        UserIdDto userIdDto = new UserIdDto();
        userIdDto.setUserId(dbUser.getUserId());
        Assertions.assertThrows(BadRequestException.class, () -> {
            weightService.deleteWeightRecord(1, userIdDto);
        });
    }

    // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
    @Test
    public void deleteWeightRecordTest() {
        when(dbWeightHistoryRepoMock.findByWeightHistoryId(Mockito.any(Integer.class)))
                .thenReturn(validHistoryId());
        DBUser dbUser = getValidUser();
        UserIdDto userIdDto = new UserIdDto();
        userIdDto.setUserId(dbUser.getUserId());
        weightService.deleteWeightRecord(1, userIdDto);
    }


}
