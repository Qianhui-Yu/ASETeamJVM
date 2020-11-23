package com.jvm.coms4156.columbia.wehealth.service;


import static com.jvm.coms4156.columbia.wehealth.common.Constants.ALL;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.CALORIES;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.CARBS;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.DEVELOPER;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.FAT;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.GRAM;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.ONE;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.POUND;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.POUND_TO_GRAM;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.PROTEIN;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.UNIT;

import com.jvm.coms4156.columbia.wehealth.domain.AuthenticatedUser;
import com.jvm.coms4156.columbia.wehealth.dto.DietHistoryDetailsDto;
import com.jvm.coms4156.columbia.wehealth.dto.DietHistoryResponseDto;
import com.jvm.coms4156.columbia.wehealth.dto.DietRecordDto;
import com.jvm.coms4156.columbia.wehealth.dto.UserIdDto;
import com.jvm.coms4156.columbia.wehealth.entity.DbUser;
import com.jvm.coms4156.columbia.wehealth.entity.DietHistory;
import com.jvm.coms4156.columbia.wehealth.entity.DietNutrientMapping;
import com.jvm.coms4156.columbia.wehealth.entity.DietType;
import com.jvm.coms4156.columbia.wehealth.exception.BadRequestException;
import com.jvm.coms4156.columbia.wehealth.exception.NotFoundException;
import com.jvm.coms4156.columbia.wehealth.repository.DbUserRepository;
import com.jvm.coms4156.columbia.wehealth.repository.DietHistoryRepository;
import com.jvm.coms4156.columbia.wehealth.repository.DietNutrientMappingRepository;
import com.jvm.coms4156.columbia.wehealth.repository.DietTypeRepository;
import com.jvm.coms4156.columbia.wehealth.repository.NutrientTypeRepository;
import com.jvm.coms4156.columbia.wehealth.utility.Utility;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
public class DietService {

  @Autowired
  private DbUserRepository dbUserRepo;

  @Autowired
  private DietHistoryRepository dietHistoryRepo;

  @Autowired
  private DietNutrientMappingRepository dietNutrientMappingRepo;

  @Autowired
  private DietTypeRepository dietTypeRepo;

  @Autowired
  private NutrientTypeRepository nutrientTypeRepo;

  /**
   * Backend logic for adding a diet record into the database.
   *
   * @param dietRecordDto Input diet record object. Refer to dto/DietRecordDto for details.
   */
  @Transactional
  public void addDietRecordToDb(AuthenticatedUser au, DietRecordDto dietRecordDto) {
    // add diet record to diet_history table
    DietHistory dietHistory = new DietHistory();
    Optional<DbUser> user = dbUserRepo.findByUserId(au.getUserId());
    if (user.isEmpty()) {
      throw new NotFoundException("User not found with provided user id.");
    }
    dietHistory.setUser(user.get());

    if (dietRecordDto.getWeight() <= 0) {
      throw new BadRequestException("Weight should be larger than zero.");
    }

    Optional<DietType> dietType = dietTypeRepo.findByDietTypeId(dietRecordDto.getDietTypeId());
    if (dietType.isEmpty()) {
      // add new diet type to diet_type table
      addDietType(dietRecordDto.getDietTypeId(), dietRecordDto.getDietTypeName());
      // add 4 nutrients' info to diet_nutrient_mapping table
      addAllNutrientsInfoToDietNutrientMapping(dietRecordDto);
    }
    dietType = dietTypeRepo.findByDietTypeId(dietRecordDto.getDietTypeId());
    dietHistory.setDietType(dietType.get());


    // In diet_history table, the default unit of weight is gram
    dietHistory.setUnit(GRAM);

    if (dietRecordDto.getUnit().toLowerCase().equals(POUND)) {
      dietHistory.setWeight(dietRecordDto.getWeight() * POUND_TO_GRAM);
    } else if (dietRecordDto.getUnit().toLowerCase().equals(GRAM)) {
      dietHistory.setWeight(dietRecordDto.getWeight());
    } else {
      throw new BadRequestException("Illegal weight unit, please use gram or pound.");
    }

    String currentDateTime = Utility.getStringOfCurrentDateTime();
    dietHistory.setCreatedBy(user.get().getUsername());
    dietHistory.setCreatedTime(currentDateTime);
    dietHistory.setUpdatedBy(user.get().getUsername());
    dietHistory.setUpdatedTime(currentDateTime);

    dietHistoryRepo.save(dietHistory);

  }

