package com.gaocimi.flashpig.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * 自己的工具类
 *
 * @author xp
 * @date 2019-10-12 20:59:43
 */
public class MyUtils {

    /**
     * @return 返回今天23点59分59秒的时间Data
     */
    public static Date getTodayLastTime() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date todayLastTime = calendar.getTime();
        return todayLastTime;
    }

    /**
     * @return 返回数字对应的整点时间Data
     */
    public static Date getTime(int hourTime) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, hourTime);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date time = calendar.getTime();
        return time;
    }

    /**
     * @return 返回与今天相差的天数（例：明天为-1，今天为0，昨天为1）
     */
    public static Long getDifferenceToday(Date date) {
        Date todayLastTime = getTodayLastTime();
        Long days = todayLastTime.getTime() / 86400000 - date.getTime() / 86400000;//与今天23点59分相差天数
        return days;
    }

}
