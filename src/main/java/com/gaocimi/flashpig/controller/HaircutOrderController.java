package com.gaocimi.flashpig.controller;

import com.gaocimi.flashpig.entity.HaircutOrder;
import com.gaocimi.flashpig.entity.Hairstylist;
import com.gaocimi.flashpig.model.HairstylistReservation;
import com.gaocimi.flashpig.model.OrderRecordFromOneUser;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.AdministratorService;
import com.gaocimi.flashpig.service.HaircutOrderService;
import com.gaocimi.flashpig.service.HairstylistService;
import com.gaocimi.flashpig.utils.MyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author liyutg
 * @date 2019/6/12 2:15
 * @description
 */
@RestController
@ResponseResult
@Api(value = "用户订单操作服务", description = "操作用户订单相关业务")
public class HaircutOrderController {
    protected static final Logger logger = LoggerFactory.getLogger(HairstylistController.class);

    @Autowired
    HaircutOrderService haircutOrderService;
    @Autowired
    AdministratorService administratorService;
    @Autowired
    HairstylistService hairstylistService;

    @ApiOperation(value = "获取一个时间段的预约列表(按预约时间顺序排序，若预约时间一致，按创建时间顺序)-用于“发型师-预约列表”页面", notes = "days的值表示获取几天前到现在的数据，days默认为0，表示只获取今天的订单,days=1表示获取昨天和今天的所有订单,days=-1表示获取今天之后的所有订单")
    @GetMapping("/hairstylist/getOrderList")
    public Map getOrderList(String myOpenid,
                            @RequestParam(defaultValue = "0", required = true) int days) {
        Map map = new HashMap();
        try {
            if (hairstylistService.findHairstylistByOpenid(myOpenid) == null || hairstylistService.findHairstylistByOpenid(myOpenid).getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else {
                Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
                List<HaircutOrder> tempOrderList = hairstylist.haircutOrderList;
                List<HaircutOrder> resultOrderList = new ArrayList<HaircutOrder>();
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
                        HairstylistReservation record = new HairstylistReservation();

                        record.setOrderId(order.getId());
                        record.setUserPhone(order.getUserPhone());
                        record.setUserId(order.user.getId());
                        record.setImgUrl(order.user.getPictureUrl());
                        record.setUserName(order.user.getName());
                        record.setCreatTime(order.getCreateTime());
                        record.setBookTime(order.getBookTime());
                        record.setHairService(order.getHairService().getServiceName());
                        record.setStatus(order.getStatus());

                        recordList.add(record);
                    }

                    // 先按创建时间顺序排序一遍
                    Collections.sort(recordList, (r1, r2) -> {
                        if (r1.getCreatTime().after(r2.getCreatTime())) {
                            return 1;
                        } else if (r2.getCreatTime().after(r1.getCreatTime())) {
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

                    map.put("recordList", recordList);
                    map.put("todayOrderCount", recordList.size());
                } else
                    map.put("message", "你今天没有订单");

                return map;
            }
        } catch (Exception e) {
            logger.error(String.valueOf(e));
            logger.info("获取预约列表失败！！（后端发生某些错误，例如数据库连接失败）");
            map.put("error", "获取预约列表失败！！（后端发生某些错误，例如数据库连接失败）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "获取当前的排号列表(按预约时间顺序排序，若预约时间一致，按创建时间顺序) - 用于“发型师-排号系统”页面")
    @GetMapping("/hairstylist/getWaitingOrder")
    public Map getWaitingOrder(String myOpenid) {
        Map map = new HashMap();

        //获取今天的所有订单(按时间顺序排序)
        map = getOrderList(myOpenid, 0);
        List<HairstylistReservation> reservationList = (List<HairstylistReservation>) map.get("recordList");

        //今天没订单或出错了
        if (reservationList == null) {
//            logger.info(map.toString());
            return map;//信息都在map里面
        }

        map = doSth(reservationList);//对今天的订单,进行订单区分统计
        return map;
    }

    /**对今天的订单,进行订单区分统计*/
    public Map doSth(List<HairstylistReservation> reservationList) {
        Map map = new HashMap();

        int todayOrderNum = reservationList.size(); //今天有效的的订单数
        int completedOrderNum = 0;//已完成订单数
        int canceledOrderNum = 0;//已被取消的订单数
        int remainingOrderNum = 0;//未完成的有效订单数

        List<HairstylistReservation> resultList = new ArrayList<>();

        for (HairstylistReservation reservation : reservationList) {
            switch (reservation.getStatus()) {
                case 0:
                    resultList.add(reservation); //将要进行的订单
                    break;
                case 1:
                    completedOrderNum++; //已完成订单数+1
                    break;
                case -1:
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
        map.put("resultList", resultList);//未完成的有效订单信息列表

        return map;
    }


    @ApiOperation(value = "获取自己关于某个顾客的预约记录(按时间顺序排序)-用于“发型师-预约列表-预约记录”页面")
    @GetMapping("/hairstylist/getOrderRecordFromOneUser")
    public Map getOrderRecordFromOneUser(String myOpenid, int userId) {
        Map map = new HashMap();
        try {
            if (hairstylistService.findHairstylistByOpenid(myOpenid) == null || hairstylistService.findHairstylistByOpenid(myOpenid).getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else {
                Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
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
                        record.setHairService(order.getHairService().getServiceName());
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
            logger.error(String.valueOf(e));
            logger.info("获取预约记录列表失败！！（后端发生某些错误，例如数据库连接失败）");
            map.put("error", "获取预约记录列表失败！！（后端发生某些错误，例如数据库连接失败）");
            e.printStackTrace();
            return map;
        }
    }


    //    @ApiOperation(value = "添加用户订单",notes = "m1")
//    @PostMapping("/haircutOrder")
//    @ResponseStatus(HttpStatus.CREATED)
//    public Map addHaircutOrder(@Validated HaircutOrder haircutOrders) {
//        Map map = new HashMap();
//        try{
//            haircutOrderService.save(haircutOrders);
//            logger.info("添加用户订单成功！");
//            map.put("message","添加用户订单成功！");
//        } catch (Exception e) {
//            logger.error(String.valueOf(e));
//            e.printStackTrace();
//            map.put("error", "操作失败！！（后端发生某些错误，例如数据库连接失败）");
//        }
//        return map;
//    }
//
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
//
//    @ApiOperation(value = "获取单个用户订单信息",notes = "m1",produces = "application/json")
//    @GetMapping("/haircutOrder/{haircutOrderId}")
//    public HaircutOrder getOne( @PathVariable("haircutOrderId") Integer haircutOrderId) {
//        return haircutOrderService.findHaircutOrderById(haircutOrderId);
//    }
    @ApiOperation(value = "分页获取所有订单列表", notes = "仅管理员有权限", produces = "application/json")
    @GetMapping("/haircutOrders/all")
    public Map getHaircutOrdersPage(String myOpenid,
                                    @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                    @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
    ) {
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
            logger.error(String.valueOf(e));
            logger.info("获取订单列表失败！！（后端发生某些错误，例如数据库连接失败）");
            map.put("error", "获取订单列表失败！！（后端发生某些错误，例如数据库连接失败）");
            e.printStackTrace();
            return map;
        }
    }

}
