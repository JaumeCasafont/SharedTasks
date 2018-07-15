package com.jcr.sharedtasks.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class TimeUtils {

    public static String getDateFormatted(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d, ''yy");
        return simpleDateFormat.format(calendar.getTime());
    }

    public static long getDateInMillis(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTimeInMillis();
    }

    public static String getDateFormatted(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d, ''yy");
        return simpleDateFormat.format(calendar.getTime());
    }

    public static long getDateFormatted(String dateToFormat) {
        Date date = new Date();
        try {
            date = new SimpleDateFormat("EEE, MMM d, ''yy").parse(dateToFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public static Integer[] getDateToCalendar(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return getSplitDate(calendar);
    }

    public static Integer[] getNow() {
        Calendar calendar = Calendar.getInstance();
        return getSplitDate(calendar);
    }

    private static Integer[] getSplitDate(Calendar calendar) {
        Integer[] splitDate = new Integer[3];
        splitDate[0] = calendar.get(Calendar.YEAR);
        splitDate[1] = calendar.get(Calendar.MONTH);
        splitDate[2] = calendar.get(Calendar.DAY_OF_MONTH);
        return splitDate;
    }
}
