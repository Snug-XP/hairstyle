package com.gaocimi.flashpig.controller;

import com.gaocimi.flashpig.entity.HairService;
import com.gaocimi.flashpig.entity.HaircutOrder;
import com.gaocimi.flashpig.entity.Hairstylist;
import com.gaocimi.flashpig.entity.User;
import com.gaocimi.flashpig.model.HairstylistReservation;
import com.gaocimi.flashpig.model.OrderRecordFromOneUser;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.*;
import com.gaocimi.flashpig.utils.MyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    @Autowired
    UserService userService;
    @Autowired
    HairServiceService hairServiceService;
    @Autowired
    PushTemplateMessageController messageController;

    @ApiOperation(value = "获取一个时间段的预约列表(按预约时间顺序排序，若预约时间一致，按创建时间顺序)-用于“发型师-预约列表”页面", notes = "days的值表示获取几天前到现在的数据，days默认为0，表示只获取今天的订单,days=1表示获取昨天和今天的所有订单,days=-1表示获取今天之后的所有订单")
    @GetMapping("/hairstylist/getOrderList")
    public Map getOrderList(String myOpenid,
                            @RequestParam(defaultValue = "0", required = true) int days) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if ( hairstylist == null || hairstylist.getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else {
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
                        HairstylistReservation record = new HairstylistReservation(order);
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
        for (HairstylistReservation reservation : reservationList) {
            switch (reservation.getStatus()) {
                case -1:
                    //待完成的订单,加入排号订单列表中
                case 0:
                    //已通知用户准备的订单,加入排号订单列表中
                case 1:
                    //当前正在进行中的订单,加入排号订单列表中
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

    @ApiOperation(value = "下一位操作，如果有进行的订单，将进行中的订单状态设为已完成；通知下一位前来美发，并通知再后2位准备前往 - 用于“发型师-排号系统”页面的通知的“下一位”操作")
    @GetMapping("/hairstylist/nextWaitingOrder")
    public Map nextWaitingOrder(String myOpenid) {
        Map map = new HashMap();

        map = getWaitingOrder(myOpenid);  //获取当前的排号列表
        List<HairstylistReservation> reservationList = (List<HairstylistReservation>) map.get("resultList");
        //今天没订单或出错了
        if (reservationList == null||reservationList.size()==0) {
//            logger.info(map.toString());
            return map; //提示信息都在map里面
        }

        /**预约订单状态，“-1”表示待完成，“0”表示已通知用户准备， “1”表示订单正在进行中，“2”表示订单已完成，“-2”表示订单已取消*/

        //获取排队列表的第一个订单
        HairstylistReservation reservation = reservationList.get(0);
        HaircutOrder order = haircutOrderService.findHaircutOrderById(reservation.getOrderId());

        //通知排队列表下一顾客（排队列表中的第一个或第二个顾客）前来美发
        if (order.getStatus() == -1||order.getStatus() == 0) {
            //如果排队列表的第一个订单为“待完成”或“已通知”
            //将该订单状态改为“进行中”，并通知用户前来美发
            reservation.setStatus(1);
            map = notifyUser(order.getId(),0);//通知用户可以前来美发了
            if(map.get("error")!=null) return map;
            
        } else if (order.getStatus() == 1) {
            //如果排队列表的第一个订单正在进行
            //将进行中的订单状态改为“已完成”
            reservation.setStatus(2);
            map = notifyUser(order.getId(),-1);//发送订单已完成，请评价的通知
            if(map.get("error")!=null) return map;

            //如果排队列表有第二个订单,通知该订单顾客前来美发
            if(reservationList.size()>1){

                //获取排队列表的第二个订单信息
                reservation = reservationList.get(1);
                order = haircutOrderService.findHaircutOrderById(reservation.getOrderId());

                if (order.getStatus() == -1||order.getStatus() == 0){
                    //如果该订单为“待完成”或“已通知”
                    //将该订单状态改为“进行中”，并通知用户前来美发
                    reservation.setStatus(1);
                    map = notifyUser(order.getId(),0);//通知用户可以前来美发了
                    if(map.get("error")!=null) return map;
                }else{
                    logger.info("逻辑错误，排队列表的第二个订单状态异常！状态为"+order.getStatus()
                            +"\n<“-1”表示待完成，“0”表示已通知用户准备， “1”表示订单正在进行中，“2”表示订单已完成，“-2”表示订单已取消>\n");
                    map.put("error","逻辑错误，排队列表的第二个订单异常！状态为"+order.getStatus()
                            +"\n<“-1”表示待完成，“0”表示已通知用户准备， “1”表示订单正在进行中，“2”表示订单已完成，“-2”表示订单已取消>");
                    return map;
                }
            }
        }else{
            logger.info("逻辑错误，排队列表的第一个订单状态异常！状态为"+order.getStatus()
                    +"\n<“-1”表示待完成，“0”表示已通知用户准备， “1”表示订单正在进行中，“2”表示订单已完成，“-2”表示订单已取消>\n");
            map.put("error","逻辑错误，排队列表的第一个订单状态异常！状态为"+order.getStatus()
                    +"\n<“-1”表示待完成，“0”表示已通知用户准备， “1”表示订单正在进行中，“2”表示订单已完成，“-2”表示订单已取消>");
            return map;
        }//通知排队列表下一顾客前来美发结束

        //下面通知后两位顾客进行准备
        int count=0;
        for(HairstylistReservation r : reservationList){
            if(r.getStatus()!=1&&r.getStatus()!=2){//找到前两个状态不是进行中或已完成的预约订单进行通知（本排号列表只可能存在已完成、进行中、已通知、待完成的订单，不会出现已取消和状态异常的订单）
                notifyUser(r.getOrderId(),++count);
            }
            if(count==2) break;
        }

        map = getWaitingOrder(myOpenid);
        //重新获取当前的排号列表并返回
        return map;
    }


    /**
     *
     * @param orderId 要通知的订单id
     * @param flag 通知用户前面还有几个人，当flag=-1时，通知订单已完成
     * @return
     */
    public Map notifyUser(int orderId , int flag){
        Map map = new HashMap();

        HaircutOrder order = haircutOrderService.findHaircutOrderById(orderId);
        switch (flag){
            case 0:
                //发送前往美发的通知
                order.setStatus(1);
                haircutOrderService.edit(order);//将订单状态设为进行中
                logger.info("id为"+order.getId()+"的订单进行中\n");
                messageController.pushComingMessage(orderId,0);
                logger.info("通知订单号id为"+order.getId()+"的顾客“"+order.user.getName()+"”前往美发");
                map.put("flag",1);
                break;
            case 1:
                //发送等待前一位的通知
                order.setStatus(0);//将订单状态设为已通知
                haircutOrderService.edit(order);
                messageController.pushComingMessage(orderId,1);
                logger.info("通知订单号id为"+order.getId()+"的顾客“"+order.user.getName()+"”前面只剩1位顾客了");
                break;
            case 2:
                //发送等待前两位的通知
                order.setStatus(0);//将订单状态设为已通知
                haircutOrderService.edit(order);
                messageController.pushComingMessage(orderId,2);
                logger.info("通知订单号id为"+order.getId()+"的顾客“"+order.user.getName()+"”前面只剩2位顾客了");
                break;
            case -1:
                //发送订单已完成，请评价一下的通知
                order.setStatus(2);
                haircutOrderService.edit(order);
                messageController.pushCompleteMessage(orderId);
                logger.info("id为"+order.getId()+"的订单已完成\n");
                break;
            default:
                //错误flag
                logger.info("通知flag错误！！请检查代码！！");
                break;
        }
        return map;
    }


    @ApiOperation(value = "获取自己关于某个顾客的预约记录(按时间倒序排序)-用于“发型师-预约列表-预约记录”页面")
    @GetMapping("/hairstylist/getOrderRecordFromOneUser")
    public Map getOrderRecordFromOneUser(String myOpenid, int userId) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if ( hairstylist == null || hairstylist.getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
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
            logger.error(String.valueOf(e));
            logger.info("获取预约记录列表失败！！（后端发生某些错误，例如数据库连接失败）");
            map.put("error", "获取预约记录列表失败！！（后端发生某些错误，例如数据库连接失败）");
            e.printStackTrace();
            return map;
        }
    }


    @ApiOperation(value = "普通用户提交预约订单",notes = "m1")
    @PostMapping("/user/addHaircutOrder")
    public Map addHaircutOrder( String myOpenid, String userName,String userPhone,
                                int hairstylistId, String bookTime,int serviceId ) {
        Map map = new HashMap();
        try{

            HaircutOrder order = new HaircutOrder();

            User user = userService.findUserByOpenid(myOpenid);
            Hairstylist hairstylist = hairstylistService.findHairstylistById(hairstylistId);
            HairService hairService = hairServiceService.findHairServiceById(serviceId);

            order.setUser(user);//设置提交该订单的用户
            order.setHairstylist(hairstylist);//设置该订单对应的发型师
            order.setServiceName(hairService.getServiceName());//设置选取的服务项目名称
            order.setDescription(hairService.getDescription());//设置选取的服务项目描述
            order.setPrice(hairService.getPrice());//设置选取的服务项目大致价格

            order.setUserName(userName);//设置用户的称呼（不是用户账户中的名字，要求自己再输一遍，可以提示用户输入自己的称呼，不一定输入真名）
            order.setUserPhone(userPhone);//设置联系方式
            order.setStatus(-1);//设置订单状态为"待完成"


            Date date = new Date(System.currentTimeMillis());
            order.setCreateTime(date);

            //...设计订单的预约号
            String reservationNum = "000"+order.user.getId()+date.getYear()+date.getDay()+date.getHours();
            order.setReservationNum(reservationNum);

            try {
                //处理用户提交的预约时间
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(bookTime);
                order.setBookTime(date);
                if(date==null){
                    logger.info("时间转换失败，请检查时间格式（传入数据："+bookTime+"）");
                    map.put("error", "时间转换失败，请检查预约的时间格式");
                    return map;
                }
            }catch (Exception e){
                logger.error(String.valueOf(e));
                logger.info("时间转换失败，请检查时间格式（传入数据："+bookTime+"）");
                map.put("error", "时间转换失败，请检查预约的时间格式");
                e.printStackTrace();
                return map;
            }

            haircutOrderService.save(order);
            logger.info("id为"+user.getId()+"的用户“"+userName+"”提交了一个对发型师（id="+hairstylist.getOpenid()+"）“"+hairstylist.getHairstylistName()+"”的订单");
            map.put("message","订单提交成功！");
        } catch (Exception e) {
            logger.error(String.valueOf(e));
            logger.info("用户提交预约订单失败！！（后端发生某些错误）");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();
        }
        return map;
    }
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

    @ApiOperation(value = "获取单个用户订单信息",notes = "m1",produces = "application/json")
    @GetMapping("/haircutOrder")
    public HaircutOrder getOne( int haircutOrderId) {
        return haircutOrderService.findHaircutOrderById(haircutOrderId);
    }


    @ApiOperation(value = "分页获取所有订单列表", notes = "仅管理员有权限", produces = "application/json")
    @GetMapping("/haircutOrders/getAll")
    public Map getHaircutOrdersPage(String myOpenid,
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
            logger.error(String.valueOf(e));
            logger.info("获取订单列表失败！！（后端发生某些错误，例如数据库连接失败）");
            map.put("error", "获取订单列表失败！！（后端发生某些错误，例如数据库连接失败）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "****************时间传输测试*************************",notes = "m1")
    @PostMapping("/test")
    public Date getHaircutOrdersPage(String time) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
        return date;
    }

}
