package com.jvm.coms4156.columbia.wehealth.service;

import static com.jvm.coms4156.columbia.wehealth.common.Constants.ALL;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.ONE;

import com.jvm.coms4156.columbia.wehealth.domain.AuthenticatedUser;
import com.jvm.coms4156.columbia.wehealth.dto.ExerciseHistoryDetailsDto;
import com.jvm.coms4156.columbia.wehealth.dto.ExerciseHistoryResponseDto;
import com.jvm.coms4156.columbia.wehealth.dto.ExerciseRecordDto;
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
import com.jvm.coms4156.columbia.wehealth.utility.Utility;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class ExerciseService {

  @Autowired
  private DbUserRepository dbUserRepo;

  @Autowired
  private ExerciseHistoryRepository exerciseHistoryRepo;

  @Autowired
  private ExerciseTypeRepository exerciseTypeRepo;

  /**
   * Check if user with userId exists in the database and retrieve the user object.
   * If requestUserId is not empty, also check if two userId are the same.
   *
   * @param userId userId.
   * @return user object retrieved from database.
   */
  // TODO(Derek Jin): This function should be more suitable for the utility class

  public DbUser validateUser(Long userId, Optional<Long> requestUserId) {
    Optional<DbUser> user = dbUserRepo.findByUserId(userId);
    if (user.isEmpty()) {
      throw new NotFoundException("User not found with provided user id.");
    }
    if (requestUserId.isPresent() && !requestUserId.orElse(-1L).equals(userId)) {
      throw new BadAuthException();
    }
    return user.get();
  }

  /**
   * Add one exercise record to database.
   *
   * @param exerciseRecordDto dto that contains the record information
   * @param au dto containing requester user's information.
   */
  @Transactional
  public void addExerciseRecordToDb(ExerciseRecordDto exerciseRecordDto, AuthenticatedUser au) {
    // add exercise record to exercise_history table
    ExerciseHistory exerciseHistory = new ExerciseHistory();
    if (exerciseRecordDto.getDuration() <= 0) {
      throw new BadRequestException("Exercise duration should be larger than zero");
    }
    DbUser user = validateUser(au.getUserId(), Optional.empty());
    exerciseHistory.setUser(user);

    Optional<ExerciseType> exerciseType = exerciseTypeRepo
            .findByExerciseTypeName(exerciseRecordDto.getExerciseTypeName());
    if (exerciseType.isEmpty()) {
      // throw exception since all exercise types should exist
      throw new MissingDataException("Exercise type not found with provided name");
    }
    exerciseHistory.setExerciseType(exerciseType.get());
    exerciseHistory.setDurationInMins(exerciseRecordDto.getDuration());
    String currentDateTime = Utility.getStringOfCurrentDateTime();
    exerciseHistory.setCreatedBy(user.getUsername());
    exerciseHistory.setCreatedTime(currentDateTime);
    exerciseHistory.setUpdatedBy(user.getUsername());
    exerciseHistory.setUpdatedTime(currentDateTime);

    exerciseHistoryRepo.save(exerciseHistory);
  }

  /**
   * Get exercise history from database.
   *
   * @param unit Unit of time for specific period of history.
   *             If ALL or empty, retrieve all history.
   * @param length Length of time to retrieve, Ignored if unit is ALL.
   * @param au dto containing requester user's information.
   * @return response dto containing the list of all record for that user in the time period
   */
  public ExerciseHistoryResponseDto getExerciseHistory(Optional<String> unit,
                                                       Optional<Integer> length,
                                                       AuthenticatedUser au) {
    DbUser user = validateUser(au.getUserId(), Optional.empty());
    String timeUnit = unit.orElse(ALL); // Default: find all exercise history
    int timeLength = length.orElse(ONE); // Default: 1 time unit e.g. 1 week, 1 month...
    if (timeLength < 0) {
      throw new BadRequestException("Invalid time length: Duration must be positive.");
    }

    List<ExerciseHistory> exerciseHistoryList;
    log.info("**********Get exercise history by selected duration**********");
    if (timeUnit.equals(ALL)) {
      exerciseHistoryList = exerciseHistoryRepo.findAllByUserOrderByCreatedTime(user);
    } else {
      // Calculate starting datetime for exercise history by selected duration
      String startDateTime = Utility.getStringOfStartDateTime(timeUnit, timeLength);
      exerciseHistoryList = exerciseHistoryRepo
              .findAllByUserAndCreatedTimeAfterOrderByCreatedTime(user, startDateTime);
    }
    ExerciseHistoryResponseDto exerciseHistoryResponseDto = new ExerciseHistoryResponseDto();
    log.info("**********Add every exercise history to responseDto**********");
    for (ExerciseHistory exerciseHistory : exerciseHistoryList) {
      ExerciseHistoryDetailsDto exerciseHistoryDetailsDto =
              getExerciseHistoryDetails(exerciseHistory);
      exerciseHistoryResponseDto.getExerciseHistoryList()
              .add(exerciseHistoryDetailsDto);
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
    exerciseHistoryDetailsDto.setTotalCalories(exerciseHistory.getDurationInMins()
            * exerciseType.getCalories());
    return exerciseHistoryDetailsDto;
  }

  /**
   * Edit one exercise record in the database.
   *
   * @param recordId the id of the record to be edited.
   * @param exerciseRecordDto the content of the result to edit to.
   * @param au dto containing requester user's information.
   */
  @Transactional
  public void editExerciseRecordAtDb(Optional<Integer> recordId,
                                     ExerciseRecordDto exerciseRecordDto,
                                     AuthenticatedUser au) {
    Integer exerciseRecordId = recordId.orElse(-1);
    Optional<ExerciseHistory> exerciseHistory = exerciseHistoryRepo
            .findByExerciseHistoryId(exerciseRecordId);
    exerciseHistory.map(record -> {
      if (exerciseRecordDto.getDuration() <= 0) {
        throw new BadRequestException("Exercise duration should be larger than zero");
      }
      Optional<ExerciseType> exerciseType = exerciseTypeRepo
              .findByExerciseTypeName(exerciseRecordDto.getExerciseTypeName());
      if (exerciseType.isEmpty()) {
        throw new MissingDataException("Exercise type not found with provided name");
      }
      DbUser user = validateUser(au.getUserId(), Optional.of(record.getUser().getUserId()));
      record.setExerciseType(exerciseType.get());
      record.setDurationInMins(exerciseRecordDto.getDuration());
      record.setUpdatedBy(user.getUsername());
      String currentDateTime = Utility.getStringOfCurrentDateTime();
      record.setUpdatedTime(currentDateTime);
      return exerciseHistoryRepo.save(record);
    }).orElseThrow(() -> new MissingDataException("Exercise record not found with provided id"));
  }

  /**
   * delete one exercise record from database.
   *
   * @param recordId the id of the record to be deleted.
   * @param au AuthenticatedUser the user that makes the request
   */
  @Transactional
  public void deleteExerciseRecordInDb(Optional<Integer> recordId,
                                       AuthenticatedUser au) {
    Integer exerciseRecordId = recordId.orElse(-1);
    Optional<ExerciseHistory> exerciseHistory = exerciseHistoryRepo
            .findByExerciseHistoryId(exerciseRecordId);
    if (exerciseHistory.isEmpty()) {
      throw new MissingDataException("Exercise record not found with provided id");
    }
    validateUser(au.getUserId(), Optional.of(exerciseHistory.get().getUser().getUserId()));
    exerciseHistoryRepo.deleteById(exerciseRecordId);
  }
}
