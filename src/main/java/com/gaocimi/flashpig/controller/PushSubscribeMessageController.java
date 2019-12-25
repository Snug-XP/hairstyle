package com.gaocimi.flashpig.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeData;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import com.gaocimi.flashpig.entity.HaircutOrder;
import com.gaocimi.flashpig.entity.UserFormid;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.HaircutOrderService;
import com.gaocimi.flashpig.service.UserFormidService;
import com.gaocimi.flashpig.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.error.WxErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * （根据binarywang大佬的github更新的微信小程序订阅消息接口照着模版消息改的）
 *
 * @author xp
 * @date 2019-12-25 21:52:01
 */
@RestController
@ResponseResult
@Api(value = "订阅消息的服务", description = "微信小程序获推送订阅消息给用户")
public class PushSubscribeMessageController {
    protected static final Logger logger = LoggerFactory.getLogger(WXLoginController.class);

    @Autowired
    private WxMaService wxService;
    @Autowired
    private HaircutOrderService haircutOrderService;
    @Autowired
    UserService userService;


//    @ApiOperation(value = "根据订单id，推送模板消息-预约成功的通知（...之后记得关闭url访问）")
//    @PostMapping("/hairstylist/pushSuccessMessage")
//    public Map pushSuccessMessage(@RequestParam Integer orderId) {
//
//        Map map = new HashMap();
//
//        //不同模板要换的地方，还有模版id哦
//        List<WxMaSubscribeData> subscribeDataList = getSuccessSubscribeDataList(orderId);
//        if(subscribeDataList==null){
//            logger.info("模板设置错误");
//            map.put("error","模板设置错误");
//            return map;
//        }
//
//        HaircutOrder order = haircutOrderService.findHaircutOrderById(orderId);
//        //设置推送消息
//        WxMaSubscribeMessage subscribeMessage = WxMaSubscribeMessage.builder()
//                .toUser(order.user.getOpenid())//要推送的用户openid
//                .templateId("d-QbHDRGS6l_AEDbpbbMs0KTV1gSozPnRvUh_CMNdyU")//推送的模版id（在小程序后台设置）
//                .data(subscribeDataList)//模版信息
//                .page("pages/index/index")//要跳转到小程序那个页面
//                .build();
//        //4，发起推送
//        try {
//            wxService.getMsgService().sendSubscribeMsg(subscribeMessage);
//        } catch (WxErrorException e) {
//            logger.info("推送失败：" + e.getMessage());
//            map.put("error", "推送失败，订阅消息的设置错误");
//            return map;
//        }
//        logger.info("推送消息发送成功：", subscribeDataList);
//        map.put("message","推送预约成功消息成功");
//        return map;
//    }
//
//
//    @ApiOperation(value = "根据订单id，推送模板消息-准备前往（...之后记得关闭url访问）")
//    @PostMapping("/hairstylist/pushComingMessage")
//    public Map pushComingMessage(@RequestParam Integer orderId , @RequestParam Integer remainingNum ) {
//
//        Map map = new HashMap();
//
//        //不同模板要换的地方，还有模版id哦
//        List<WxMaSubscribeData> subscribeDataList = getComingSubscribeDataList(orderId,remainingNum);
//        if(subscribeDataList==null){
//            logger.info("模板设置错误");
//            map.put("error","模板设置错误");
//            return map;
//        }
//
//        HaircutOrder order = haircutOrderService.findHaircutOrderById(orderId);
//
//        //设置推送消息
//        WxMaSubscribeMessage subscribeMessage = WxMaSubscribeMessage.builder()
//                .toUser(order.user.getOpenid())//要推送的用户openid
//                .templateId("ZFP8KiBS6jDc4Ft5yTl6fz46zPG4fyuIOARhIz0aYFw")//推送的模版id（在小程序后台设置）
//                .data(subscribeDataList)//模版信息
//                .page("pages/index/index")//要跳转到小程序那个页面
//                .build();
//        //4，发起推送
//        try {
//            wxService.getMsgService().sendSubscribeMsg(subscribeMessage);
//        } catch (WxErrorException e) {
//            logger.info("推送失败：" + e.getMessage());
//            map.put("error", "推送失败，订阅消息的设置错误");
//            return map;
//        }
//        logger.info("推送消息发送成功：", subscribeDataList);
//        map.put("message","推送准备前往消息成功");
//        return map;
//    }
//
//
//    @ApiOperation(value = "根据订单id，推送模板消息-订单已完成（...之后记得关闭url访问）")
//    @PostMapping("/hairstylist/pushCompletedMessage")
//    public Map pushCompletedMessage(@RequestParam Integer orderId) {
//        Map map = new HashMap();
//
//        //不同模板要换的地方，还有模版id哦
//        List<WxMaSubscribeData> subscribeDataList = getCompetedSubscribeDataList(orderId);
//
//        if(subscribeDataList==null){
//            logger.info("模板设置错误");
//            map.put("error","模板设置错误");
//            return map;
//        }
//        HaircutOrder order = haircutOrderService.findHaircutOrderById(orderId);
//
//        //设置推送消息
//        WxMaSubscribeMessage subscribeMessage = WxMaSubscribeMessage.builder()
//                .toUser(order.user.getOpenid())//要推送的用户openid
//                .templateId("2Ib132bA4R-Y31pfL3Tb5qMgTW5WkEJ50_c1UyskSDM")//推送的模版id（在小程序后台设置）
//                .data(subscribeDataList)//模版信息
//                .page("pages/index/index")//要跳转到小程序那个页面
//                .build();
//        //4，发起推送
//        try {
//            wxService.getMsgService().sendSubscribeMsg(subscribeMessage);
//        } catch (WxErrorException e) {
//            logger.info("推送失败：" + e.getMessage());
//            map.put("error", "推送失败，订阅消息的设置错误");
//            return map;
//        }
//        logger.info("推送消息发送成功：", subscribeDataList);
//        map.put("message","推送订单已完成消息成功");
//        return map;
//    }
//
//
//    /**
//     * 使用订单id获取"通知前往"消息模版数据列表
//     *
//     * @param remainingNum 剩余等待人数
//     * @param orderId 订单id
//     * @return "通知前往"消息模版数据列表
//     */
//    private List<WxMaSubscribeData> getComingSubscribeDataList(@RequestParam Integer orderId, @RequestParam Integer remainingNum) {
//
//        String tipsStr=null ;
//        switch (remainingNum){
//            case 0:
//                tipsStr = "发型师提醒你前往接收服务！";break;
//            case 1:
//                tipsStr = "亲，前面还有1位顾客哦，你可以前往了";break;
//            case 2:
//                tipsStr = "亲，前面还有2位顾客哦，你可以准备前往了";break;
//            default:
//                tipsStr = "亲，前面还有"+remainingNum+"位顾客哦，请耐心等候";break;
//
//        }
//
//        //这边必须从数据库获取一遍，不能直接传一个HaircutOrder实体类，因为懒加载原因可能不包含发型师的数据
//        HaircutOrder order = haircutOrderService.findHaircutOrderById(orderId);
//
//        List<WxMaSubscribeData> subscribeDataList = new ArrayList<>();
//
//        //设置模版信息（keyword1：类型，keyword2：内容）
//        WxMaSubscribeData reservationNum = new WxMaSubscribeData("keyword1", order.getReservationNum());
//        WxMaSubscribeData shop = new WxMaSubscribeData("keyword2", order.hairstylist.shop.getShopName());
//        WxMaSubscribeData address = new WxMaSubscribeData("keyword3", order.getAddress());
//        WxMaSubscribeData bookTime = new WxMaSubscribeData("keyword4", order.getBookTime().toString());
//        WxMaSubscribeData hairstylistPhone = new WxMaSubscribeData("keyword5", order.hairstylist.getPersonalPhone());
//        WxMaSubscribeData serviceName = new WxMaSubscribeData("keyword6", order.getServiceName());
//        WxMaSubscribeData tips = new WxMaSubscribeData("keyword7", tipsStr);
//        subscribeDataList.add(reservationNum);
//        subscribeDataList.add(shop);
//        subscribeDataList.add(address);
//        subscribeDataList.add(bookTime);
//        subscribeDataList.add(serviceName);
//        subscribeDataList.add(hairstylistPhone);
//        subscribeDataList.add(tips);
//
//        return subscribeDataList;
//    }