  /**
   * Add a new diet type into the database.
   *
   * @param dietTypeId ID of the diet type.
   * @param dietTypeName Name of the diet type.
   */
  public void addDietType(int dietTypeId, String dietTypeName) {
    String currentDateTime = Utility.getStringOfCurrentDateTime();
    DietType dietType = new DietType();
    dietType.setDietTypeId(dietTypeId);
    dietType.setDietTypeName(dietTypeName);
    dietType.setCreatedBy(DEVELOPER);
    dietType.setCreatedTime(currentDateTime);
    dietType.setUpdatedBy(DEVELOPER);
    dietType.setUpdatedTime(currentDateTime);
    dietTypeRepo.save(dietType);
  }

  /**
   * Associate the four nutrition types to a diet type.
   *
   * @param dietRecordDto Input diet record object. Refer to dto/DietRecordDto for details.
   */
  public void addAllNutrientsInfoToDietNutrientMapping(DietRecordDto dietRecordDto) {
    Optional<DietType> dietType = dietTypeRepo.findByDietTypeId(dietRecordDto.getDietTypeId());
    if (dietType.isEmpty()) {
      throw new BadRequestException("Diet type not found: Please insert diet type first.");
    }
    // Insert 4 diet_nutrient mappings
    String currentDateTime = Utility.getStringOfCurrentDateTime();
    addOneNutrientInfoToDietNutrientMapping(dietType.get(), PROTEIN,
            dietRecordDto.getProtein(), currentDateTime);
    addOneNutrientInfoToDietNutrientMapping(dietType.get(), FAT,
            dietRecordDto.getFat(), currentDateTime);
    addOneNutrientInfoToDietNutrientMapping(dietType.get(), CARBS,
            dietRecordDto.getCarbs(), currentDateTime);
    addOneNutrientInfoToDietNutrientMapping(dietType.get(), CALORIES,
            dietRecordDto.getCalories(), currentDateTime);
  }

  /**
   * Associate one nutrition to a diet type.
   *
   * @param dietType Input diet type.
   * @param nutrientTypeId Input nutrietion type ID.
   * @param value Input value of the nutrition per 100 gram.
   * @param currentDateTime Input date and time.
   */
  public void addOneNutrientInfoToDietNutrientMapping(DietType dietType, int nutrientTypeId,
                                                      double value,
                                                      String currentDateTime) {
    DietNutrientMapping dietNutrientMapping = new DietNutrientMapping();
    dietNutrientMapping.setDietType(dietType);
    dietNutrientMapping.setNutrientType(nutrientTypeRepo.findByNutrientTypeId(nutrientTypeId));
    dietNutrientMapping.setValue(value);
    dietNutrientMapping.setCreatedBy(DEVELOPER);
    dietNutrientMapping.setCreatedTime(currentDateTime);
    dietNutrientMapping.setUpdatedBy(DEVELOPER);
    dietNutrientMapping.setUpdatedTime(currentDateTime);
    dietNutrientMappingRepo.save(dietNutrientMapping);
  }

