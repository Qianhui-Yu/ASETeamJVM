package com.jvm.coms4156.columbia.wehealth.utility;

import static com.jvm.coms4156.columbia.wehealth.common.Constants.MONTH;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.WEEK;
import static com.jvm.coms4156.columbia.wehealth.common.Constants.YEAR;

import com.jvm.coms4156.columbia.wehealth.exception.BadRequestException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utility {
  /**
   * Convert a date to string of format "yyyy-MM-dd HH:mm:ss".
   *
   * @param date The date to convert.
   * @return String containing the formatted date.
   */
  public static String getStringFromDate(Date date) {
    String pattern = "yyyy-MM-dd HH:mm:ss";
    SimpleDateFormat df = new SimpleDateFormat(pattern);
    return df.format(date);
  }

  public static String getStringOfCurrentDateTime() {
    Date currentDate = new Date();
    return getStringFromDate(currentDate);
  }

  /**
   * Calculate the date that is equal to (current time - timeUnit * timeLength).
   *
   * @param timeUnit Unit for time. One of {YEAR, WEEK, MONTH}.
   * @param timeLength Length of time.
   * @return String describing the result date.
   */
  public static String getStringOfStartDateTime(String timeUnit, int timeLength) {
    Date currentDate = new Date();
    Calendar targetDateTime = Calendar.getInstance();
    targetDateTime.setTime(currentDate);
    switch (timeUnit) {
      case YEAR:
        targetDateTime.add(Calendar.YEAR, -timeLength);
        break;
      case WEEK:
        targetDateTime.add(Calendar.DAY_OF_YEAR, -7 * timeLength);
        break;
      case MONTH:
        targetDateTime.add(Calendar.MONTH, -timeLength);
        break;
      default:
        throw new BadRequestException("Invalid time unit.");
    }
    Date startDateTime = targetDateTime.getTime();
    return getStringFromDate(startDateTime);
  }

  /**
   * Calculate days between two days in String format.
   *
   * @param date1 The first date.
   * @param date2 The second date.
   * @return Integer days between two days.
   */
  public static int getDaysBetween(String date1, String date2) throws ParseException {
    String pattern = "yyyy-MM-dd HH:mm:ss";
    SimpleDateFormat df = new SimpleDateFormat(pattern);
    Calendar cal = Calendar.getInstance();
    cal.setTime(df.parse(date1));
    long time1 = cal.getTimeInMillis();
    cal.setTime(df.parse(date2));
    long time2 = cal.getTimeInMillis();
    long betweenDays = 1 + Math.abs(time2 - time1) / (1000 * 3600 * 24);
    return Integer.parseInt(String.valueOf(betweenDays));
  }
}