    /**
     * 使用订单id获取"预约成功"消息模版数据列表
     *
     * @param orderId 订单id
     * @return "预约成功"消息模版数据列表
     */
    public List<WxMaSubscribeData> getSuccessSubscribeDataList(@RequestParam Integer orderId) {

        //这边必须从数据库获取一遍，不能直接传一个HaircutOrder实体类，因为懒加载原因可能不包含发型师的数据
        HaircutOrder order = haircutOrderService.findHaircutOrderById(orderId);

        List<WxMaSubscribeData> subscribeDataList = new ArrayList<>();

        //设置模版信息（keyword1：类型，keyword2：内容）
        WxMaSubscribeData reservationNum = new WxMaSubscribeData("keyword1", order.getReservationNum());
        WxMaSubscribeData creatTime = new WxMaSubscribeData("keyword2", order.getCreateTime().toString());
        WxMaSubscribeData shop = new WxMaSubscribeData("keyword3", order.hairstylist.shop.getShopName());
        WxMaSubscribeData bookTime = new WxMaSubscribeData("keyword4", order.getBookTime().toString());
        WxMaSubscribeData address = new WxMaSubscribeData("keyword5", order.getAddress());
        WxMaSubscribeData hairstylistName = new WxMaSubscribeData("keyword6", order.hairstylist.getHairstylistName());
        WxMaSubscribeData hairstylistPhone = new WxMaSubscribeData("keyword7", order.hairstylist.getPersonalPhone());
        WxMaSubscribeData serviceName = new WxMaSubscribeData("keyword8", order.getServiceName());
        subscribeDataList.add(reservationNum);
        subscribeDataList.add(creatTime);
        subscribeDataList.add(bookTime);
        subscribeDataList.add(shop);
        subscribeDataList.add(address);
        subscribeDataList.add(hairstylistName);
        subscribeDataList.add(hairstylistPhone);
        subscribeDataList.add(serviceName);

        return subscribeDataList;
    }


