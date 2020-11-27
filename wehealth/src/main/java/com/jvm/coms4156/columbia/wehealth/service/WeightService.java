package com.jvm.coms4156.columbia.wehealth.service;

import static com.jvm.coms4156.columbia.wehealth.common.Constants.ALL;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.GRAM;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.ONE;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.POUND;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.POUND_TO_GRAM;

import com.jvm.coms4156.columbia.wehealth.domain.AuthenticatedUser;
import com.jvm.coms4156.columbia.wehealth.dto.WeightHistoryDetailsDto;
import com.jvm.coms4156.columbia.wehealth.dto.WeightHistoryResponseDto;
import com.jvm.coms4156.columbia.wehealth.dto.WeightRecordDto;
import com.jvm.coms4156.columbia.wehealth.entity.DbUser;
import com.jvm.coms4156.columbia.wehealth.entity.WeightHistory;
import com.jvm.coms4156.columbia.wehealth.exception.BadRequestException;
import com.jvm.coms4156.columbia.wehealth.exception.NotFoundException;
import com.jvm.coms4156.columbia.wehealth.repository.DbUserRepository;
import com.jvm.coms4156.columbia.wehealth.repository.WeightHistoryRepository;
import com.jvm.coms4156.columbia.wehealth.utility.Utility;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class WeightService {

  @Autowired
  private DbUserRepository dbUserRepo;

  @Autowired
  private WeightHistoryRepository weightHistoryRepo;

  /**
   * Add a weight record into the database.
   *
   * @param weightRecordDto Input weight record object. Refer to dto/WeightRecordDto for details.
   */
  @Transactional
  public void addWeightRecordToDb(AuthenticatedUser au, WeightRecordDto weightRecordDto) {
    // add weight record to weight_history table
    WeightHistory weightHistory = new WeightHistory();
    Optional<DbUser> user = dbUserRepo.findByUserId(au.getUserId());
    if (user.isEmpty()) {
      throw new NotFoundException("User not found with provided user id.");
    }
    weightHistory.setUser(user.get());
    if (weightRecordDto.getWeight() <= 0) {
      throw new BadRequestException("Weight should be larger than zero.");
    }

    // In weight_history table, the default unit of weight is gram
    weightHistory.setUnit(GRAM);

    if (weightRecordDto.getUnit().toLowerCase().equals(POUND)) {
      weightHistory.setWeight(weightRecordDto.getWeight() * POUND_TO_GRAM);
    } else if (weightRecordDto.getUnit().toLowerCase().equals(GRAM)) {
      weightHistory.setWeight(weightRecordDto.getWeight());
    } else {
      throw new BadRequestException("Illegal weight unit, please use gram or pound.");
    }

    String currentDateTime = Utility.getStringOfCurrentDateTime();
    weightHistory.setCreatedBy(user.get().getUsername());
    weightHistory.setCreatedTime(currentDateTime);
    weightHistory.setUpdatedBy(user.get().getUsername());
    weightHistory.setUpdatedTime(currentDateTime);

    weightHistoryRepo.save(weightHistory);

  }

  /**
   * Get a all the weight records based on input criteria.
   *
   * @param userIdDto Input user ID. Refer to dto/UserIdDto for details.
   * @param unit Unit of the span.
   * @param length Number of units to date back from current date.
   * @return Refer to dto/WeightHistoryResponseDto for details.
   */
  public WeightHistoryResponseDto getWeightHistory(AuthenticatedUser au,
                                                   Optional<String> unit,
                                                   Optional<Integer> length) {
    Optional<DbUser> user = dbUserRepo.findByUserId(au.getUserId());
    if (user.isEmpty()) {
      throw new NotFoundException("User not found with provided user id.");
    }

    String timeUnit = unit.orElse(ALL); // Default: find all weight history
    int timeLength = length.orElse(ONE); // Default: 1 time unit e.g. 1 week, 1 month...
    if (timeLength < 0) {
      throw new BadRequestException("Invalid time length: Duration must be positive.");
    }

    List<WeightHistory> weightHistoryList;
    log.info("**********Get weight history by selected duration**********");
    if (timeUnit.equals(ALL)) {
      weightHistoryList = weightHistoryRepo.findAllByUser(user.get());
    } else {
      // Calculate starting datetime for weight history by selected duration
      String startDateTime = Utility.getStringOfStartDateTime(timeUnit, timeLength);
      weightHistoryList = weightHistoryRepo
              .findAllByUserAndCreatedTimeAfter(user.get(), startDateTime);
    }
    WeightHistoryResponseDto weightHistoryResponseDto = new WeightHistoryResponseDto();
    List<WeightHistoryDetailsDto> weightHistoryDetailsDtoList = new ArrayList<>();
    weightHistoryResponseDto.setWeightHistoryList(weightHistoryDetailsDtoList);
    log.info("**********Add every weight history to responseDto**********");
    for (WeightHistory weightHistory : weightHistoryList) {
      WeightHistoryDetailsDto weightHistoryDetailsDto = getWeightHistoryDetails(weightHistory);
      weightHistoryResponseDto.getWeightHistoryList().add(weightHistoryDetailsDto);
    }
    return weightHistoryResponseDto;
  }

  /**
   * Pack the weight record into return type.
   *
   * @param weightHistory Input record to be packed.
   * @return Packed record for return. Refer to dto/WeightHistoryDetailsDto for details.
   */
  private WeightHistoryDetailsDto getWeightHistoryDetails(WeightHistory weightHistory) {
    WeightHistoryDetailsDto weightHistoryDetailsDto = new WeightHistoryDetailsDto();
    weightHistoryDetailsDto.setWeightHistoryId(weightHistory.getWeightHistoryId());
    weightHistoryDetailsDto.setWeight(weightHistory.getWeight());
    weightHistoryDetailsDto.setUnit(weightHistory.getUnit()); // default: gram
    weightHistoryDetailsDto.setTime(weightHistory.getCreatedTime());
    return weightHistoryDetailsDto;
  }

  /**
   * Edit a weight record.
   *
   * @param weightId ID of the weight record to be edited.
   * @param weightRecordDto Target weight record after editing.
   */
  @Transactional
  public void editWeightRecord(AuthenticatedUser au,
                               Integer weightId,
                               WeightRecordDto weightRecordDto) {
    Optional<WeightHistory> weightHistory = weightHistoryRepo.findByWeightHistoryId(weightId);
    if (weightHistory.isEmpty()) {
      throw new NotFoundException("Weight record not found with provided weight record id.");
    }

    WeightHistory weightHistoryRecord = weightHistory.get();
    if (! weightHistoryRecord.getUser().getUserId().equals(au.getUserId())) {
      throw new BadRequestException("Illegal edit attempt: Record not belong to this user.");
    }

    if (weightRecordDto.getWeight() <= 0) {
      throw new BadRequestException("Weight should be larger than zero.");
    }

    String currentDateTime = Utility.getStringOfCurrentDateTime();
    if (weightRecordDto.getUnit().toLowerCase().equals(POUND)) {
      weightHistoryRecord.setWeight(weightRecordDto.getWeight() * POUND_TO_GRAM);
    } else {
      weightHistoryRecord.setWeight(weightRecordDto.getWeight());
    }
    weightHistoryRecord.setUnit(GRAM);
    weightHistoryRecord.setUpdatedTime(currentDateTime);
    weightHistoryRepo.save(weightHistoryRecord);
  }

  /**
   * Delete a weight record.
   *
   * @param weightId ID of the weight record to be deleted.
   * @param userIdDto Input user ID object.
   */
  @Transactional
  public void deleteWeightRecord(AuthenticatedUser au, Integer weightId) {
    Optional<WeightHistory> weightHistory = weightHistoryRepo.findByWeightHistoryId(weightId);
    if (weightHistory.isEmpty()) {
      throw new NotFoundException("Weight record not found with provided weight record id.");
    }

    WeightHistory weightHistoryRecord = weightHistory.get();
    if (! weightHistoryRecord.getUser().getUserId().equals(au.getUserId())) {
      throw new BadRequestException("Illegal delete attempt: Record not belong to this user.");
    }
    weightHistoryRepo.delete(weightHistoryRecord);
  }
}
