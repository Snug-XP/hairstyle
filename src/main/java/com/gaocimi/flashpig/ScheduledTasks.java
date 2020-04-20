package com.gaocimi.flashpig;

import com.gaocimi.flashpig.controller.PushSubscribeMessageController;
import com.gaocimi.flashpig.entity.HaircutOrder;
import com.gaocimi.flashpig.entity.User;
import com.gaocimi.flashpig.service.HaircutOrderService;
import com.gaocimi.flashpig.service.UserService;
import com.gaocimi.flashpig.utils.xp.MyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务类
 */
@Component
public class ScheduledTasks{
    protected static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    PushSubscribeMessageController pushSubscribeMessageController;
    @Autowired
    HaircutOrderService haircutOrderService;
    @Autowired
    UserService userService;


    static int count = 0;

//    下面是原先的定时任务实现方式
//    static int timeout = 60*60;//单位秒
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        System.out.println("通过实现ApplicationRunner接口，在spring boot项目启动后自动执行的方法");
//
//        Long delay = MyUtils.getDifferenceNextHour();
//        System.out.println(delay+"秒后开始检查未完成订单（预约时间在当前时间之后的订单）");
//        //定期检查刷新数据... 	 开启一个线程，检查有效期...(过期自动删除缓存)
//        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
//        //定时周期任务(间隔时间重复执行)
//        scheduledThreadPool.scheduleAtFixedRate(() -> {
//            count++;
//            Date nowTime = new Date(System.currentTimeMillis());
//            System.out.println("每" + timeout + "秒执行一次，检查未完成订单,并通知还有60分钟左右的订单用户准备前往(" + count + ")");
//            List<HaircutOrder> haircutOrderList = haircutOrderService.findAllByStatusAfterNow(-1);//找到未完成订单
//            System.out.println("目前有" + haircutOrderList.size() + "个未完成订单");
//            for (HaircutOrder order : haircutOrderList) {
//                Long timeDiff = order.getBookTime().getTime() - nowTime.getTime();//与当前时刻相差的毫秒数
//
//                System.out.println("距离用户“"+order.getUser().getName()+"”的订单（id="+order.getId()+"）到达预约时间还有" + timeDiff/(60*1000)+"分钟(" + timeDiff/(60*60*1000)+"小时)（现在时间："+nowTime+"）");
//
//                if (timeDiff > (60-5)*60*1000 && timeDiff <= (60+5)*60*1000) {//找到还有接近60分钟内的订单
//                    pushSubscribeMessageController.pushComingMessage(order.getId());
//                }
//            }
//            System.out.println("\n\n");
//        }, delay, timeout, TimeUnit.SECONDS);
//        //参数第一次执行时间，间隔执行时间,执行时间单位
//    }


    /**
     * 每天9点到22点检查检查未完成订单（预约时间在当前时间之后的订单）
     */
    @Scheduled(cron = "0 0 9-22 * * *")
    public void checkComingHaircutOrder(){
        Date nowTime = new Date(System.currentTimeMillis());
        System.out.println("(9点到22点之间)每个小时执行一次，检查未完成订单,并通知还有60分钟左右的订单用户准备前往(" + count + ")");
        List<HaircutOrder> haircutOrderList = haircutOrderService.findAllByStatusAfterNow(-1);//找到未完成订单
        System.out.println("目前有" + haircutOrderList.size() + "个未完成订单：");
        for (HaircutOrder order : haircutOrderList) {
            Long timeDiff = order.getBookTime().getTime() - nowTime.getTime();//与当前时刻相差的毫秒数

            System.out.println("》》》距离用户“"+order.getUser().getName()+"”的订单（id="+order.getId()+"）到达预约时间还有" + timeDiff/(60*1000)+"分钟(" + timeDiff/(60*60*1000)+"小时)（现在时间："+nowTime+"）");

            if (timeDiff > (60-5)*60*1000 && timeDiff <= (60+5)*60*1000) {//找到还有接近60分钟内的订单
                pushSubscribeMessageController.pushComingMessage(order.getId());
            }
        }
        System.out.println("\n\n");

    }


    /**
     * 每天零点开始检查
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void checkVip(){
        logger.info("检查是否有会员过期...");

        try{
            Date nowTime = new Date(System.currentTimeMillis());
            List<User> userList = userService.findAllByIsVipAndVipEndTimeBefore(1,nowTime);
            for(User user : userList){
                user.setIsVip(0);
                userService.edit(user);
                logger.error("今天用户"+user.getName()+"(id="+user.getId()+")的普通会员已到期！");
            }
        }catch (Exception e){
            logger.error("检查过程中发生错误！");
        }
    }
}