    /**
     * 使用订单id获取"订单完成"消息模版数据列表
     *
     * @param orderId 订单id
     * @return "订单完成"消息模版数据列表
     */
    public List<WxMaSubscribeData> getCompetedSubscribeDataList(@RequestParam Integer orderId) {

        //这边必须从数据库获取一遍，不能直接传一个HaircutOrder实体类，因为懒加载原因可能不包含发型师的数据
        HaircutOrder order = haircutOrderService.findHaircutOrderById(orderId);

        String statusString=null;
        /**预约订单状态，“-1”表示待完成，“0”表示已通知用户准备， “1”表示订单正在进行中，“2”表示订单已完成，“-2”表示订单已取消*/
        switch ( order.getStatus() ){
            case -2:
                statusString = "已取消";
                break;
            case -1:
                statusString = "待完成";
                break;
            case 0:
                statusString = "已通知";
                break;
            case 1:
                statusString = "进行中";
                break;
            case 2:
                statusString = "已完成";
                break;
            default:
                statusString = "状态异常";
        }
        List<WxMaSubscribeData> subscribeDataList = new ArrayList<>();

        //设置模版信息（keyword1：类型，keyword2：内容）
        WxMaSubscribeData reservationNum = new WxMaSubscribeData("keyword1", order.getReservationNum());
        WxMaSubscribeData tips = new WxMaSubscribeData("keyword2", "您的订单已完成，可以前往小程序对本次服务进行评价哦；或者您对本次订单有疑问的话，请联系下方相关人员电话");
        WxMaSubscribeData serviceName = new WxMaSubscribeData("keyword3", order.getServiceName());
        WxMaSubscribeData shop = new WxMaSubscribeData("keyword4", order.hairstylist.shop.getShopName());
        WxMaSubscribeData address = new WxMaSubscribeData("keyword5", order.getAddress());
        WxMaSubscribeData hairstylistPhone = new WxMaSubscribeData("keyword6", order.hairstylist.getPersonalPhone());
        WxMaSubscribeData orderStatus = new WxMaSubscribeData("keyword7", statusString );
        subscribeDataList.add(reservationNum);
        subscribeDataList.add(tips);
        subscribeDataList.add(serviceName);
        subscribeDataList.add(shop);
        subscribeDataList.add(address);
        subscribeDataList.add(hairstylistPhone);
        subscribeDataList.add(orderStatus);

        return subscribeDataList;
    }

//------------------------------下面的已成功实现---------------------------------------------------------------------
    @ApiOperation(value = "测试推送订阅消息")
    @PostMapping("/hairstylist/pushTestMessage")
    public Map pushTestMessage(@RequestParam String openid) {

        Map map = new HashMap();

        //不同模板要换的地方，还有模版id哦
        List<WxMaSubscribeData> subscribeDataList = getTestSubscribeDataList();

        //设置推送消息
        WxMaSubscribeMessage subscribeMessage = WxMaSubscribeMessage.builder()
                .toUser(openid)//要推送的用户openid
                .templateId("d-QbHDRGS6l_AEDbpbbMs0KTV1gSozPnRvUh_CMNdyU")//推送的模版id（在小程序后台设置）
                .data(subscribeDataList)//模版信息
                .page("pages/index/index")//要跳转到小程序那个页面
                .build();
        //4，发起推送
        try {
            wxService.getMsgService().sendSubscribeMsg(subscribeMessage);
        } catch (WxErrorException e) {
            logger.info("推送失败：" + e.getMessage());
            map.put("error", "推送失败，模版消息的设置错误（例如小程序id不对应）");
            return map;
        }
        logger.info("推送消息发送成功：", subscribeDataList);
        map.put("message","推送预约成功消息成功");
        return map;
    }

    private List<WxMaSubscribeData> getTestSubscribeDataList() {
        List<WxMaSubscribeData> subscribeDataList = new ArrayList<>();

//        Data date= new Date(System.currentTimeMillis());

        //设置模版信息（keyword1：类型，keyword2：内容）
        WxMaSubscribeData creatTime = new WxMaSubscribeData("date2", "2019年10月1日 15:01");
        WxMaSubscribeData address = new WxMaSubscribeData("thing4","地址");
        WxMaSubscribeData shop = new WxMaSubscribeData("thing6", "商店名");
        WxMaSubscribeData hairstylistPhone = new WxMaSubscribeData("phone_number9", "13123191995");
        WxMaSubscribeData service = new WxMaSubscribeData("thing1", "服务名称");
        subscribeDataList.add(creatTime);
        subscribeDataList.add(address);
        subscribeDataList.add(shop);
        subscribeDataList.add(hairstylistPhone);
        subscribeDataList.add(service);

        return subscribeDataList;
    }
}