  /**
   * Get a list of diet history records based on input criterion.
   *
   * @param unit Unit type of the span. Among ["day", "week", "month", "year"].
   * @param length Date the number units back.
   * @return Return a list of records. Refer to dto/DietHistoryResponseDto for details.
   */
  public DietHistoryResponseDto getDietHistory(AuthenticatedUser au,
                                               Optional<String> unit, Optional<Integer> length) {
    Optional<DbUser> user = dbUserRepo.findByUserId(au.getUserId());
    if (user.isEmpty()) {
      throw new BadRequestException("User not found with provided user id.");
    }

    String timeUnit = unit.orElse(ALL); // Default: find all diet history
    int timeLength = length.orElse(ONE); // Default: 1 time unit e.g. 1 week, 1 month...
    if (timeLength < 0) {
      throw new BadRequestException("Invalid time length: Duration must be positive.");
    }

    List<DietHistory> dietHistoryList;
    log.info("**********Get diet history by selected duration**********");
    if (timeUnit.equals(ALL)) {
      dietHistoryList = dietHistoryRepo.findAllByUser(user.get());
    } else {
      // Calculate starting datetime for diet history by selected duration
      String startDateTime = Utility.getStringOfStartDateTime(timeUnit, timeLength);
      dietHistoryList = dietHistoryRepo.findAllByUserAndCreatedTimeAfter(user.get(), startDateTime);
    }
    DietHistoryResponseDto dietHistoryResponseDto = new DietHistoryResponseDto();
    log.info("**********Add every diet history to responseDto**********");
    for (DietHistory dietHistory : dietHistoryList) {
      DietHistoryDetailsDto dietHistoryDetailsDto = getDietHistoryDetails(dietHistory);
      dietHistoryResponseDto.getDietHistoryList().add(dietHistoryDetailsDto);
    }
    return dietHistoryResponseDto;
  }

  /**
   * Pack a diet history record into the return type DeitHisotryDetailsDto.
   *
   * @param dietHistory The record to be packed.
   * @return Refer to dto/DietHistoryDetailsDto for details.
   */
  private DietHistoryDetailsDto getDietHistoryDetails(DietHistory dietHistory) {
    DietHistoryDetailsDto dietHistoryDetailsDto = new DietHistoryDetailsDto();
    dietHistoryDetailsDto.setDietHistoryId(dietHistory.getDietHistoryId());
    dietHistoryDetailsDto.setDietTypeId(dietHistory.getDietType().getDietTypeId());
    dietHistoryDetailsDto.setDietTypeName(dietHistory.getDietType().getDietTypeName());
    dietHistoryDetailsDto.setWeight(dietHistory.getWeight());
    dietHistoryDetailsDto.setUnit(dietHistory.getUnit()); // default: gram
    dietHistoryDetailsDto.setTime(dietHistory.getCreatedTime());
    log.info("**********Get this diet's 4 nutrients' unit amount (per 100 gram)**********");
    List<DietNutrientMapping> nutrientsList =
            dietNutrientMappingRepo.findAllByDietTypeOrderByNutrientType(dietHistory.getDietType());
    log.info("**********Set this diet's 4 nutrients' total amount**********");
    for (DietNutrientMapping nutrient : nutrientsList) {
      double totalAmount = dietHistory.getWeight() / UNIT * nutrient.getValue();
      log.info("nutrient: {}, unit amount: {}, total amount: {}",
              nutrient.getNutrientType().getNutrientTypeName(), nutrient.getValue(), totalAmount);
      if (nutrient.getNutrientType().getNutrientTypeId() == PROTEIN) {
        dietHistoryDetailsDto.setTotalProtein(totalAmount);
        log.info("Set totalProtein.");
      } else if (nutrient.getNutrientType().getNutrientTypeId() == FAT) {
        dietHistoryDetailsDto.setTotalFat(totalAmount);
        log.info("Set totalFat.");
      } else if (nutrient.getNutrientType().getNutrientTypeId() == CARBS) {
        dietHistoryDetailsDto.setTotalCarbs(totalAmount);
        log.info("Set totalCarbs.");
      } else if (nutrient.getNutrientType().getNutrientTypeId() == CALORIES) {
        dietHistoryDetailsDto.setTotalCalories(totalAmount);
        log.info("Set totalCalories.");
      } else {
        throw new BadRequestException("Invalid nutrient type.");
      }
    }
    return dietHistoryDetailsDto;
  }

