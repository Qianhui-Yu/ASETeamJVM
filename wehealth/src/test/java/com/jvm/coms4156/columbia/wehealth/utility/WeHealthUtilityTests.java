package com.jvm.coms4156.columbia.wehealth.utility;

import static com.jvm.coms4156.columbia.wehealth.common.Constants.MONTH;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.WEEK;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.YEAR;

import com.jvm.coms4156.columbia.wehealth.exception.BadRequestException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WeHealthUtilityTests {
  @Test
  public void getStringFromDateTest() {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    try {
      String testDateString = "2020-11-16 11:11:11";
      Date testDate = df.parse(testDateString);
      Assertions.assertEquals(Utility.getStringFromDate(testDate), testDateString);
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  /* TODO(Derek Jin): using Date() to get current time makes it difficult to test
          Consider changing implementation to use java.time.Clock
   */
  @Test
  public void getStringOfCurrentDateTimeTest() {
    Utility.getStringOfCurrentDateTime();
  }

  @Test
  public void getStringOfStartDateTimeInvalidUnit() {
    Assertions.assertThrows(BadRequestException.class, () ->
            Utility.getStringOfStartDateTime("BadTestUnit", 1)
    );
  }

  @Test
  public void getStringOfStartDateTimeYear() {
    Utility.getStringOfStartDateTime(YEAR, 1);
  }

  @Test
  public void getStringOfStartDateTimeWeek() {
    Utility.getStringOfStartDateTime(WEEK, 1);
  }

  @Test
  public void getStringOfStartDateTimeMonth() {
    Utility.getStringOfStartDateTime(MONTH, 1);
  }

}