package com.jvm.coms4156.columbia.wehealth.service;

import com.jvm.coms4156.columbia.wehealth.dto.DietHistoryDetailsDto;
import com.jvm.coms4156.columbia.wehealth.entity.*;
import com.jvm.coms4156.columbia.wehealth.Utility.Utility;
import com.jvm.coms4156.columbia.wehealth.dto.DietHistoryResponseDto;
import com.jvm.coms4156.columbia.wehealth.dto.DietRecordDto;
import com.jvm.coms4156.columbia.wehealth.dto.UserIdDto;
import com.jvm.coms4156.columbia.wehealth.exception.BadRequestException;
import com.jvm.coms4156.columbia.wehealth.exception.NotFoundException;
import com.jvm.coms4156.columbia.wehealth.repository.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.jvm.coms4156.columbia.wehealth.common.Constants.*;

@Service
@Log4j2
public class DietService {

    @Autowired
    private DBUserRepository dbUserRepo;

    @Autowired
    private DietHistoryRepository dietHistoryRepo;

    @Autowired
    private DietNutrientMappingRepository dietNutrientMappingRepo;

    @Autowired
    private DietTypeRepository dietTypeRepo;

    @Autowired
    private NutrientTypeRepository nutrientTypeRepo;

    @Transactional
    public void addDietRecordToDB(DietRecordDto dietRecordDto) {
        // add diet record to diet_history table
        DietHistory dietHistory = new DietHistory();
        Optional<DBUser> user = dbUserRepo.findByUserId(dietRecordDto.getUserId());
        if (user.isEmpty()) {
            throw new NotFoundException("User not found with provided user id.");
        }
        dietHistory.setUser(user.get());

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
        }
        else if (dietRecordDto.getUnit().toLowerCase().equals(GRAM)) {
            dietHistory.setWeight(dietRecordDto.getWeight());
        }
        else {
            throw new BadRequestException("Illegal weight unit, please use gram or pound.");
        }

        String currentDateTime = Utility.getStringOfCurrentDateTime();
        dietHistory.setCreatedBy(user.get().getUsername());
        dietHistory.setCreatedTime(currentDateTime);
        dietHistory.setUpdatedBy(user.get().getUsername());
        dietHistory.setUpdatedTime(currentDateTime);

        dietHistoryRepo.save(dietHistory);

    }

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

    public void addAllNutrientsInfoToDietNutrientMapping(DietRecordDto dietRecordDto) {
        Optional<DietType> dietType = dietTypeRepo.findByDietTypeId(dietRecordDto.getDietTypeId());
        if (dietType.isEmpty()) {
            throw new BadRequestException("Diet type not found: Please insert diet type first.");
        }
        // Insert 4 diet_nutrient mappings
        String currentDateTime = Utility.getStringOfCurrentDateTime();
        addOneNutrientInfoToDietNutrientMapping(dietType.get(), PROTEIN, dietRecordDto.getProtein(), currentDateTime);
        addOneNutrientInfoToDietNutrientMapping(dietType.get(), FAT, dietRecordDto.getFat(), currentDateTime);
        addOneNutrientInfoToDietNutrientMapping(dietType.get(), CARBS, dietRecordDto.getCarbs(), currentDateTime);
        addOneNutrientInfoToDietNutrientMapping(dietType.get(), CALORIES, dietRecordDto.getCalories(), currentDateTime);
    }

    public void addOneNutrientInfoToDietNutrientMapping(DietType dietType, int nutrientTypeId, double value,
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

    public DietHistoryResponseDto getDietHistory(UserIdDto userIdDto,
                                                 Optional<String> unit, Optional<Integer> length) {
        Optional<DBUser> user = dbUserRepo.findByUserId(userIdDto.getUserId());
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
        }
        else {
            // Calculate starting datetime for diet history by selected duration
            String startDateTime = Utility.getStringOfStartDateTime(timeUnit, timeLength);
            dietHistoryList = dietHistoryRepo.findAllByUserAndCreatedTimeAfter(user.get(), startDateTime);
        }
        DietHistoryResponseDto dietHistoryResponseDto = new DietHistoryResponseDto();
        log.info("**********Add every diet history to responseDto**********");
        for (DietHistory dietHistory: dietHistoryList) {
            DietHistoryDetailsDto dietHistoryDetailsDto = getDietHistoryDetails(dietHistory);
            dietHistoryResponseDto.getDietHistoryList().add(dietHistoryDetailsDto);
        }
        return dietHistoryResponseDto;
    }

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
        for (DietNutrientMapping nutrient: nutrientsList) {
            double totalAmount = dietHistory.getWeight() * nutrient.getValue();
            if (nutrient.getNutrientType().getNutrientTypeId() == PROTEIN) {
                dietHistoryDetailsDto.setTotalProtein(totalAmount);
            }
            else if (nutrient.getNutrientType().getNutrientTypeId() == FAT) {
                dietHistoryDetailsDto.setTotalProtein(totalAmount);
            }
            else if (nutrient.getNutrientType().getNutrientTypeId() == CARBS) {
                dietHistoryDetailsDto.setTotalCarbs(totalAmount);
            }
            else if (nutrient.getNutrientType().getNutrientTypeId() == CALORIES) {
                dietHistoryDetailsDto.setTotalCalories(totalAmount);
            }
            else {
                throw new BadRequestException("Invalid nutrient type.");
            }
        }
        return dietHistoryDetailsDto;
    }

    @Transactional
    public void updateDietHistory(Integer recordId, DietRecordDto dietRecordDto) {
        // Check and get the old record
        Optional<DietHistory> dietHistory = dietHistoryRepo.findByDietHistoryId(recordId);
        if (dietHistory.isEmpty()) {
            throw new BadRequestException("Invalid recordId.");
        }
        // Check and get the user
        Optional<DBUser> user = dbUserRepo.findByUserId(dietRecordDto.getUserId());
        if (user.isEmpty()) {
            throw new BadRequestException("User not found with provided user id.");
        }
        if (dietHistory.get().getUser().getUserId() != user.get().getUserId()) {
            throw new BadRequestException("You can't update other user's diet record.");
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
        dietHistory.get().setWeight(dietRecordDto.getWeight());
        dietHistory.get().setUnit(dietRecordDto.getUnit());
        dietHistory.get().setUpdatedBy(user.get().getUsername());
        dietHistory.get().setUpdatedTime(Utility.getStringOfCurrentDateTime());
        dietHistoryRepo.save(dietHistory.get());
    }

    @Transactional
    public void deleteDietHistory(Integer recordId, UserIdDto userIdDto) {
        // Check and get the old record
        Optional<DietHistory> dietHistory = dietHistoryRepo.findByDietHistoryId(recordId);
        if (dietHistory.isEmpty()) {
            throw new BadRequestException("Invalid recordId.");
        }
        // Check and get the user
        Optional<DBUser> user = dbUserRepo.findByUserId(userIdDto.getUserId());
        if (user.isEmpty()) {
            throw new BadRequestException("User not found with provided user id.");
        }
        if (dietHistory.get().getUser().getUserId() != (user.get().getUserId())) {
            throw new BadRequestException("You can't delete other user's diet record.");
        }
        // delete the diet record
        dietHistoryRepo.delete(dietHistory.get());
    }
}
