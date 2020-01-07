package com.gaocimi.flashpig.controller;

import com.gaocimi.flashpig.entity.*;
import com.gaocimi.flashpig.model.HairstylistReservation;
import com.gaocimi.flashpig.model.OrderRecordFromOneUser;
import com.gaocimi.flashpig.model.UserReservation;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.*;
import com.gaocimi.flashpig.utils.xp.MyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author xp
 * @date 2019-10-13 19:12:25
 * @description 订单的相关业务
 */
@RestController
@ResponseResult
@Api(value = "订单操作服务", description = "订单的相关业务")
public class HaircutOrderController {
    protected static final Logger logger = LoggerFactory.getLogger(HairstylistController.class);

    @Autowired
    HaircutOrderService haircutOrderService;
    @Autowired
    AdministratorService administratorService;
    @Autowired
    HairstylistService hairstylistService;
    @Autowired
    UserService userService;
    @Autowired
    HairServiceService hairServiceService;
    @Autowired
    PushTemplateMessageController messageController;
    @Autowired
    PushSubscribeMessageController pushWxMsg;

    @ApiOperation(value = "获取一个时间段的预约列表(按预约时间倒序排序，若预约时间一致，按创建时间倒序)-用于“发型师-预约列表”页面", notes = "days的值表示获取几天前到现在的数据，days默认为0，表示只获取今天的订单,days=1表示获取昨天和今天的所有订单,days=-1表示获取今天之后的所有订单")
    @GetMapping("/hairstylist/getOrderList")
    public Map getOrderList(@RequestParam String myOpenid,
                            @RequestParam(defaultValue = "0") int days) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null) {
                logger.info("非发型师用户操作！！（获取一个时间段的预约列表）");
                map.put("error", "对不起，你不是发型师用户，无权操作！！");
                return map;
            }
            List<HaircutOrder> tempOrderList = hairstylist.haircutOrderList;
            List<HaircutOrder> resultOrderList = new ArrayList<>();
            Date orderBookTime;

            for (HaircutOrder order : tempOrderList) {
                orderBookTime = order.getBookTime();
                Long day = MyUtils.getDifferenceToday(orderBookTime);//取得预约时间与今天23点59分相差的天数
//                    System.out.println("订单id="+order.getId()+"的预约时间为"+orderBookTime+"，  与今天相差"+day+"天");
                if (days == -1) {
                    if (day < 0) resultOrderList.add(order);
                } else if (0 <= day && day <= days) {
                    resultOrderList.add(order);
                }
            }

            //在获取到的预约订单列表中筛选用于“发型师-预约列表”页面所需的信息
            if (resultOrderList.size() > 0) {
                List<HairstylistReservation> recordList = new ArrayList<>();
                for (HaircutOrder order : resultOrderList) {
                    HairstylistReservation record = new HairstylistReservation(order);
                    recordList.add(record);
                }

                // 先按创建时间倒序排序一遍
                Collections.sort(recordList, (r1, r2) -> {
                    if (r1.getCreateTime().after(r2.getCreateTime())) {
                        return -1;
                    } else if (r2.getCreateTime().after(r1.getCreateTime())) {
                        return 1;
                    }
                    return 0; //相等为0
                });

                // 再按预约时间倒序排序
                Collections.sort(recordList, (r1, r2) -> {
                    if (r1.getBookTime().after(r2.getBookTime())) {
                        return -1;
                    } else if (r2.getBookTime().after(r1.getBookTime())) {
                        return 1;
                    }
                    return 0; //相等为0
                });

                map.put("recordList", recordList);
                map.put("orderSum", recordList.size());
            } else
                map.put("message", "无订单!");

            return map;

        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取预约列表失败！！（后端发生某些错误）");
            map.put("error", "获取预约列表失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "获取今天、明天、或全部的预约列表(今天和明天的按预约时间顺序排序，全部的按预约时间倒序排序，若预约时间一致，按创建时间排序)-用于“发型师-预约列表”页面", notes = "flag默认为0，表示只获取今天的订单,flag=-1表示获取明天的订单,flag=1表示获取所有的订单")
    @GetMapping("/hairstylist/getOrderListByFlag")
    public Map getOrderListByFlag(@RequestParam String myOpenid,
                                  @RequestParam(defaultValue = "0") int flag) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null) {
                logger.info("非发型师用户操作！！（获取今天、明天、或全部的预约列表）");
                map.put("error", "对不起，你不是发型师用户，无权操作！！");
                return map;
            }
            List<HaircutOrder> tempOrderList = hairstylist.haircutOrderList;
            List<HaircutOrder> resultOrderList = new ArrayList<>();
            Date orderBookTime;

            if (flag == 1) {
                //获取所有的预约列表
                resultOrderList.addAll(tempOrderList);
            } else if (flag == 0 || flag == -1) {
                //获取今天或明天的预约列表
                for (HaircutOrder order : tempOrderList) {
                    orderBookTime = order.getBookTime();
                    Long day = MyUtils.getDifferenceToday(orderBookTime);//取得预约时间与今天23点59分相差的天数
//                    System.out.println("订单id=" + order.getId() + "的预约时间为" + orderBookTime + "，  与今天相差" + day + "天");
                    if (day == flag) resultOrderList.add(order);
                }
            } else {
                logger.info("获取今天、明天、或全部的预约列表,flag标志错误：" + flag);
                map.put("error", "flag标志错误：" + flag);
                return map;
            }

            //在获取到的预约订单列表中筛选用于“发型师-预约列表”页面所需的信息
            if (resultOrderList.size() > 0) {
                List<HairstylistReservation> recordList = new ArrayList<>();
                for (HaircutOrder order : resultOrderList) {
                    HairstylistReservation record = new HairstylistReservation(order);
                    recordList.add(record);
                }

                if (flag == 1) {//获取的全部预约列表按时间倒序排序

                    // 先按创建时间倒序排序一遍
                    Collections.sort(recordList, (r1, r2) -> {
                        if (r1.getCreateTime().after(r2.getCreateTime())) {
                            return -1;
                        } else if (r2.getCreateTime().after(r1.getCreateTime())) {
                            return 1;
                        }
                        return 0; //相等为0
                    });

                    // 再按预约时间倒序排序
                    Collections.sort(recordList, (r1, r2) -> {
                        if (r1.getBookTime().after(r2.getBookTime())) {
                            return -1;
                        } else if (r2.getBookTime().after(r1.getBookTime())) {
                            return 1;
                        }
                        return 0; //相等为0
                    });
                } else {
                    // 先按创建时间顺序排序一遍
                    Collections.sort(recordList, (r1, r2) -> {
                        if (r1.getCreateTime().after(r2.getCreateTime())) {
                            return 1;
                        } else if (r2.getCreateTime().after(r1.getCreateTime())) {
                            return -1;
                        }
                        return 0; //相等为0
                    });

                    // 再按预约时间顺序排序
                    Collections.sort(recordList, (r1, r2) -> {
                        if (r1.getBookTime().after(r2.getBookTime())) {
                            return 1;
                        } else if (r2.getBookTime().after(r1.getBookTime())) {
                            return -1;
                        }
                        return 0; //相等为0
                    });
                }


                map.put("recordList", recordList);
                map.put("orderSum", recordList.size());
            } else
                map.put("message", "无订单!");

            return map;

        } catch (
                Exception e) {
            logger.error(e.getMessage());
            logger.info("获取预约列表失败！！（后端发生某些错误）");
            map.put("error", "获取预约列表失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }

    }

    /**
     * 对今天的订单,进行订单区分统计，并返回未完成的有效订单信息列表
     *
     * @param reservationList 今天的所有订单
     * @return 正在进行和将要进行的订单信息列表以及订单区分的数据
     */
    public Map doSth(List<HairstylistReservation> reservationList) {
        Map map = new HashMap();

        int todayOrderNum;  //今天有效的的订单数
        int completedOrderNum = 0;  //已完成订单数
        int canceledOrderNum = 0;   //已被取消的订单数
        int remainingOrderNum; //未完成的有效订单数

        todayOrderNum = reservationList.size(); //先将有效订单数等于今日订单总数，统计时再减去无效的的订单数

        //订单状态，“-1”表示待完成，“0”表示已通知用户准备，“1”表示订单正在进行中，“2”表示订单已完成，“-2”表示订单已取消
        List<HairstylistReservation> resultList = new ArrayList<>();//要返回的排号订单列表
        for (int i = 0; i < reservationList.size(); i++) {
            HairstylistReservation reservation = reservationList.get(i);
            switch (reservation.getStatus()) {
                case -1:
                    //待完成的订单,加入排号订单列表中
                case 0:
                    //已通知用户准备的订单,加入排号订单列表中
                case 1:
                    //当前正在进行中的订单,加入排号订单列表中
                    reservation.setIndex(i + 1);
                    resultList.add(reservation); //加入排号订单列表
                    break;
                case 2:
                    completedOrderNum++; //已完成订单数+1
                    break;
                case -2:
                    canceledOrderNum++;  //已被取消的订单数+1
                    todayOrderNum--;  //有效订单-1
                    break;
                default:
                    logger.info("id为" + reservation.getOrderId() + "的订单状态异常！！");
                    todayOrderNum--;  //有效订单-1
                    break;
            }
        }

        remainingOrderNum = todayOrderNum - completedOrderNum;//剩余订单数=有效订单数-已完成订单数

        map.put("todayOrderNum", todayOrderNum);//今天有效的的订单数
        map.put("completedOrderNum", completedOrderNum);//已完成订单数
        map.put("canceledOrderNum", canceledOrderNum);//已被取消的订单数
        map.put("remainingOrderNum", remainingOrderNum);//未完成的有效订单数
        map.put("resultList", resultList);//排号订单列表（未完成的有效订单信息列表）

        return map;
    }

    @ApiOperation(value = "获取当前的排号列表(按预约时间顺序排序，若预约时间一致，按订单创建时间顺序) - 用于“发型师-排号系统”页面")
    @GetMapping("/hairstylist/getWaitingOrder")
    public Map getWaitingOrder(@RequestParam String myOpenid) {
        Map map = new HashMap();

        //获取今天的所有订单(按时间倒序排序的)
        map = getOrderList(myOpenid, 0);
        List<HairstylistReservation> reservationList = (List<HairstylistReservation>) map.get("recordList");

        //今天没订单或出错了
        if (reservationList == null) {
//            logger.info(map.toString());
            return map;//信息都在map里面
        }

        Collections.reverse(reservationList);//将获取到的今天的所有订单按时间正序（因为原来是按预约时和创建时间时间倒序获取到的）

        map = doSth(reservationList);//对今天的订单,进行订单区分统计
        return map;
    }

    @ApiOperation(value = "下一位操作，如果有进行的订单，将进行中的订单状态设为已完成；通知下一位前来美发，并通知再后2位准备前往 - 用于“发型师-排号系统”页面的通知的“下一位”操作")
    @GetMapping("/hairstylist/nextWaitingOrder")
    public Map nextWaitingOrder(@RequestParam String myOpenid) {
        Map map = new HashMap();

        map = getWaitingOrder(myOpenid);  //获取当前的排号列表
        List<HairstylistReservation> reservationList = (List<HairstylistReservation>) map.get("resultList");
        //今天没订单或出错了
        if (reservationList == null || reservationList.size() == 0) {
//            logger.info(map.toString());
            return map; //提示信息都在map里面
        }

        /**预约订单状态，“-1”表示待完成，“0”表示已通知用户准备， “1”表示订单正在进行中，“2”表示订单已完成，“-2”表示订单已取消*/

        //获取排队列表的第一个订单
        HairstylistReservation reservation = reservationList.get(0);
        HaircutOrder order = haircutOrderService.findHaircutOrderById(reservation.getOrderId());

        //通知排队列表下一顾客（排队列表中的第一个或第二个顾客）前来美发
        if (order.getStatus() == -1 || order.getStatus() == 0) {
            //如果排队列表的第一个订单为“待完成”或“已通知”
            //将该订单状态改为“进行中”，并通知用户前来美发
            reservation.setStatus(1);
            map = notifyUser(order.getId(), 0);//通知用户可以前来美发了
            if (map.get("error") != null) return map;

        } else if (order.getStatus() == 1) {
            //如果排队列表的第一个订单正在进行
            //将进行中的订单状态改为“已完成”
            reservation.setStatus(2);
            map = notifyUser(order.getId(), -1);//发送订单已完成，请评价的通知
            if (map.get("error") != null) return map;

            //如果排队列表有第二个订单,通知该订单顾客前来美发
            if (reservationList.size() > 1) {

                //获取排队列表的第二个订单信息
                reservation = reservationList.get(1);
                order = haircutOrderService.findHaircutOrderById(reservation.getOrderId());

                if (order.getStatus() == -1 || order.getStatus() == 0) {
                    //如果该订单为“待完成”或“已通知”
                    //将该订单状态改为“进行中”，并通知用户前来美发
                    reservation.setStatus(1);
                    map = notifyUser(order.getId(), 0);//通知用户可以前来美发了
                    if (map.get("error") != null) return map;
                } else {
                    logger.info("逻辑错误，排队列表的第二个订单状态异常！状态为" + order.getStatus()
                            + "\n<“-1”表示待完成，“0”表示已通知用户准备， “1”表示订单正在进行中，“2”表示订单已完成，“-2”表示订单已取消>\n");
                    map.put("error", "逻辑错误，排队列表的第二个订单异常！状态为" + order.getStatus()
                            + "\n<“-1”表示待完成，“0”表示已通知用户准备， “1”表示订单正在进行中，“2”表示订单已完成，“-2”表示订单已取消>");
                    return map;
                }
            }
        } else {
            logger.info("逻辑错误，排队列表的第一个订单状态异常！状态为" + order.getStatus()
                    + "\n<“-1”表示待完成，“0”表示已通知用户准备， “1”表示订单正在进行中，“2”表示订单已完成，“-2”表示订单已取消>\n");
            map.put("error", "逻辑错误，排队列表的第一个订单状态异常！状态为" + order.getStatus()
                    + "\n<“-1”表示待完成，“0”表示已通知用户准备， “1”表示订单正在进行中，“2”表示订单已完成，“-2”表示订单已取消>");
            return map;
        }//通知排队列表下一顾客前来美发结束

        //下面通知后两位顾客进行准备
        int count = 0;
        for (HairstylistReservation r : reservationList) {
            if (r.getStatus() != 1 && r.getStatus() != 2) {//找到前两个状态不是进行中或已完成的预约订单进行通知（本排号列表只可能存在已完成、进行中、已通知、待完成的订单，不会出现已取消和状态异常的订单）
                notifyUser(r.getOrderId(), ++count);
            }
            if (count == 2) break;
        }

        map = getWaitingOrder(myOpenid);
        //重新获取当前的排号列表并返回
        return map;
    }


    /**
     * @param orderId 要通知的订单id
     * @param flag    通知用户前面还有几个人，当flag=-1时，通知订单已完成
     * @return
     */
    @ApiOperation(value = "发模板信息通知用户前面还有flag个人（flag=0/1/2/-1），当flag=-1时，通知订单已完成，并且根据通知类型改变订单状态" +
            "（...到时候记得关闭url访问）", notes = "当flag > 0 时，通知用户前面还有flag个人，" +
            "并将订单状态改为0（已通知）     当flag = 0 时，通知用户前往接受服务，并将订单状态改为1（进行中）     " +
            "当flag = -1 时，通知用户订单已完成，并将订单状态改为2（已完成）")
    @GetMapping("/hairstylist/notifyUser")
    public Map notifyUser(@RequestParam Integer orderId, @RequestParam Integer flag) {
        Map map = new HashMap();
        //错误flag
        if (flag < -1) {
            logger.info("通知flag错误！！(仅允许大于等于-1的整数)");
            map.put("error", "通知flag错误！！(仅允许大于等于-1的整数)");
            return map;
        }
        HaircutOrder order = haircutOrderService.findHaircutOrderById(orderId);
        switch (flag) {
            case 0:
                //发送前往美发的通知
                order.setStatus(1);
                haircutOrderService.edit(order);//将订单状态设为进行中
                logger.info("id为" + order.getId() + "的订单进行中\n");
                map = messageController.pushComingMessage(orderId, 0);
                logger.info("通知订单号id为" + order.getId() + "的顾客“" + order.user.getName() + "”前往美发");
                break;
            case 1:
                //发送等待前一位的通知
                order.setStatus(0);//将订单状态设为已通知
                haircutOrderService.edit(order);
                map = messageController.pushComingMessage(orderId, 1);
                logger.info("通知订单号id为" + order.getId() + "的顾客“" + order.user.getName() + "”前面只剩1位顾客了");
                break;
            case 2:
                //发送等待前两位的通知
                order.setStatus(0);//将订单状态设为已通知
                haircutOrderService.edit(order);
                map = messageController.pushComingMessage(orderId, 2);
                logger.info("通知订单号id为" + order.getId() + "的顾客“" + order.user.getName() + "”前面只剩2位顾客了");
                break;
            case -1:
                //发送订单已完成，请评价一下的通知

                order.setStatus(2);
                haircutOrderService.edit(order);

                //发型师已完成的总订单+1
                Hairstylist hairstylist = order.getHairstylist();
                hairstylist.regulateOrderSum();//根据自己的订单列表（中的已完成）数量进行校正
                hairstylistService.edit(hairstylist);

                map = pushWxMsg.pushEvaluationMessage(orderId);//发送“评价服务提醒”的订阅消息
                logger.info("id为" + order.getId() + "的订单已完成(用户：“" + order.user.getName() + "”，发型师：“" + hairstylist.getHairstylistName() + "”)\n");
                break;
            default:
                //发送等待前flag位的通知
                order.setStatus(0);//将订单状态设为已通知
                haircutOrderService.edit(order);
                map = messageController.pushComingMessage(orderId, flag);
                logger.info("通知订单号id为" + order.getId() + "的顾客“" + order.user.getName() + "”前面还有" + flag + "位顾客,请耐心等候");
                break;
        }
        return map;
    }


    @ApiOperation(value = "获取自己关于某个顾客的预约记录(按时间倒序排序)-用于“发型师-预约列表-预约记录”页面")
    @GetMapping("/hairstylist/getOrderRecordFromOneUser")
    public Map getOrderRecordFromOneUser(@RequestParam String myOpenid, @RequestParam Integer userId) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null) {
                logger.info("非发型师用户操作！！（获取自己关于某个顾客的预约记录）");
                map.put("error", "对不起，你不是发型师用户，无权操作！！");
                return map;
            } else {
                List<HaircutOrder> tempOrderList = hairstylist.haircutOrderList;
                List<HaircutOrder> resultOrderList = new ArrayList<HaircutOrder>();
                for (HaircutOrder order : tempOrderList) {
                    if (order.user.getId() == userId) {
                        resultOrderList.add(order);
                    }
                }
                //获取某个顾客关于自己的预约列表（按时间倒序排序） 即筛选用于“发型师-预约列表-预约记录”页面的信息
                if (resultOrderList.size() > 0) {
                    List<OrderRecordFromOneUser> recordList = new ArrayList<>();
                    for (HaircutOrder order : resultOrderList) {
                        OrderRecordFromOneUser record = new OrderRecordFromOneUser();

                        record.setOrderId(order.getId());
                        record.setDate(order.getBookTime());
                        record.setHairService(order.getServiceName());
                        record.setPoint(order.getPoint());
                        recordList.add(record);
                    }

                    // 按时间倒序排序
                    Collections.sort(recordList, (r1, r2) -> {
                        if (r1.getDate().after(r2.getDate())) {
                            return -1;
                        } else if (r2.getDate().after(r1.getDate())) {
                            return 1;
                        }
                        return 0; //相等为0
                    });

                    map.put("recordList", recordList);
                    map.put("count", recordList.size());
                } else {
                    map.put("message", "没有匹配的记录");
                }
                return map;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取预约记录列表失败！！（后端发生某些错误）");
            map.put("error", "获取预约记录列表失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


    @ApiOperation(value = "获取自己的运营数据（包括中数据和日报周报月报表格的所有数据）")
    @GetMapping("/hairstylist/getOperationalData")
    public Map getOperationalData(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null) {
                logger.info("非发型师用户操作！！（获取自己的运营数据（包括中数据和日报周报月报表格的所有数据））");
                map.put("error", "对不起，你不是发型师用户，无权操作！！");
                return map;
            } else {
                map.put("completedOrderSum", hairstylist.getOrderSum());//已完成订单总数
                map.put("CustomerSum", hairstylist.getCustomerSum());//顾客总数
                map.put("loyalCustomerSum", hairstylist.getLoyalUserRecordList().size());//忠实（粉丝）顾客数
                map.put("operationalData", hairstylist.getAllOperationalData());//页面中日报周报月报折线图中的数据

                return map;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取运营数据失败！！（后端发生某些错误）");
            map.put("error", "获取运营数据失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "普通用户提交预约订单")
    @PostMapping("/user/addHaircutOrder")
    public Map addHaircutOrder(@RequestParam String myOpenid, @RequestParam String userPhone,
                               @RequestParam Integer hairstylistId, @RequestParam String bookTime, @RequestParam Integer serviceId) {
        Map map = new HashMap();
        try {

            HaircutOrder order = new HaircutOrder();

            User user = userService.findUserByOpenid(myOpenid);
            if (user == null) {
                logger.info("openid为" + myOpenid + "的普通用户不存在！");
                map.put("error", "无效的用户！！");
                return map;
            }
            Hairstylist hairstylist = hairstylistService.findHairstylistById(hairstylistId);
            HairService hairService = hairServiceService.findHairServiceById(serviceId);

            order.setUser(user);//设置提交该订单的用户
            order.setHairstylist(hairstylist);//设置该订单对应的发型师
            order.setServiceName(hairService.getServiceName());//设置选取的服务项目名称
            order.setDescription(hairService.getDescription());//设置选取的服务项目描述
            order.setPrice(hairService.getPrice());//设置选取的服务项目大致价格

            order.setUserPhone(userPhone);//设置联系方式
            order.setStatus(-1);//设置订单状态为"待完成"


            Date date = new Date(System.currentTimeMillis());
            order.setCreateTime(date);

            try {
                //处理用户提交的预约时间
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(bookTime);
                order.setBookTime(date);
                if (date == null) {
                    logger.info("时间转换失败，请检查时间格式（传入数据：" + bookTime + "）");
                    map.put("error", "时间转换失败，请检查预约的时间格式：“yyyy-MM-dd HH:mm:ss”");
                    return map;
                }
                if(date.before(new Date(System.currentTimeMillis()))){
                    logger.info("用户提交了当前时间之前的时间（" + bookTime + "），预约失败");
                    map.put("error", "请选择当前时刻之后的可预约时间！");
                    return map;
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
                logger.info("时间转换失败，请检查时间格式（传入数据：" + bookTime + "）");
                map.put("error", "时间转换失败，请检查预约的时间格式：“yyyy-MM-dd HH:mm:ss”");
                e.printStackTrace();
                return map;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            //...设计订单的预约号(预约的年月日+0+发型师id+0+用户id+0+用户性别标志)
            String reservationNum = calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.MONTH) + "" + calendar.get(Calendar.DAY_OF_MONTH) + "0" + hairstylist.getId() + "0" + user.getId() + "0" + user.getSex();
            if (user.getSex() == null)
                reservationNum = calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.MONTH) + "" + calendar.get(Calendar.DAY_OF_MONTH) + "0" + hairstylist.getId() + "0" + user.getId() + "00" ;
            order.setReservationNum(reservationNum);

            if (haircutOrderService.findByReservationNum(reservationNum) != null) {
                logger.info("该用户当天已有同一发型师的预约，为避免刷单，禁止重复预约！");
                map.put("error", "当天已有同一发型师的预约，不可重复预约！");
                return map;
            }

            haircutOrderService.save(order);
            logger.info("id为" + user.getId() + "的用户“" + user.getName() + "”提交了一个对发型师（id=" + hairstylist.getOpenid() + "）“" + hairstylist.getHairstylistName() + "”的订单");
            messageController.pushSuccessMessage(order.getId());//给用户发送预约成功的模板消息通知
            map.put("message", "订单提交成功！");
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("用户提交预约订单失败！！（后端发生某些错误）");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();
        }
        return map;
    }

    @ApiOperation(value = "普通用户获取自己的预约订单列表(分页展示)")
    @GetMapping("/user/getHaircutOrderList")
    public Map getHaircutOrderList(@RequestParam String myOpenid,
                                   @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                   @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {

            User user = userService.findUserByOpenid(myOpenid);
            if (user == null) {
                logger.info("openid为" + myOpenid + "的普通用户不存在！");
                map.put("error", "无效的用户！！");
                return map;
            }
            List<HaircutOrder> tempOrderList = user.getHaircutOrderList();
            List<UserReservation> resultList = new ArrayList<>();

            if (tempOrderList == null || tempOrderList.size() == 0) {
                map.put("message", "你还没有进行预约过哦~");
                return map;
            }

            // 按创建时间倒序排序
            Collections.sort(tempOrderList, (o1, o2) -> {
                if (o2.getCreateTime().after(o1.getCreateTime())) {
                    return 1;
                } else if (o1.getCreateTime().after(o2.getCreateTime())) {
                    return -1;
                }
                return 0; //相等为0
            });
            // 按预约时间倒序排序
            Collections.sort(tempOrderList, (o1, o2) -> {
                if (o2.getBookTime().after(o1.getBookTime())) {
                    return 1;
                } else if (o1.getBookTime().after(o2.getBookTime())) {
                    return -1;
                }
                return 0; //相等为0
            });

            //获取所求页数的订单列表数据
            int first = pageNum * pageSize;
            int last = pageNum * pageSize + pageSize - 1;
            for (int i = first; i <= last && i < tempOrderList.size(); i++) {
                UserReservation userReservation = new UserReservation(tempOrderList.get(i));
                resultList.add(userReservation);
            }

            //包装分页数据
            Pageable pageable = PageRequest.of(pageNum, pageSize);
            Page<UserReservation> page = new PageImpl<>(resultList, pageable, tempOrderList.size());

            map.put("page", page);
            return map;
        } catch (Exception e) {
            logger.info("获取自己的预约列表失败！！（后端发生某些错误）");
            map.put("error", "获取我的预约列表失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "普通用户获取预约单详情")
    @PostMapping("/user/getHaircutOrder")
    public Map addHaircutOrder(@RequestParam String myOpenid, @RequestParam Integer orderId) {
        Map map = new HashMap();
        try {

            HaircutOrder order = haircutOrderService.findHaircutOrderById(orderId);
            if (order == null) {
                logger.info("id为" + orderId + "的订单不存在！");
                map.put("error", "未找到该订单！！");
                return map;
            }
            User user = userService.findUserByOpenid(myOpenid);
            if (user == null) {
                logger.info("openid为" + myOpenid + "的普通用户不存在！");
                map.put("error", "无效的用户！！");
                return map;
            }
            if (order.getUser().getId() != user.getId()) {
                logger.info("id为" + order.getId() + "的订单不是id为" + user.getId() + "的用户“" + user.getName() + "”的订单，无权查看");
                map.put("error", "该订单不是你的，无权操作！！");
                return map;
            }


            map.put("orderDetail", order);
        } catch (Exception e) {
            logger.info("用户获取预约单详情失败！！（后端发生某些错误）\n\n");
            map.put("error", "获取预约单详情失败！！（后端发生某些错误）");
            e.printStackTrace();
        }
        return map;
    }

    @ApiOperation(value = "普通用户给该订单的发型师评分")
    @PostMapping("/user/order/rate")
    public Map rateThisOrder(@RequestParam String myOpenid, @RequestParam Integer orderId, @RequestParam Double point,
                             @RequestParam(value = "comment", required = false) String comment) {
        Map map = new HashMap();
        try {

            HaircutOrder order = haircutOrderService.findHaircutOrderById(orderId);
            if (order == null) {
                logger.info("id为" + orderId + "的订单不存在！");
                map.put("error", "未找到该订单！！");
                return map;
            }
            User user = userService.findUserByOpenid(myOpenid);
            if (user == null) {
                logger.info("openid为" + myOpenid + "的普通用户不存在！");
                map.put("error", "无效的用户！！");
                return map;
            }
            if (order.getUser().getId() != user.getId()) {
                logger.info("id为" + order.getId() + "的订单不是id为" + user.getId() + "的用户“" + user.getName() + "”的订单，无权评分");
                map.put("error", "该订单不是你的，无权操作！！");
                return map;
            }

            if (order.getStatus() != 2) {
                logger.info("id为" + order.getId() + "的订单还未完成，不允许评分！");
                map.put("error", "订单未完成，不允许评分！！");
                return map;
            }
            if (order.getPoint() != null && order.getPoint() > 0) {
                logger.info("id为" + order.getId() + "的订单已经评过分了，不允许重复评分！");
                map.put("error", "已经评过分了，不允许重复评分！！");
                return map;
            }

            //进行评分
            order.setPoint(point);
            order.setComment(comment);
            haircutOrderService.edit(order);

            Hairstylist hairstylist = order.getHairstylist();
            hairstylist.regulatePoint();//校正发型师的评分
            hairstylistService.edit(hairstylist);

            logger.info("id为" + order.getId() + "的订单中，用户“" + user.getName() + "”给发型师“" + hairstylist.getHairstylistName() + "”  <id为" + hairstylist.getId() + ">评分评了“" + point + "”分");
            map.put("message", "评分成功！");
        } catch (Exception e) {
            logger.info("用户为订单评分失败！！（后端发生某些错误）\n\n");
            map.put("error", "评分失败！！（后端发生某些错误）");
            e.printStackTrace();
        }
        return map;
    }


//    @ApiOperation(value = "删除用户订单",notes = "m1")
//    @DeleteMapping("/haircutOrder/{haircutOrderId}")
//    public void deleteHaircutOrder(@PathVariable("haircutOrderId") Integer haircutOrderId) {
//        haircutOrderService.delete(haircutOrderId);
//    }
//
//    @ApiOperation(value = "修改用户订单",notes = "m1")
//    @PutMapping("/haircutOrder")
//    public void updateHaircutOrder(@Validated HaircutOrder haircutOrders) {
//        haircutOrderService.edit(haircutOrders);
//    }
//
//    @ApiOperation(value = "获取单个订单信息的所有信息，包括其中用户和发型师信息", produces = "application/json")
//    @GetMapping("/haircutOrder")
//    public HaircutOrder getOne(int orderId) {
//        return haircutOrderService.findHaircutOrderById(orderId);
//    }


    @ApiOperation(value = "分页获取所有订单列表", notes = "仅管理员有权限", produces = "application/json")
    @GetMapping("/haircutOrders/getAll")
    public Map getHaircutOrdersPage(@RequestParam String myOpenid,
                                    @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                    @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {
            if (administratorService.isExist(myOpenid)) {
                Page<HaircutOrder> page = haircutOrderService.findAll(pageNum, pageSize);
                map.put("page", page);
                logger.info("获取订单列表成功！");
                return map;
            } else {
                logger.info("获取订单列表失败！！（没有权限！！）");
                map.put("error", "获取订单列表失败！！（没有权限！！）");
                return map;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取订单列表失败！！（后端发生某些错误）");
            map.put("error", "获取订单列表失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


}
