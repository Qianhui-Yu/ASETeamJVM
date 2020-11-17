package com.jvm.coms4156.columbia.wehealth;

import static org.mockito.Mockito.when;

import com.jvm.coms4156.columbia.wehealth.common.Constants;
import com.jvm.coms4156.columbia.wehealth.dto.UserIdDto;
import com.jvm.coms4156.columbia.wehealth.dto.WeightRecordDto;
import com.jvm.coms4156.columbia.wehealth.entity.DBUser;
import com.jvm.coms4156.columbia.wehealth.entity.WeightHistory;
import com.jvm.coms4156.columbia.wehealth.exception.BadRequestException;
import com.jvm.coms4156.columbia.wehealth.exception.NotFoundException;
import com.jvm.coms4156.columbia.wehealth.repository.DBUserRepository;
import com.jvm.coms4156.columbia.wehealth.repository.WeightHistoryRepository;
import com.jvm.coms4156.columbia.wehealth.service.WeightService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Null;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import java.util.Optional;

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
        dbUser.setUser_id(1);
        return dbUser;
    }

    private Optional<DBUser> validUser() {
        DBUser user = new DBUser("a", "a");
        return Optional.of(user);
    }

    private Optional<DBUser> invalidUser() {
        return Optional.ofNullable(null);
    }

    private List<WeightHistory> getHistories() {
        List<WeightHistory> list = null;
        return list;
    }

    private Optional<WeightHistory> validHistoryId() {
        WeightHistory weightHistory = new WeightHistory();
        DBUser dbUser = getValidUser();
        weightHistory.setUser(dbUser);
        return Optional.of(weightHistory);
    }

    private Optional<WeightHistory> invalidHistoryId() {
        return Optional.ofNullable(null);
    }

    // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
    @Test
    public void addWeightRecordToDBInvalidUserTest() {
        when(dbUserRepoMock.findByUserId(Mockito.any(Integer.class))).thenReturn(invalidUser());
        Assertions.assertThrows(NotFoundException.class, () -> {
            weightService.addWeightRecordToDB(new WeightRecordDto());
        });
    }

    // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
    @Test
    public void addWeightRecordToDBInvalidUnitTest() {
        when(dbUserRepoMock.findByUserId(Mockito.any(Integer.class))).thenReturn(validUser());
        WeightRecordDto weightRecordDto = new WeightRecordDto(1, 60000.0, "unit");
        Assertions.assertThrows(BadRequestException.class, () -> {
            weightService.addWeightRecordToDB(weightRecordDto);
        });
    }

    // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
    @Test
    public void addWeightRecordToDBGramTest() {
        when(dbUserRepoMock.findByUserId(Mockito.any(Integer.class))).thenReturn(validUser());
        WeightRecordDto weightRecordDto = new WeightRecordDto(1, 60000.0, Constants.GRAM);
        weightService.addWeightRecordToDB(weightRecordDto);
    }

    // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
    @Test
    public void addWeightRecordToDBPoundTest() {
        when(dbUserRepoMock.findByUserId(Mockito.any(Integer.class))).thenReturn(validUser());
        WeightRecordDto weightRecordDto = new WeightRecordDto(1, 60000.0, Constants.POUND);
        weightService.addWeightRecordToDB(weightRecordDto);
    }

    @Test
    public void getWeightHistoryTest() {

    }

    // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
    @Test
    public void editWeightRecordInvalidWeightIdTest() {
        when(dbWeightHistoryRepoMock.findById(Mockito.any(String.class))).thenReturn(invalidHistoryId());
        Assertions.assertThrows(NotFoundException.class, () -> {
            weightService.editWeightRecord("1", new WeightRecordDto());
        });
    }

    // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
    @Test
    public void editWeightRecordNotBelongedTest() {
        when(dbWeightHistoryRepoMock.findById(Mockito.any(String.class))).thenReturn(validHistoryId());
        DBUser dbUser = getValidUser();
        dbUser.setUser_id(2);
        WeightRecordDto weightRecordDto = new WeightRecordDto();
        weightRecordDto.setUserId(dbUser.getUser_id());
        Assertions.assertThrows(BadRequestException.class, () -> {
            weightService.editWeightRecord("1", weightRecordDto);
        });
    }

    // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
    @Test
    public void editWeightRecordTest() {
        when(dbWeightHistoryRepoMock.findById(Mockito.any(String.class))).thenReturn(validHistoryId());
        DBUser dbUser = getValidUser();
        WeightRecordDto weightRecordDto = new WeightRecordDto();
        weightRecordDto.setUserId(dbUser.getUser_id());
        weightService.editWeightRecord("1", weightRecordDto);
    }

    // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
    @Test
    public void deleteWeightRecordInvalidWeightIdTest() {
        when(dbWeightHistoryRepoMock.findById(Mockito.any(String.class))).thenReturn(invalidHistoryId());
        Assertions.assertThrows(NotFoundException.class, () -> {
            weightService.editWeightRecord("1", new WeightRecordDto());
        });
    }

    // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
    @Test
    public void deleteWeightRecordNotBelongedTest() {
        when(dbWeightHistoryRepoMock.findById(Mockito.any(String.class))).thenReturn(validHistoryId());
        DBUser dbUser = getValidUser();
        dbUser.setUser_id(2);
        UserIdDto userIdDto = new UserIdDto();
        userIdDto.setUserId(dbUser.getUser_id());
        Assertions.assertThrows(BadRequestException.class, () -> {
            weightService.deleteWeightRecord("1", userIdDto);
        });
    }

    // TODO: (Chengchen Li) Modify to use jwt.au() instead of query db for user info
    @Test
    public void deleteWeightRecordTest() {
        when(dbWeightHistoryRepoMock.findById(Mockito.any(String.class))).thenReturn(validHistoryId());
        DBUser dbUser = getValidUser();
        UserIdDto userIdDto = new UserIdDto();
        userIdDto.setUserId(dbUser.getUser_id());
        weightService.deleteWeightRecord("1", userIdDto);
    }

}
