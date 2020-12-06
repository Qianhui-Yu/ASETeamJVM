package com.jvm.coms4156.columbia.wehealth.service;

import static com.jvm.coms4156.columbia.wehealth.common.Constants.DEFAULT_WEIGHT;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.GOOD_AVG_CALORIES;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.GOOD_AVG_CARBS;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.GOOD_AVG_FAT;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.GOOD_AVG_PROTEIN;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.GOOD_DURATION;

import com.jvm.coms4156.columbia.wehealth.domain.AuthenticatedUser;
import com.jvm.coms4156.columbia.wehealth.dto.AdviceDto;
import com.jvm.coms4156.columbia.wehealth.dto.DietByDayDto;
import com.jvm.coms4156.columbia.wehealth.dto.DietHistoryDetailsDto;
import com.jvm.coms4156.columbia.wehealth.dto.DietHistoryResponseDto;
import com.jvm.coms4156.columbia.wehealth.dto.ExerciseByDayDto;
import com.jvm.coms4156.columbia.wehealth.dto.ExerciseHistoryDetailsDto;
import com.jvm.coms4156.columbia.wehealth.dto.ExerciseHistoryResponseDto;
import com.jvm.coms4156.columbia.wehealth.dto.WeightHistoryDetailsDto;
import com.jvm.coms4156.columbia.wehealth.dto.WeightHistoryResponseDto;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
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
  public AdviceDto getAdvice(AuthenticatedUser user,
                             Optional<Integer> length,
                             Optional<String> unit) {
    AdviceDto adviceDto = new AdviceDto();
    adviceDto.setIsEmpty(true);

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
    Double weightProportion = 1.0;
    Random rand = new Random();
    if (!adviceDto.getAvgWeight().isNaN()) {
      weightProportion = adviceDto.getAvgWeight() / DEFAULT_WEIGHT;
    }

    if (!adviceDto.getAvgCalories().isNaN()) {
      if (adviceDto.getAvgCalories() < GOOD_AVG_CALORIES * 0.7 * weightProportion) {
        Double extra = 1 - adviceDto.getAvgCalories() / (GOOD_AVG_CALORIES *  weightProportion);
        suggestion = String.format("You avg calories intake is %.1f%% lower than "
                + "the recommended level. Please eat more!", extra * 100);

      } else if (adviceDto.getAvgCalories() > GOOD_AVG_CALORIES * 1.3 * weightProportion) {
        Double cut = adviceDto.getAvgCalories() / (GOOD_AVG_CALORIES *  weightProportion) - 1;
        suggestion = String.format("You avg calories intake is %.1f%% higher than"
                + " the recommended level. Please eat less to control your weight.", cut * 100);
      } else {
        suggestion = " Your calories intake is just about right!";
      }
    } else {
      suggestion = "No record";
    }
    adviceDto.setCaloriesAdvice(suggestion);

    // protein recommendation
    if (! adviceDto.getAvgProtein().isNaN()) {
      String[] proteinFood = new String[]{"Eggs", "Chicken Breast", "Lean Beef", "Beans"};
      if (adviceDto.getAvgProtein() < GOOD_AVG_PROTEIN * 0.7 * weightProportion) {
        Double extra = 1 - adviceDto.getAvgProtein() / (GOOD_AVG_PROTEIN * weightProportion);
        suggestion = String.format("Low protein intake (%.1f%% less than recommended)"
                + ", some food idea for you: ", extra * 100);
        suggestion += proteinFood[rand.nextInt(proteinFood.length)];

      } else if (adviceDto.getAvgProtein() > GOOD_AVG_PROTEIN * 1.3 * weightProportion) {
        Double cut = adviceDto.getAvgProtein() / (GOOD_AVG_PROTEIN * weightProportion) - 1;
        suggestion = String.format("Too much protein intake, recommended cutting"
                + " %.1f%% of high protein food intake.", cut * 100);
      } else {
        suggestion = " Your protein intake is just about right!";
      }
    } else {
      suggestion = "No record";
    }
    adviceDto.setProteinAdvice(suggestion);

    // fat recommendation
    if (! adviceDto.getAvgFat().isNaN()) {
      String[] fatFood = new String[] { "Avocado", "Eggs", "Fish", "Assorted Nuts"};
      if (adviceDto.getAvgFat() < GOOD_AVG_FAT * 0.7 * weightProportion) {
        Double extra = 1 - adviceDto.getAvgFat() / (GOOD_AVG_FAT * weightProportion);
        suggestion = String.format("Low fat intake (%.1f%% less than recommended)"
                + ", some food idea for you: ", extra * 100);
        suggestion += fatFood[rand.nextInt(fatFood.length)];

      } else if (adviceDto.getAvgFat() > GOOD_AVG_FAT * 1.3 * weightProportion) {
        Double cut = adviceDto.getAvgFat() / (GOOD_AVG_FAT * weightProportion) - 1;
        suggestion = String.format("Too much fat intake, recommended cutting"
                + " %.1f%% of high fat food intake.", cut * 100);
      } else {
        suggestion = " Your fat intake is just about right!";
      }
    } else {
      suggestion = "No record";
    }
    adviceDto.setFatAdvice(suggestion);

    // carbs recommendation
    if (! adviceDto.getAvgCarbs().isNaN()) {
      String[] carbFood = new String[]{"Bread", "Rice", "Oats"};
      if (adviceDto.getAvgCarbs() < GOOD_AVG_CARBS * 0.7 * weightProportion) {
        Double extra = 1 - adviceDto.getAvgCarbs() / (GOOD_AVG_CARBS * weightProportion);
        suggestion = String.format("Low carbs intake (%.1f%% less than recommended)"
                + ", some food idea for you: ", extra * 100);
        suggestion += carbFood[rand.nextInt(carbFood.length)];

      } else if (adviceDto.getAvgCarbs() > GOOD_AVG_CARBS * 1.3 * weightProportion) {
        Double cut = adviceDto.getAvgCarbs() / (GOOD_AVG_CARBS * weightProportion) - 1;
        suggestion = String.format("Too much carbs intake, recommended cutting"
                + " %.1f%% of high carbs food intake.", cut * 100);
      } else {
        suggestion = " Your carbs intake is just about right!";
      }
    } else {
      suggestion = "No record";
    }
    adviceDto.setCarbsAdvice(suggestion);

    // exercise recommendation
    if (! adviceDto.getAvgDuration().isNaN()) {
      if (adviceDto.getAvgDuration() < GOOD_DURATION) {
        suggestion = "We recommend 30 mins of mild to medium intensity exercise every day!";
      } else {
        suggestion = "You are killing the daily exercises target!";
      }
    } else {
      suggestion = "No record";
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
    result.sort(Comparator.comparing((DietByDayDto var)
        -> df.parse(var.getDate(), new ParsePosition(0))));
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
    result.sort(Comparator.comparing((ExerciseByDayDto var)
        -> df.parse(var.getDate(), new ParsePosition(0))));
    return result;
  }

  private List<WeightHistoryDetailsDto> groupWeightByDate(WeightHistoryResponseDto dto) {
    HashMap<String, WeightHistoryDetailsDto> aggregated = new HashMap<String,
            WeightHistoryDetailsDto>();
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
    //    for (String date : aggregated.keySet()) {
    //      WeightHistoryDetailsDto weightByDay = aggregated.get(date);
    //      weightByDay.setWeight(weightByDay.getWeight() / counter.get(date));
    //    }
    for (Map.Entry<String, WeightHistoryDetailsDto> entry : aggregated.entrySet()) {
      WeightHistoryDetailsDto weightByDay = entry.getValue();
      weightByDay.setWeight(weightByDay.getWeight() / counter.get(entry.getKey()));
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
    Double avgWeight = 0.0;

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

    List<WeightHistoryDetailsDto> weightHist = adviceDto.getWeightByDate();
    for (WeightHistoryDetailsDto oneDay : weightHist) {
      avgWeight += oneDay.getWeight();
    }
    adviceDto.setAvgWeight(avgWeight / weightHist.size());
  }
}
