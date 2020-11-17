package com.jvm.coms4156.columbia.wehealth.service;

import com.jvm.coms4156.columbia.wehealth.dto.ExerciseHistoryDetailsDto;
import com.jvm.coms4156.columbia.wehealth.dto.ExerciseHistoryResponseDto;
import com.jvm.coms4156.columbia.wehealth.dto.ExerciseRecordDto;
import com.jvm.coms4156.columbia.wehealth.dto.UserIdDto;
import com.jvm.coms4156.columbia.wehealth.entity.DBUser;
import com.jvm.coms4156.columbia.wehealth.entity.ExerciseHistory;
import com.jvm.coms4156.columbia.wehealth.entity.ExerciseType;
import com.jvm.coms4156.columbia.wehealth.exception.BadAuthException;
import com.jvm.coms4156.columbia.wehealth.exception.BadRequestException;
import com.jvm.coms4156.columbia.wehealth.exception.MissingDataException;
import com.jvm.coms4156.columbia.wehealth.exception.NotFoundException;
import com.jvm.coms4156.columbia.wehealth.repository.*;
import com.jvm.coms4156.columbia.wehealth.utility.Utility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.jvm.coms4156.columbia.wehealth.common.Constants.*;

@Service
@Log4j2
public class ExerciseService {

    @Autowired
    private DBUserRepository dbUserRepo;

    @Autowired
    private ExerciseHistoryRepository exerciseHistoryRepo;

    @Autowired
    private ExerciseTypeRepository exerciseTypeRepo;

    // TODO(Derek Jin): This function should be more suitable for the utility class
    public DBUser validateUser(Integer userId, Optional<Integer> requestUserId) {
        Optional<DBUser> user = dbUserRepo.findByUserId(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found with provided user id.");
        }
        if (!requestUserId.orElse(-1).equals(userId)) {
            throw new BadAuthException();
        }
        return user.get();
    }

    @Transactional
    public void addExerciseRecordToDB(ExerciseRecordDto exerciseRecordDto) {
        // add exercise record to exercise_history table
        ExerciseHistory exerciseHistory = new ExerciseHistory();
        DBUser user = validateUser(exerciseRecordDto.getUserId(), Optional.empty());
        exerciseHistory.setUser(user);

        Optional<ExerciseType> exerciseType = exerciseTypeRepo.findByExerciseTypeName(exerciseRecordDto.getExerciseTypeName());
        if (exerciseType.isEmpty()) {
            // throw exception since all exercise types should exist
            throw new MissingDataException("Exercise type not found with provided name");
        }
        exerciseHistory.setExerciseType(exerciseType.get());

        String currentDateTime = Utility.getStringOfCurrentDateTime();
        exerciseHistory.setCreatedBy(user.getUsername());
        exerciseHistory.setCreatedTime(currentDateTime);
        exerciseHistory.setUpdatedBy(user.getUsername());
        exerciseHistory.setUpdatedTime(currentDateTime);

        exerciseHistoryRepo.save(exerciseHistory);
    }

    public ExerciseHistoryResponseDto getExerciseHistory(UserIdDto userIdDto,
                                                 Optional<String> unit, Optional<Integer> length) {
        DBUser user = validateUser(userIdDto.getUserId(), Optional.empty());
        String timeUnit = unit.orElse(ALL); // Default: find all exercise history
        int timeLength = length.orElse(ONE); // Default: 1 time unit e.g. 1 week, 1 month...
        if (timeLength < 0) {
            throw new BadRequestException("Invalid time length: Duration must be positive.");
        }

        List<ExerciseHistory> exerciseHistoryList;
        log.info("**********Get exercise history by selected duration**********");
        if (timeUnit.equals(ALL)) {
            exerciseHistoryList = exerciseHistoryRepo.findAllByUser(user);
        }
        else {
            // Calculate starting datetime for exercise history by selected duration
            String startDateTime = Utility.getStringOfStartDateTime(timeUnit, timeLength);
            exerciseHistoryList = exerciseHistoryRepo.findAllByUserAndCreatedTimeAfter(user, startDateTime);
        }
        ExerciseHistoryResponseDto exerciseHistoryResponseDto = new ExerciseHistoryResponseDto();
        log.info("**********Add every exercise history to responseDto**********");
        for (ExerciseHistory exerciseHistory: exerciseHistoryList) {
            ExerciseHistoryDetailsDto exerciseHistoryDetailsDto = getExerciseHistoryDetails(exerciseHistory);
            exerciseHistoryResponseDto.getExerciseHistoryList().add(exerciseHistoryDetailsDto);
        }
        return exerciseHistoryResponseDto;
    }

    private ExerciseHistoryDetailsDto getExerciseHistoryDetails(ExerciseHistory exerciseHistory) {
        ExerciseType exerciseType = exerciseHistory.getExerciseType();
        ExerciseHistoryDetailsDto exerciseHistoryDetailsDto = new ExerciseHistoryDetailsDto();
        exerciseHistoryDetailsDto.setExerciseHistoryId(exerciseHistory.getExerciseHistoryId());
        exerciseHistoryDetailsDto.setExerciseTypeName(exerciseType.getExerciseTypeName());
        exerciseHistoryDetailsDto.setTime(exerciseHistory.getCreatedTime());
        exerciseHistoryDetailsDto.setDuration(exerciseHistory.getDurationInMins());
        exerciseHistoryDetailsDto.setTotalCalories(exerciseHistory.getDurationInMins() * exerciseType.getCalories());
        return exerciseHistoryDetailsDto;
    }

    @Transactional
    public void editExerciseRecordAtDB(Optional<Integer> recordId, ExerciseRecordDto exerciseRecordDto) {
        Integer exerciseRecordId = recordId.orElse(-1);
        Optional<ExerciseHistory> exerciseHistory = exerciseHistoryRepo.findByExerciseHistoryId(exerciseRecordId);
        exerciseHistory.map(record -> {
            DBUser user = validateUser(exerciseRecordDto.getUserId(), Optional.of(exerciseHistory.get().getUser().getUser_id()));
            Optional<ExerciseType> exerciseType = exerciseTypeRepo.findByExerciseTypeName(exerciseRecordDto.getExerciseTypeName());
            if (exerciseType.isEmpty()) {
                throw new MissingDataException("Exercise type not found with provided name");
            }
            record.setExerciseType(exerciseType.get());
            String currentDateTime = Utility.getStringOfCurrentDateTime();
            record.setUpdatedBy(user.getUsername());
            record.setUpdatedTime(currentDateTime);
            return exerciseHistoryRepo.save(record);
        }).orElseThrow(() -> new MissingDataException("Exercise record not found with provided id"));
    }

    @Transactional
    public void deleteExerciseRecordInDB(Optional<Integer> recordId, UserIdDto userIdDto) {
        Integer exerciseRecordId = recordId.orElse(-1);
        Optional<ExerciseHistory> exerciseHistory = exerciseHistoryRepo.findByExerciseHistoryId(exerciseRecordId);
        if (exerciseHistory.isPresent() == false) {
            throw new MissingDataException("Exercise record not found with provided id");
        }
        validateUser(userIdDto.getUserId(), Optional.of(exerciseHistory.get().getUser().getUser_id()));
        exerciseHistoryRepo.deleteById(exerciseRecordId);
    }
}
