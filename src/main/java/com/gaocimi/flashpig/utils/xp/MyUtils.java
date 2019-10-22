package com.gaocimi.flashpig.utils.xp;

import com.gaocimi.flashpig.controller.HairstylistController;
import com.gaocimi.flashpig.entity.Hairstylist;
import com.gaocimi.flashpig.entity.User;
import com.gaocimi.flashpig.entity.UserToHairstylist;
import com.gaocimi.flashpig.model.HairstylistInfo;
import com.gaocimi.flashpig.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 自己的工具类
 *
 * @author xp
 * @date 2019-10-12 20:59:43
 */
public class MyUtils {
    protected static final Logger logger = LoggerFactory.getLogger(HairstylistController.class);

    /**
     * @return 返回今天23点59分59秒的时间Data
     */
    public static Date getTodayLastTime() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * @return 返回今天00点00分00秒的时间Data
     */
    public static Date getTodayFirstTime() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
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
     * @return 获取传入日期所在月的第一天的日期Date(时间为00:00:00)
     */
    public static Date getFirstDayOfMonth(final Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int last = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, last);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTime();
    }


    /**
     * @return 获取传入日期所在周的星期一的日期Date(时间为00:00:00)
     */
    public static Date getFirstDayOfWeek(Date date) {

        Calendar calendar = Calendar.getInstance();

        // （外国是周天到下一周的周六为一周，即一周中数字1、2、3、4、5、6、7对应星期天、星期一、星期二、..、星期六）
        //  如果今天是星期天(对应数字1)，会获取到下一周的周一，所以将获取到的时间周数减1
        boolean flag = false;
        if(calendar.get(Calendar.DAY_OF_WEEK)==1) {
            flag = true;
        }

        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, 2);//如果为1的话是星期日
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if(flag)
            return stepWeek(calendar.getTime(),-1);
        else
            return calendar.getTime();
    }

    /**
     * 获取在给定的日期加上或减去指定月份后的日期
     *
     * @param sourceDate 原始时间
     * @param month      要调整的月份，向前为负数，向后为正数
     * @return 在给定的日期加上或减去指定月份后的日期
     */
    public static Date stepMonth(Date sourceDate, int month) {
        Calendar c = Calendar.getInstance();
        c.setTime(sourceDate);
        c.add(Calendar.MONTH, month);

        return c.getTime();
    }

    /**
     * 获取在给定的日期加上或减去指定周数后的日期
     *
     * @param sourceDate 原始时间
     * @param week      要调整的周数，向前为负数，向后为正数
     * @return 在给定的日期加上或减去指定周数后的日期
     */
    public static Date stepWeek(Date sourceDate, int week) {
        Calendar c = Calendar.getInstance();
        c.setTime(sourceDate);
        c.add(Calendar.WEEK_OF_YEAR, week);

        return c.getTime();
    }

    /**
     * 获取在给定的日期加上或减去指定天数后的日期
     *
     * @param sourceDate 原始时间
     * @param day      要调整的周数，向前为负数，向后为正数
     * @return 在给定的日期加上或减去指定天数后的日期
     */
    public static Date stepDay(Date sourceDate, int day) {
        Calendar c = Calendar.getInstance();
        c.setTime(sourceDate);
        c.add(Calendar.DAY_OF_MONTH, day);

        return c.getTime();
    }










    /**
     * @return 返回与今天相差的天数（例：明天为-1，今天为0，昨天为1）
     */
    public static Long getDifferenceToday(Date date) {
        Date todayLastTime = getTodayLastTime();
        Long days = todayLastTime.getTime() / 86400000 - date.getTime() / 86400000;//与今天23点59分相差天数
        return days;
    }

    /**
     * 用于删除7天前的Formid
     *
     * @return 返回与当前时间相差的天数（例：明天为-1，今天为0，昨天为1）
     */
    public static Long getDifferenceNow(Date date) {
        Date nowTime = new Date(System.currentTimeMillis());
        Long days = nowTime.getTime() / 86400000 - date.getTime() / 86400000;//与当前时刻相差的天数
        return days;
    }


    /**
     *
     * @param user 用户类
     * @param hairstylistId 发型师的id
     * @return 用户是否收藏了发型师
     */
    public static boolean isUserLoyalToHairstylist(User user, int hairstylistId) {
        List<UserToHairstylist> recordList = user.getHairstylistRecordList();
        if (recordList == null || recordList.size() == 0) {
            return false;
        }
        for (UserToHairstylist h : recordList) {
            if (h.getHairstylist().getId() == hairstylistId )
                return true;
        }
        return false;
    }


}
