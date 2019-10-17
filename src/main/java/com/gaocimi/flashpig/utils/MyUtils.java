package com.gaocimi.flashpig.utils;

import com.gaocimi.flashpig.controller.HairstylistController;
import com.gaocimi.flashpig.entity.Hairstylist;
import com.gaocimi.flashpig.entity.User;
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

    /**
     * @return 返回与当前时间相差的天数（例：明天为-1，今天为0，昨天为1）
     */
    public static Long getDifferenceNow(Date date) {
        Date nowTime = new Date(System.currentTimeMillis());
        Long days = nowTime.getTime() / 86400000 - date.getTime() / 86400000;//与当前时刻相差的天数
        return days;
    }


    public static boolean isUserLoyalToHairstylist(User user, Hairstylist hairstylist){
        List<Hairstylist> hairstylists = user.getHairstylistList();
        if(hairstylists==null||hairstylists.size()==0) {
            return false;
        }
        for(Hairstylist h: hairstylists){
            if(h.getId()==hairstylist.getId())
                return true;
        }
        return false;
    }


}
