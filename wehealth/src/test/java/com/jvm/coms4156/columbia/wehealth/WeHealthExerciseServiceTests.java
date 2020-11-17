package com.jvm.coms4156.columbia.wehealth;

import static org.mockito.Mockito.when;

import com.jvm.coms4156.columbia.wehealth.dto.ExerciseHistoryDetailsDto;
import com.jvm.coms4156.columbia.wehealth.dto.ExerciseHistoryResponseDto;
import com.jvm.coms4156.columbia.wehealth.dto.UserIdDto;
import com.jvm.coms4156.columbia.wehealth.dto.ExerciseRecordDto;
import com.jvm.coms4156.columbia.wehealth.entity.DBUser;
import com.jvm.coms4156.columbia.wehealth.entity.ExerciseHistory;
import com.jvm.coms4156.columbia.wehealth.entity.ExerciseType;
import com.jvm.coms4156.columbia.wehealth.exception.BadRequestException;
import com.jvm.coms4156.columbia.wehealth.exception.MissingDataException;
import com.jvm.coms4156.columbia.wehealth.exception.NotFoundException;
import com.jvm.coms4156.columbia.wehealth.repository.DBUserRepository;
import com.jvm.coms4156.columbia.wehealth.repository.ExerciseHistoryRepository;
import com.jvm.coms4156.columbia.wehealth.repository.ExerciseTypeRepository;
import com.jvm.coms4156.columbia.wehealth.service.ExerciseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class WeHealthExerciseServiceTests {

    @InjectMocks
    private ExerciseService exerciseService;

    @Mock
    private DBUserRepository dbUserRepoMock;

    @Mock
    private ExerciseHistoryRepository dbExerciseHistoryRepoMock;

    @Mock
    private ExerciseTypeRepository dbExerciseTypeRepoMock;

    private DBUser validUser(Integer userId) {
        DBUser dbUser = new DBUser("1", "1");
        dbUser.setUser_id(userId);
        return dbUser;
    }


    private List<ExerciseHistory> validExerciseHistoryList() {
        List<ExerciseHistory> exerciseHistoryList = new ArrayList<>();
        for (int i = 1; i < 10; ++i) {
            exerciseHistoryList.add(validExerciseHistory(i).get());
        }
        return exerciseHistoryList;
    }

    private Optional<ExerciseHistory> validExerciseHistory(Integer userId) {
        ExerciseHistory exerciseHistory = new ExerciseHistory();
        DBUser dbUser = validUser(userId);
        exerciseHistory.setUser(dbUser);
        return Optional.of(exerciseHistory);
    }

    @Test
    public void validateUserInvalidUserTest() {
        when(dbUserRepoMock.findByUserId(Mockito.any(Integer.class))).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> {
            exerciseService.validateUser(1, Optional.of(2));
        });
    }

    @Test
    public void validateUserTest() {
        when(dbUserRepoMock.findByUserId(Mockito.any(Integer.class))).thenReturn(Optional.of(validUser(1)));
        exerciseService.validateUser(1, Optional.of(1));
    }


    @Test
    public void validateUserDifferentUserTest() {
        when(dbUserRepoMock.findByUserId(Mockito.any(Integer.class))).thenReturn(Optional.of(validUser(1)));
        Assertions.assertThrows(NotFoundException.class, () -> {
            exerciseService.validateUser(1, Optional.empty());
        });
    }

    @Test
    public void addExerciseRecordToDBInvalidTypeTest() {
        when(dbUserRepoMock.findByUserId(Mockito.any(Integer.class))).thenReturn(Optional.of(validUser(1)));
        when(dbExerciseTypeRepoMock.findByExerciseTypeName(Mockito.any(String.class))).thenReturn(Optional.empty());
        ExerciseRecordDto exerciseRecordDto = new ExerciseRecordDto(1, "TestExerciseType", 1000.0);
        Assertions.assertThrows(MissingDataException.class, () -> {
            exerciseService.addExerciseRecordToDB(exerciseRecordDto);
        });
    }

    @Test
    public void addExerciseRecordToDBTest() {
        when(dbUserRepoMock.findByUserId(Mockito.any(Integer.class))).thenReturn(Optional.of(validUser(1)));
        when(dbExerciseTypeRepoMock.findByExerciseTypeName(Mockito.any(String.class))).thenReturn(Optional.of(new ExerciseType()));
        ExerciseRecordDto exerciseRecordDto = new ExerciseRecordDto(1, "TestExerciseType", 1000.0);
        exerciseService.addExerciseRecordToDB(exerciseRecordDto);
    }

    @Test
    public void getExerciseHistoryNegativeDurationTest() {
        when(dbUserRepoMock.findByUserId(Mockito.any(Integer.class))).thenReturn(Optional.of(validUser(1)));
        Assertions.assertThrows(BadRequestException.class, () -> {
            exerciseService.getExerciseHistory(new UserIdDto(1), Optional.empty(), Optional.of(-100));
        });
    }

    @Test
    public void getExerciseHistoryAllTest() {
        DBUser user = validUser(1);
        List<ExerciseHistory> historyDetailList = validExerciseHistoryList();
        when(dbUserRepoMock.findByUserId(Mockito.any(Integer.class))).thenReturn(Optional.of(validUser(1)));
        when(dbExerciseHistoryRepoMock.findAllByUser(user)).thenReturn(historyDetailList);
        ExerciseHistoryResponseDto retDto = exerciseService.getExerciseHistory(new UserIdDto(1), Optional.empty(), Optional.empty());
        List<ExerciseHistoryDetailsDto> historyDetailDtoList = retDto.getExerciseHistoryList();
        Assertions.assertEquals(historyDetailDtoList.size(), historyDetailList.size());
        for (int i = 0; i < historyDetailDtoList.size(); ++i) {
            Assertions.assertEquals(historyDetailDtoList.get(i).getExerciseHistoryId(), historyDetailList.get(i).getExerciseHistoryId());
        }
    }

    @Test
    public void getExerciseHistoryDurationTest() {
        DBUser user = validUser(1);
        List<ExerciseHistory> historyDetailList = validExerciseHistoryList();
        when(dbUserRepoMock.findByUserId(Mockito.any(Integer.class))).thenReturn(Optional.of(validUser(1)));
        when(dbExerciseHistoryRepoMock.findAllByUserAndCreatedTimeAfter(user, Mockito.anyString())).thenReturn(historyDetailList);
        ExerciseHistoryResponseDto retDto = exerciseService.getExerciseHistory(new UserIdDto(1), Optional.empty(), Optional.empty());
        List<ExerciseHistoryDetailsDto> historyDetailDtoList = retDto.getExerciseHistoryList();
        Assertions.assertEquals(historyDetailDtoList.size(), historyDetailList.size());
        for (int i = 0; i < historyDetailDtoList.size(); ++i) {
            Assertions.assertEquals(historyDetailDtoList.get(i).getExerciseHistoryId(), historyDetailList.get(i).getExerciseHistoryId());
        }
    }

    @Test
    public void editExerciseRecordInvalidExerciseIdTest() {
        when(dbUserRepoMock.findByUserId(Mockito.any(Integer.class))).thenReturn(Optional.of(validUser(1)));
        when(dbExerciseHistoryRepoMock.findByExerciseHistoryId(Mockito.any(Integer.class))).thenReturn(Optional.empty());
        Assertions.assertThrows(MissingDataException.class, () -> {
            exerciseService.editExerciseRecordAtDB(Optional.of(1), new ExerciseRecordDto());
        });
    }

    @Test
    public void editExerciseRecordInvalidTypeNameTest() {
        when(dbUserRepoMock.findByUserId(Mockito.any(Integer.class))).thenReturn(Optional.of(validUser(1)));
        when(dbExerciseHistoryRepoMock.findById(Mockito.any(Integer.class))).thenReturn(validExerciseHistory(1));
        when(dbExerciseTypeRepoMock.findByExerciseTypeName(Mockito.any(String.class))).thenReturn(Optional.empty());
        Assertions.assertThrows(MissingDataException.class, () -> {
            ExerciseRecordDto newRecord = new ExerciseRecordDto();
            newRecord.setUserId(1);
            newRecord.setExerciseTypeName("TestExerciseType");
            exerciseService.editExerciseRecordAtDB(Optional.of(1), new ExerciseRecordDto());
        });
    }

    @Test
    public void editExerciseRecordTest() {
        when(dbUserRepoMock.findByUserId(Mockito.any(Integer.class))).thenReturn(Optional.of(validUser(1)));
        when(dbExerciseHistoryRepoMock.findById(Mockito.any(Integer.class))).thenReturn(validExerciseHistory(1));
        when(dbExerciseTypeRepoMock.findByExerciseTypeName(Mockito.any(String.class))).thenReturn(Optional.of(new ExerciseType()));
        ExerciseRecordDto newRecord = new ExerciseRecordDto();
        newRecord.setUserId(1);
        newRecord.setExerciseTypeName("TestExerciseType");
        exerciseService.editExerciseRecordAtDB(Optional.of(1), new ExerciseRecordDto());
    }

    @Test
    public void deleteExerciseRecordInvalidExerciseIdTest() {
        when(dbUserRepoMock.findByUserId(Mockito.any(Integer.class))).thenReturn(Optional.of(validUser(1)));
        when(dbExerciseHistoryRepoMock.findByExerciseHistoryId(Mockito.any(Integer.class))).thenReturn(Optional.empty());
        Assertions.assertThrows(MissingDataException.class, () -> {
            exerciseService.deleteExerciseRecordInDB(Optional.of(1), new UserIdDto(1));
        });
    }

    @Test
    public void deleteExerciseRecordNotBelongedTest() {
        when(dbUserRepoMock.findByUserId(Mockito.any(Integer.class))).thenReturn(Optional.of(validUser(1)));
        when(dbExerciseHistoryRepoMock.findByExerciseHistoryId(Mockito.any(Integer.class))).thenReturn(validExerciseHistory(1));
        exerciseService.deleteExerciseRecordInDB(Optional.of(1), new UserIdDto(1));
    }


}