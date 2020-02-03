package com.gaocimi.flashpig.utils.xp;

import com.gaocimi.flashpig.controller.HairstylistController;
import com.gaocimi.flashpig.entity.Article;
import com.gaocimi.flashpig.entity.Hairstylist;
import com.gaocimi.flashpig.entity.User;
import com.gaocimi.flashpig.entity.UserToHairstylist;
import com.gaocimi.flashpig.model.HairstylistInfo;
import com.gaocimi.flashpig.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * @return 获取传入日期所在月的第一天的日期Date(时间为00 : 00 : 00)
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
     * @return 获取传入日期所在周的星期一的日期Date(时间为00 : 00 : 00)
     */
    public static Date getFirstDayOfWeek(Date date) {

        Calendar calendar = Calendar.getInstance();

        // （外国是周天到下一周的周六为一周，即一周中数字1、2、3、4、5、6、7对应星期天、星期一、星期二、..、星期六）
        //  如果今天是星期天(对应数字1)，会获取到下一周的周一，所以将获取到的时间周数减1
        boolean flag = false;
        if (calendar.get(Calendar.DAY_OF_WEEK) == 1) {
            flag = true;
        }

        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, 2);//如果为1的话是星期日
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (flag)
            return stepWeek(calendar.getTime(), -1);
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
     * @param week       要调整的周数，向前为负数，向后为正数
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
     * @param day        要调整的周数，向前为负数，向后为正数
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
     * 仅用于删除7天前的Formid
     *
     * @return 返回与当前时间相差的天数（例：明天为-1，今天为0，昨天为1）
     */
    public static Long getDifferenceNow(Date date) {
        Date nowTime = new Date(System.currentTimeMillis());
        Long days = nowTime.getTime() / 86400000 - date.getTime() / 86400000;//与当前时刻相差的天数
        return days;
    }

    /**
     * 获取当前系统时间和下一个整点时间相差的秒数
     */
    public static Long getDifferenceNextHour() {
        Calendar c = Calendar.getInstance();//加一个小时,设置对应的分和秒为0则得到了下一个整点的时间.
        c.add(Calendar.HOUR, 1);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Long diff = (c.getTime().getTime() - System.currentTimeMillis()) / 1000;
        return diff;
    }

    /**
     * 将列表分页，返回分页内容
     *
     * @return
     */
    public static <T> Page<T> getPage(List<T> list, int pageNum, int pageSize) {
        int first = pageNum * pageSize;//该页第一个元素位置
        int last = pageNum * pageSize + pageSize - 1;//该页最后一个元素位置

        List<T> resultList = new ArrayList<>();

        for (int i = first; i <= last && i < list.size(); i++) {
            resultList.add(list.get(i));
        }

        //包装分页数据
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<T> page = new PageImpl<>(resultList, pageable, list.size());

        return page;
    }

    /**
     * 判断电话号码是否符合格式
     *
     * @param mobiles 11位电话号码
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        boolean flag = false;
        try {
            //13********* ,15********,18*********
            Pattern p = Pattern
                    .compile("^(1)\\d{10}$");
//                    .compile("^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$");
            Matcher m = p.matcher(mobiles);
            flag = m.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }


    private static double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 通过经纬度获取距离(单位：米)
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static double getDistance(double lat1, double lng1, double lat2,
                                     double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        s = s * 1000;
        return s;
    }


    /**
     * 获取时间字符串
     */
    public static String getTimeString(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");//精确到毫秒
//        SimpleDateFormat df =new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");

        return df.format(date);
    }

    /**
     * 获取日期年份
     *
     * @param date 日期
     * @return
     */
    public static String getYear(Date date) {
        return getTimeString(date).substring(0, 4);
    }

    /**
     * 功能描述：返回月
     *
     * @param date Date 日期
     * @return 返回月份
     */
    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 功能描述：返回日期
     *
     * @param date Date 日期
     * @return 返回日份
     */
    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 功能描述：返回小时
     *
     * @param date 日期
     * @return 返回小时
     */
    public static int getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 功能描述：返回分
     *
     * @param date 日期
     * @return 返回分钟
     */
    public static int getMinute(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 返回秒钟
     *
     * @param date Date 日期
     * @return 返回秒钟
     */
    public static int getSecond(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.SECOND);
    }

    /**
     * 功能描述：返回毫
     *
     * @param date 日期
     * @return 返回毫
     */
    public static long getMillis(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getTimeInMillis();
    }
}
