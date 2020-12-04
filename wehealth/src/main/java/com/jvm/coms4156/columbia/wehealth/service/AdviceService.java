package com.jvm.coms4156.columbia.wehealth.service;

import static com.jvm.coms4156.columbia.wehealth.common.Constants.GOOD_AVG_CALORIES;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.GOOD_AVG_CARBS;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.GOOD_AVG_FAT;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.GOOD_AVG_PROTEIN;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.GOOD_DURATION;

import com.jvm.coms4156.columbia.wehealth.domain.AuthenticatedUser;
import com.jvm.coms4156.columbia.wehealth.dto.*;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

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
  @Autowired
  private WeightService weightService;

  /**
   * generate diet and exercise advice for a given user.
   *
   * @param user authenticated user
   * @return adviceDto generated advice
   */
  public AdviceDto getAdvice(AuthenticatedUser user) {
    AdviceDto adviceDto = new AdviceDto();
    adviceDto.setIsEmpty(true);
    Optional<String> unit = Optional.of("month");
    Optional<Integer> length = Optional.of(1);

    DietHistoryResponseDto dietHistory = dietService.getDietHistory(user, unit, length);
    List<DietByDayDto> dietByDayDtos = groupDietByDate(dietHistory);

    ExerciseHistoryResponseDto exerciseHistory = exerciseService
            .getExerciseHistory(unit, length, user);
    List<ExerciseByDayDto> exerciseByDayDtos = groupExerciseByDate(exerciseHistory);

    WeightHistoryResponseDto weightHistory = weightService
            .getWeightHistory(user, unit, length);
    List<WeightHistoryDetailsDto> weightByDay = groupWeightByDate(weightHistory);

    if (dietByDayDtos.size() > 0) {
      adviceDto.setIsEmpty(false);
    }
    if (exerciseByDayDtos.size() > 0) {
      adviceDto.setIsEmpty(false);
    }
    if (weightByDay.size() > 0) {
      adviceDto.setIsEmpty(false);
    }

    adviceDto.setDietByDate(dietByDayDtos);
    adviceDto.setExerciseByDate(exerciseByDayDtos);
    adviceDto.setWeightByDate(weightByDay);
    getStats(adviceDto);
    generateAdvice(adviceDto);
    return adviceDto;
  }

  private void generateAdvice(AdviceDto adviceDto) {
    String suggestion;
    if (adviceDto.getAvgCalories() < GOOD_AVG_CALORIES * 0.7) {
      suggestion = "You avg calories intake is 30% lower than the recommended level."
              + " Please eat more to control your weight.";
    } else if (adviceDto.getAvgCalories() > GOOD_AVG_CALORIES * 1.3) {
      suggestion = "You avg calories intake is 30% higher than the recommended level."
              + " Please eat less to control your weight.";
    } else {
      suggestion = " Your calories intake is just about right!";
    }
    adviceDto.setCaloriesAdvice(suggestion);

    // protein recommendation
    if (adviceDto.getAvgProtein() < GOOD_AVG_PROTEIN * 0.7) {
      suggestion = "Low protein intake, recommended food for you: Eggs, "
              + "Roasted Chicken, Lean Beef.";
    } else if (adviceDto.getAvgProtein() > GOOD_AVG_PROTEIN * 1.3) {
      suggestion = "Too much protein intake, recommended cutting 30% of high protein food intake.";
    } else {
      suggestion = " Your protein intake is just about right!";
    }
    adviceDto.setProteinAdvice(suggestion);

    // fat recommendation
    if (adviceDto.getAvgFat() < GOOD_AVG_FAT * 0.7) {
      suggestion = "Low fat intake, recommended food for you: Avocado, Eggs, Fish.";
    } else if (adviceDto.getAvgFat() > GOOD_AVG_FAT * 1.3) {
      suggestion = "Too much fat intake, recommended cutting 30% of high fat food intake.";
    } else {
      suggestion = " Your fat intake is just about right!";
    }
    adviceDto.setFatAdvice(suggestion);

    // carbs recommendation
    if (adviceDto.getAvgCarbs() < GOOD_AVG_CARBS * 0.8) {
      suggestion = "Low carbs intake, recommended food for you: Bread, Rice, Oats.";
    } else if (adviceDto.getAvgCarbs() > GOOD_AVG_CARBS * 1.2) {
      suggestion = "Too much carbs intake, recommended cutting 30% of high fat carbs intake.";
    } else {
      suggestion = " Your carbs intake is just about right!";
    }
    adviceDto.setCarbsAdvice(suggestion);

    // exercise recommendation
    if (adviceDto.getAvgDuration() < GOOD_DURATION) {
      suggestion = "We recommend 30 mins of mild to medium intensity exercise every day!";
    } else {
      suggestion = "You are killing the daily exercises target!";
    }
    adviceDto.setExerciseAdvice(suggestion);
  }



  private List<DietByDayDto> groupDietByDate(DietHistoryResponseDto dto) {
    HashMap<String, DietByDayDto> aggregated = new HashMap<>();

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
    List<DietByDayDto> result = new ArrayList<>(aggregated.values());
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    result.sort(Comparator.comparing((DietByDayDto var) -> df.parse(var.getDate(), new ParsePosition(0))));
    return result;
  }

  private List<ExerciseByDayDto> groupExerciseByDate(ExerciseHistoryResponseDto dto) {
    HashMap<String, ExerciseByDayDto> aggregated = new HashMap<String, ExerciseByDayDto>();

    for (ExerciseHistoryDetailsDto dhd : dto.getExerciseHistoryList()) {
      String date = dhd.getTime().split(" ")[0];
      if (!aggregated.containsKey(date)) {
        aggregated.put(date, new ExerciseByDayDto(date, 0.0, 0.0));
      }
      ExerciseByDayDto exerciseByDayDto = aggregated.get(date);
      exerciseByDayDto.setTotalCalories(exerciseByDayDto.getTotalCalories()
              + dhd.getTotalCalories());
      exerciseByDayDto.setTotalDuration(exerciseByDayDto.getTotalDuration()
              + dhd.getDuration());
    }
    List<ExerciseByDayDto> result = new ArrayList<>(aggregated.values());
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    result.sort(Comparator.comparing((ExerciseByDayDto var) -> df.parse(var.getDate(), new ParsePosition(0))));
    return result;
  }

  private List<WeightHistoryDetailsDto> groupWeightByDate(WeightHistoryResponseDto dto) {
    HashMap<String, WeightHistoryDetailsDto> aggregated = new HashMap<String, WeightHistoryDetailsDto>();
    HashMap<String, Integer> counter = new HashMap<String, Integer>();

    for (WeightHistoryDetailsDto dhd : dto.getWeightHistoryList()) {
      String date = dhd.getTime().split(" ")[0];
      if (!aggregated.containsKey(date)) {
        aggregated.put(date, new WeightHistoryDetailsDto(-1, 0.0, "gram", date));
        counter.put(date, 0);
      }
      WeightHistoryDetailsDto weightByDay = aggregated.get(date);
      weightByDay.setWeight(weightByDay.getWeight() + dhd.getWeight());
      counter.put(date, counter.get(date) + 1);
    }
    // calculate average
    for (String date : aggregated.keySet()) {
      WeightHistoryDetailsDto weightByDay = aggregated.get(date);
      weightByDay.setWeight(weightByDay.getWeight() / counter.get(date));
    }

    List<WeightHistoryDetailsDto> result = new ArrayList<>(aggregated.values());
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    result.sort(Comparator.comparing((WeightHistoryDetailsDto var)
            -> df.parse(var.getTime(), new ParsePosition(0))));
    return result;
  }


  private void getStats(AdviceDto adviceDto) {
    Double avgCalories = 0.0;
    Double avgFat = 0.0;
    Double avgProtein = 0.0;
    Double avgCarbs = 0.0;
    Double avgDuration = 0.0;
    Double avgExerciseCal = 0.0;

    List<DietByDayDto> dietHist = adviceDto.getDietByDate();
    for (DietByDayDto oneDay : dietHist) {
      avgCalories += oneDay.getTotalCalories();
      avgFat += oneDay.getTotalFat();
      avgProtein += oneDay.getTotalProtein();
      avgCarbs += oneDay.getTotalCarbs();
    }

    adviceDto.setAvgCalories(avgCalories / dietHist.size());
    adviceDto.setAvgFat(avgFat / dietHist.size());
    adviceDto.setAvgProtein(avgProtein / dietHist.size());
    adviceDto.setAvgCarbs(avgCarbs / dietHist.size());

    List<ExerciseByDayDto> exerciseHist = adviceDto.getExerciseByDate();
    for (ExerciseByDayDto oneDay : exerciseHist) {
      avgDuration += oneDay.getTotalDuration();
      avgExerciseCal += oneDay.getTotalCalories();
    }
    adviceDto.setAvgDuration(avgDuration / exerciseHist.size());
    adviceDto.setAvgExerciseCal(avgExerciseCal / exerciseHist.size());
  }
}
