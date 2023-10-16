package com.zcunsoft.clklog.api.utils;

import java.sql.Timestamp;
import java.util.Calendar;

public class TimeUtils {
    public static long[] getCurrentWeekTimeFrame(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        //start of the week
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        }
        calendar.add(Calendar.DAY_OF_WEEK, -(calendar.get(Calendar.DAY_OF_WEEK) - 2));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startTime = calendar.getTimeInMillis();

        //end of the week
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        long endTime = calendar.getTimeInMillis();
        return new long[]{startTime, endTime};
    }

    public static long[] getCurrentMonthTimeFrame(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        //start of the month
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startTime = calendar.getTimeInMillis();

        //end of the month
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        long endTime = calendar.getTimeInMillis();

        return new long[]{startTime, endTime};
    }

    public static long[] getCurrentYearTimeFrame(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        //start of the year
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startTime = calendar.getTimeInMillis();

        //end of the year
        calendar.add(Calendar.YEAR, 1);
        calendar.add(Calendar.DATE, -1);
        long endTime = calendar.getTimeInMillis();

        return new long[]{startTime, endTime};
    }
    
    /**
     * 把时间戳转换为：时分秒
     * @param second
     * @return
     */
    public static String getTimeString(long second) {
        if (second < 1) {
            return "00:00:00";
        }
//        long millisecond = second*1000;
//        long second = millisecond / 1000;
        long seconds = second % 60;
        long minutes = second / 60;
        long hours = 0;
        if (minutes >= 60) {
            hours = minutes / 60;
            minutes = minutes % 60;
        }
        String timeString = "";
        String secondString = "";
        String minuteString = "";
        String hourString = "";
        if (seconds < 10) {
            secondString = "0" + seconds;
        } else {
            secondString = seconds + "";
        }
        if (minutes < 10 && hours < 1) {
            minuteString = "0"+minutes + "";
        } else if (minutes < 10){
            minuteString =  "0" + minutes + "";
        } else {
            minuteString = minutes + "";
        }
        if (hours < 10) {
            hourString = "0"+hours + "";
        } else {
            hourString = hours + "" + "";
        }
        if (hours != 0) {
            timeString = hourString +":"+ minuteString +":"+ secondString;
        } else {
            timeString = "00:"+minuteString +":"+ secondString;
        }
        return timeString;
    }

}
