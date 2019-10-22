package com.kingsoft.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeUtil {
    private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private static DateFormat sourceWithecond = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    private static SimpleDateFormat formatHour = new SimpleDateFormat("yyyyMMdd HH");

    public Long getDaysAgo(Integer day) {
        Long res = 0l;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -day);
        try {
            res = format.parse(format.format(cal.getTime())).getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }

    public Long parsedayWhitsecond(String day) {
        Long res = 0L;
        try {
            res = format.parse(day).getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    }


}
