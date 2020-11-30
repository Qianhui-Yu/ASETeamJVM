package com.jvm.coms4156.columbia.wehealth.service;

import static com.jvm.coms4156.columbia.wehealth.common.Constants.GOOD_AVG_CALORIES;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.GOOD_AVG_CARBS;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.GOOD_AVG_FAT;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.GOOD_AVG_PROTEIN;

import com.jvm.coms4156.columbia.wehealth.domain.AuthenticatedUser;
import com.jvm.coms4156.columbia.wehealth.dto.*;

import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class AdviceService {
  @Autowired
  private DietService dietService;
  @Autowired
  private ExerciseService exerciseService;

  /**
   * generate diet and exercise advice for a given user.
   *
   * @param user authenticated user
   * @return adviceDto generated advice
   */
  public AdviceDto getAdvice(AuthenticatedUser user) {
    AdviceDto adviceDto = new AdviceDto();
    Optional<String> unit = Optional.of("month");
    Optional<Integer> length = Optional.of(1);

    DietHistoryResponseDto dietHistory = dietService.getDietHistory(user, unit, length);
    List<DietByDayDto> dietByDayDtos = groupDietByDate(dietHistory);
    ExerciseHistoryResponseDto exerciseHistory = exerciseService
            .getExerciseHistory(unit, length, user);
    List<ExerciseByDayDto> exerciseByDayDtos = groupExerciseByDate(exerciseHistory);

    adviceDto.setDietByDate(dietByDayDtos);
    adviceDto.setExerciseByDate(exerciseByDayDtos);
//    Optional<DietStatsDto> dietStatsDto = getStats(dietHistoryResponseDto);
//    if (dietStatsDto.isEmpty()) {
//      adviceDto.setIsEmpty(true);
//    } else {
//      adviceDto = generateAdvice(dietStatsDto.get());
//      adviceDto.setIsEmpty(false);
//    }
    return adviceDto;
  }

  private List<DietByDayDto> groupDietByDate(DietHistoryResponseDto dto) {
    HashMap<String, DietByDayDto> aggregated = new HashMap<String, DietByDayDto>();
    for (DietHistoryDetailsDto dhd : dto.getDietHistoryList()) {
      String date = dhd.getTime().split(" ")[0];
      if (!aggregated.containsKey(date)) {
        aggregated.put(date, new DietByDayDto(date, 0.0, 0.0, 0.0, 0.0));
      }
      DietByDayDto dietByDayDto = aggregated.get(date);
      dietByDayDto.setTotalCalories(dietByDayDto.getTotalCalories() + dhd.getTotalCalories());
      dietByDayDto.setTotalProtein(dietByDayDto.getTotalProtein() + dhd.getTotalProtein());
      dietByDayDto.setTotalCarbs(dietByDayDto.getTotalCarbs() + dhd.getTotalCarbs());
      dietByDayDto.setTotalFat(dietByDayDto.getTotalFat() + dhd.getTotalFat());
    }
    return new ArrayList(aggregated.values());
  }

  private List<ExerciseByDayDto> groupExerciseByDate(ExerciseHistoryResponseDto dto) {
    HashMap<String, ExerciseByDayDto> aggregated = new HashMap<String, ExerciseByDayDto>();
    for (ExerciseHistoryDetailsDto dhd : dto.getExerciseHistoryList()) {
      String date = dhd.getTime().split(" ")[0];
      if (!aggregated.containsKey(date)) {
        aggregated.put(date, new ExerciseByDayDto(date, 0.0, 0.0));
      }
      ExerciseByDayDto exerciseByDayDto = aggregated.get(date);
      exerciseByDayDto.setTotalCalories(exerciseByDayDto.getTotalCalories() + dhd.getTotalCalories());
      exerciseByDayDto.setTotalDuration(exerciseByDayDto.getTotalDuration() + dhd.getDuration());
    }
    return new ArrayList(aggregated.values());
  }


  private AdviceDto generateAdvice(DietStatsDto dietStatsDto) {
    AdviceDto adviceDto = new AdviceDto();
    String suggestions = String.format("You Avg calories intake is %.2f KCAL; "
                    + "the recommended intake is %.2f KCAL,",
            dietStatsDto.getAvgCalories(), GOOD_AVG_CALORIES);
    // calories recommendation
    if (dietStatsDto.getAvgCalories() < GOOD_AVG_CALORIES * 0.8) {
      suggestions += " You should eat more!";
    } else if (dietStatsDto.getAvgCalories() > GOOD_AVG_CALORIES * 1.2) {
      suggestions += " You should eat less!";
    } else {
      suggestions += " Your calories intake is just about right!";
    }
    adviceDto.setCaloriesAdvice(suggestions);
    // protein recommendation
    suggestions = String.format("You Avg protein intake is %.2f grams; "
                    + "the recommended intake is %.2f grams,",
            dietStatsDto.getAvgProtein(), GOOD_AVG_PROTEIN);
    if (dietStatsDto.getAvgProtein() < GOOD_AVG_PROTEIN * 0.8) {
      suggestions += " You should eat more protein product (like Milk or beef)!";
    } else if (dietStatsDto.getAvgProtein() > GOOD_AVG_PROTEIN * 1.2) {
      suggestions += " You should eat less protein product (like Milk or beef)!!";
    } else {
      suggestions += " Your protein intake is just about right!";
    }
    adviceDto.setProteinAdvice(suggestions);
    // fat recommendation
    suggestions = String.format("You Avg fat intake is %.2f grams; "
                    + "the recommended intake is %.2f grams,",
            dietStatsDto.getAvgFat(), GOOD_AVG_FAT);
    if (dietStatsDto.getAvgFat() < GOOD_AVG_FAT * 0.8) {
      suggestions += " You should eat more fat product (like pork)!";
    } else if (dietStatsDto.getAvgFat() > GOOD_AVG_FAT * 1.2) {
      suggestions += " You should eat less fat product (like pork)!!";
    } else {
      suggestions += " Your fat intake is just about right!";
    }
    adviceDto.setFatAdvice(suggestions);
    // carbs recommendation
    suggestions = String.format("You Avg carbs intake is %.2f grams; "
                    + "the recommended intake is %.2f grams,",
            dietStatsDto.getAvgCarbs(), GOOD_AVG_CARBS);
    if (dietStatsDto.getAvgCarbs() < GOOD_AVG_CARBS * 0.8) {
      suggestions += " You should eat more carbs product (like rice or bread)!";
    } else if (dietStatsDto.getAvgCarbs() > GOOD_AVG_CARBS * 1.2) {
      suggestions += " You should eat less carbs product (like rice or bread)!!";
    } else {
      suggestions += " Your carbs intake is just about right!";
    }
    adviceDto.setCarbsAdvice(suggestions);
    return adviceDto;
  }


  private Optional<DietStatsDto> getStats(DietHistoryResponseDto dietHistoryResponseDto) {
    Double avgCalories = 0.0;
    Double avgFat = 0.0;
    Double avgProtein = 0.0;
    Double avgCarbs = 0.0;

    Double maxCalories = 0.0;
    Double maxFat = 0.0;
    Double maxProtein = 0.0;
    Double maxCarbs = 0.0;

    List<DietHistoryDetailsDto> dietHistoryList = dietHistoryResponseDto.getDietHistoryList();
    if (dietHistoryList.size() == 0) {
      return Optional.empty();
    }
    DietStatsDto dietStatsDto = new DietStatsDto();
    for (DietHistoryDetailsDto detail : dietHistoryList) {
      avgCalories += detail.getTotalCalories();
      avgFat += detail.getTotalFat();
      avgProtein += detail.getTotalProtein();
      avgCarbs += detail.getTotalCarbs();

      maxCalories = Math.max(maxCalories, detail.getTotalCalories());
      maxFat = Math.max(maxFat, detail.getTotalFat());
      maxCarbs = Math.max(maxCarbs, detail.getTotalCarbs());
      maxProtein = Math.max(maxProtein, detail.getTotalProtein());
    }
    dietStatsDto.setAvgCalories(avgCalories / dietHistoryList.size());
    dietStatsDto.setAvgFat(avgFat / dietHistoryList.size());
    dietStatsDto.setAvgProtein(avgProtein / dietHistoryList.size());
    dietStatsDto.setAvgCarbs(avgCarbs / dietHistoryList.size());

    dietStatsDto.setMaxCalories(maxCalories);
    dietStatsDto.setMaxFat(maxFat);
    dietStatsDto.setMaxCarbs(maxCarbs);
    dietStatsDto.setMaxProtein(maxProtein);

    return Optional.of(dietStatsDto);
  }
}