  /**
   * Edit a diet record in the database.
   *
   * @param recordId ID of the record to be edited.
   * @param dietRecordDto Target record after editing. Refer to dto/DietRecordDto for details.
   */
  @Transactional
  public void updateDietHistory(AuthenticatedUser au, Integer recordId,
                                DietRecordDto dietRecordDto) {
    // Check and get the old record
    Optional<DietHistory> dietHistory = dietHistoryRepo.findByDietHistoryId(recordId);
    if (dietHistory.isEmpty()) {
      throw new BadRequestException("Invalid recordId.");
    }
    // Check and get the user
    Optional<DbUser> user = dbUserRepo.findByUserId(au.getUserId());
    if (user.isEmpty()) {
      throw new BadRequestException("User not found with provided user id.");
    }
    if (!dietHistory.get().getUser().getUserId().equals(user.get().getUserId())) {
      throw new BadRequestException("You can't update other user's diet record.");
    }
    if (dietRecordDto.getWeight() <= 0) {
      throw new BadRequestException("Weight should be larger than zero.");
    }

    // Check if need to update diet type
    DietType dietType = dietHistory.get().getDietType();
    if (dietType.getDietTypeId() != dietRecordDto.getDietTypeId()
            && !dietType.getDietTypeName().equals(dietRecordDto.getDietTypeName())) {
      // Update diet type for this record
      // Check if the new diet type exists
      Optional<DietType> newDietType = dietTypeRepo.findByDietTypeId(dietRecordDto.getDietTypeId());
      if (newDietType.isEmpty()) {
        // add new diet type to diet_type table
        addDietType(dietRecordDto.getDietTypeId(), dietRecordDto.getDietTypeName());
        // add 4 nutrients' info to diet_nutrient_mapping table
        addAllNutrientsInfoToDietNutrientMapping(dietRecordDto);
      }
      newDietType = dietTypeRepo.findByDietTypeId(dietRecordDto.getDietTypeId());
      dietHistory.get().setDietType(newDietType.get());
    }

    // should also check weight and unit, but...
    if (dietRecordDto.getUnit().equals(POUND)) {
      dietHistory.get().setWeight(dietRecordDto.getWeight() * POUND_TO_GRAM);
    } else if (dietRecordDto.getUnit().equals(GRAM)) {
      dietHistory.get().setWeight(dietRecordDto.getWeight());
    } else {
      throw new BadRequestException("Illegal weight unit, please use gram or pound.");
    }
    //dietHistory.get().setUnit(dietRecordDto.getUnit());
    dietHistory.get().setUpdatedBy(user.get().getUsername());
    dietHistory.get().setUpdatedTime(Utility.getStringOfCurrentDateTime());
    dietHistoryRepo.save(dietHistory.get());
  }

  /**
   * Delete a diet record from the database.
   *
   * @param recordId ID of the diet record to be deleted.
   * @param userIdDto Input user ID object indicating which user performs this.
   */
  @Transactional
  public void deleteDietHistory(AuthenticatedUser au, Integer recordId) {
    // Check and get the old record
    Optional<DietHistory> dietHistory = dietHistoryRepo.findByDietHistoryId(recordId);
    if (dietHistory.isEmpty()) {
      throw new BadRequestException("Invalid recordId.");
    }
    // Check and get the user
    Optional<DbUser> user = dbUserRepo.findByUserId(au.getUserId());
    if (user.isEmpty()) {
      throw new BadRequestException("User not found with provided user id.");
    }
    if (!dietHistory.get().getUser().getUserId().equals(user.get().getUserId())) {
      throw new BadRequestException("You can't delete other user's diet record.");
    }
    // delete the diet record
    dietHistoryRepo.delete(dietHistory.get());
  }
}
