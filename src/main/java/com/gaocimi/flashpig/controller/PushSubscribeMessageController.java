package com.gaocimi.flashpig.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeData;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import com.gaocimi.flashpig.entity.HaircutOrder;
import com.gaocimi.flashpig.entity.Hairstylist;
import com.gaocimi.flashpig.entity.Shop;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.HaircutOrderService;
import com.gaocimi.flashpig.service.HairstylistService;
import com.gaocimi.flashpig.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.error.WxErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * （根据binarywang大佬的github更新的微信小程序订阅消息接口照着订阅消息改的）
 *
 * @author xp
 * @date 2019-12-25 21:52:01
 */
@RestController
@ResponseResult
@Api(value = "订阅消息的服务", description = "微信小程序获推送订阅消息给用户")
public class PushSubscribeMessageController {
    protected static final Logger logger = LoggerFactory.getLogger(WxLoginController.class);

    @Autowired
    private WxMaService wxService;
    @Autowired
    private HaircutOrderService haircutOrderService;
    @Autowired
    private HairstylistService hairstylistService;
    @Autowired
    private ShopService shopService;


    @ApiOperation(value = "推送订阅消息")
    @PostMapping("/hairstylist/pushComingMessage")
    public Map pushComingMessage(@RequestParam Integer orderId) {

        Map map = new HashMap();
        try {
            HaircutOrder order = haircutOrderService.findHaircutOrderById(orderId);
            if (order == null) {
                logger.info("不存在的订单！！（输入的订单id=" + orderId + "）");
                map.put("error", "不存在的订单！！");
                return map;
            }
            //不同模板要换的地方，还有模版id哦
            List<WxMaSubscribeData> subscribeDataList = getComingSubscribeDataList(orderId);

            //设置推送消息
            WxMaSubscribeMessage subscribeMessage = WxMaSubscribeMessage.builder()
                    .toUser(order.user.getOpenid())//要推送的用户openid
                    .templateId("CQgQJU8GbYEaP3h6ImCde5YXsuTubF88mdaz89sUy2M")//推送的模版id（在小程序后台设置）
                    .data(subscribeDataList)//订阅消息
                    .page("pages/homepage/homepage")//要跳转到小程序那个页面
                    .build();
            //4，发起推送
            try {
                wxService.getMsgService().sendSubscribeMsg(subscribeMessage);
            } catch (WxErrorException e) {
                logger.info("推送失败：" + e.getMessage());
                map.put("error", "推送失败：" + e.getMessage());
                return map;
            }
            logger.info("推送给用户"+order.user.getName()+"(id="+order.user.getId()+")的预约提醒消息发送成功："+ subscribeDataList);
            map.put("message", "推送预约提醒消息成功");
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("后台发生异常！！(推送预约提醒消息)");
            map.put("error", "后台发生异常！！");
            return map;
        }

    }

    /**
     * 使用订单id获取"预约提醒"订阅v消息数据列表
     *
     * @param orderId 订单id
     * @return "预约提醒"订阅消息数据列表
     */
    private List<WxMaSubscribeData> getComingSubscribeDataList(Integer orderId) {

        HaircutOrder order = haircutOrderService.findHaircutOrderById(orderId);
        DateFormat df2 = DateFormat.getDateTimeInstance();
        String bookTimeString = df2.format(order.getBookTime());

//        logger.info("获取到的预约时间："+bookTimeString);
        List<WxMaSubscribeData> subscribeDataList = new ArrayList<>();
        //设置订阅消息;
        WxMaSubscribeData shop = new WxMaSubscribeData("thing8", order.getHairstylist().getShop().getShopName());
        WxMaSubscribeData hairstylist = new WxMaSubscribeData("name5", order.getHairstylist().getHairstylistName());
        WxMaSubscribeData bookTime = new WxMaSubscribeData("time2", bookTimeString);
        WxMaSubscribeData service = new WxMaSubscribeData("thing7", order.getServiceName());
        WxMaSubscribeData tips = new WxMaSubscribeData("thing9", "亲，距离您的预约时间还有60分钟,请准备");
        subscribeDataList.add(shop);
        subscribeDataList.add(hairstylist);
        subscribeDataList.add(bookTime);
        subscribeDataList.add(service);
        subscribeDataList.add(tips);

        return subscribeDataList;
    }

//----------------------------------------------------------------------------------------------------------

    @ApiOperation(value = "推送“服务评价提醒”订阅消息")
    @PostMapping("/hairstylist/pushEvaluationMessage")
    public Map pushEvaluationMessage(@RequestParam Integer orderId) {

        Map map = new HashMap();
        try {
            HaircutOrder order = haircutOrderService.findHaircutOrderById(orderId);
            if (order == null) {
                logger.info("不存在的订单！！（输入的订单id=" + orderId + "）");
                map.put("error", "不存在的订单！！");
                return map;
            }
            //不同模板要换的地方，还有模版id哦
            List<WxMaSubscribeData> subscribeDataList = getEvaluationSubscribeDataList(orderId);

            //设置推送消息
            WxMaSubscribeMessage subscribeMessage = WxMaSubscribeMessage.builder()
                    .toUser(order.user.getOpenid())//要推送的用户openid
                    .templateId("NJQk-0J20gRSjIiklUocZMCaFmkC336gs8sZHrV_Kh4")//推送的模版id（在小程序后台设置）
                    .data(subscribeDataList)//订阅消息
                    .page("pages/orderDetail/orderDetail")//要跳转到小程序那个页面
                    .build();
            //4，发起推送
            try {
                wxService.getMsgService().sendSubscribeMsg(subscribeMessage);
            } catch (WxErrorException e) {
                logger.info("推送失败：" + e.getMessage());
                map.put("error", "推送失败：" + e.getMessage());
                return map;
            }
            logger.info("推送给用户"+order.user.getName()+"(id="+order.user.getId()+")服务评价提醒消息发送成功："+ subscribeDataList);
            map.put("message", "推送服务评价提醒消息成功");
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("后台发生异常！！(推送服务评价提醒消息)");
            map.put("error", "后台发生异常！！");
            return map;
        }

    }

    /**
     * 使用订单id获取"服务评价提醒"订阅消息数据列表
     *
     * @param orderId 订单id
     * @return "服务评价提醒"订阅消息数据列表
     */
    private List<WxMaSubscribeData> getEvaluationSubscribeDataList(Integer orderId) {

        HaircutOrder order = haircutOrderService.findHaircutOrderById(orderId);
        DateFormat df2 = DateFormat.getDateTimeInstance();
        String bookTimeString = df2.format(order.getBookTime());

//        logger.info("获取到的预约时间："+bookTimeString);
        List<WxMaSubscribeData> subscribeDataList = new ArrayList<>();
        //设置订阅消息;
        WxMaSubscribeData shop = new WxMaSubscribeData("thing4", order.getHairstylist().getShop().getShopName());
        WxMaSubscribeData hairstylistName = new WxMaSubscribeData("thing2", order.getHairstylist().getHairstylistName());
        WxMaSubscribeData service = new WxMaSubscribeData("thing3", order.getServiceName());
        WxMaSubscribeData tips = new WxMaSubscribeData("thing1", "您对本次的服务体验如何呢？");
        subscribeDataList.add(shop);
        subscribeDataList.add(hairstylistName);
        subscribeDataList.add(service);
        subscribeDataList.add(tips);

        return subscribeDataList;
    }


    //----------------------------------------------------------------------------------------------------------

    @ApiOperation(value = "推送该发型师的“审核结果”订阅消息")
    @PostMapping("/hairstylist/pushApplyResultMessage")
    public Map pushApplyResultMessage(@RequestParam Integer hairstylistId) {

        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistById(hairstylistId);
            if (hairstylist == null) {
                logger.info("不存在的发型师！！（输入的发型师id=" + hairstylistId + "）");
                map.put("error", "不存在的发型师！！");
                return map;
            }
            if (hairstylist.getShop() == null) {
                logger.info("该发型师" + hairstylist.getHairstylistName() + "(id=" + hairstylistId + ")没有提交过入驻门店的申请，无法发送订阅消息！");
                map.put("error","该发型师没有提交过入驻门店的申请，无法发送订阅消息！！");
                return map;
            }
            if (hairstylist.getApplyTime() == null) {
                logger.info("该发型师" + hairstylist.getHairstylistName() + "(id=" + hairstylistId + ")发送审核结果通知时发现没有提交申请的时间，无法发送订阅消息");
                map.put("error","缺失该发型师提交入驻申请的时间！无法发送订阅消息");
                return map;
            }

            //不同模板要换的地方，还有模版id哦
            List<WxMaSubscribeData> subscribeDataList = getApplyResultSubscribeDataList(hairstylistId);

            //设置推送消息
            WxMaSubscribeMessage subscribeMessage = WxMaSubscribeMessage.builder()
                    .toUser(hairstylist.getOpenid())//要推送的用户openid
                    .templateId("AGRhyY6S2Vrb8Lg1lLDjh7MU0JD-46Mvase6bmXGBIQ")//推送的模版id（在小程序后台设置）
                    .data(subscribeDataList)//订阅消息
                    .page("pages/userdata/userdata")//要跳转到小程序那个页面
                    .build();
            //4，发起推送
            try {
                wxService.getMsgService().sendSubscribeMsg(subscribeMessage);
            } catch (WxErrorException e) {
                logger.info("推送失败：" + e.getMessage());
                map.put("error", "推送失败：" + e.getMessage());
                return map;
            }
            logger.info("推送该发型师" + hairstylist.getHairstylistName() + "(id=" + hairstylistId + ")的审核结果消息发送成功："+ subscribeDataList);
            map.put("message", "推送审核结果提醒消息成功");
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("后台发生异常！！(推送发型师审核结果提醒消息)");
            map.put("error", "后台发生异常！！");
            return map;
        }

    }

    /**
     * 使推送该发型师的“审核结果”订阅消息数据列表
     *
     * @param hairstylistId 发型师id
     * @return "审核结果"订阅消息模版数据列表
     */
    private List<WxMaSubscribeData> getApplyResultSubscribeDataList(Integer hairstylistId) {

        Hairstylist hairstylist = hairstylistService.findHairstylistById(hairstylistId);


        String applyTimeString;
        String resultString;
        String resultDescriptionString;

        DateFormat df2 = DateFormat.getDateTimeInstance();
        applyTimeString = df2.format(hairstylist.getApplyTime());

        switch (hairstylist.getApplyStatus()) {
            case -1:
                resultString = "通过";
                break;
            case 1:
                resultString = "未通过";
                break;
            case 0:
                resultString = "审核中..";
                break;
            default:
                resultString = "状态异常";
        }

        if (hairstylist.getApplyResultDescription() == null || hairstylist.getApplyResultDescription().length() == 0)
            resultDescriptionString = "无";
        else
            resultDescriptionString = hairstylist.getApplyResultDescription();


//        logger.info("获取到的预约时间："+bookTimeString);
        List<WxMaSubscribeData> subscribeDataList = new ArrayList<>();
        //设置订阅消息;
        WxMaSubscribeData hairstylistName = new WxMaSubscribeData("name10", hairstylist.getHairstylistName());
        WxMaSubscribeData phone = new WxMaSubscribeData("phone_number2", hairstylist.getPersonalPhone());
        WxMaSubscribeData applyTime = new WxMaSubscribeData("date6", applyTimeString);
        WxMaSubscribeData result = new WxMaSubscribeData("phrase5", resultString);
        WxMaSubscribeData resultDescription = new WxMaSubscribeData("thing8", resultDescriptionString);
        subscribeDataList.add(hairstylistName);
        subscribeDataList.add(phone);
        subscribeDataList.add(applyTime);
        subscribeDataList.add(result);
        subscribeDataList.add(resultDescription);

        return subscribeDataList;
    }



    //----------------------------------------------------------------------------------------------------------

    @ApiOperation(value = "推送给门店的“审核结果”订阅消息")
    @PostMapping("/shop/pushApplyResultMessage")
    public Map pushApplyResultMessage2(@RequestParam Integer shopId) {

        Map map = new HashMap();
        try {
            Shop shop = shopService.findShopById(shopId);
            if (shop == null) {
                logger.info("不存在的门店！！（输入的门店id=" + shopId + "）");
                map.put("error", "不存在的门店！！");
                return map;
            }

            if (shop.getOperatingLicensePictureUrl() == null) {
                logger.info("该门店" + shop.getShopName() + "(id=" + shopId + ")没有提交过经营认定的申请，无法发送订阅消息！");
                map.put("error","该门店没有提交过经营认定的申请，无法发送订阅消息！！");
                return map;
            }
            if (shop.getApplyTime() == null) {
                logger.info("该门店" + shop.getShopName() + "(id=" + shopId + ")发送审核结果通知时发现没有提交申请的时间，无法发送订阅消息");
                map.put("error","缺失该门店提交经营认定申请的时间！无法发送订阅消息");
                return map;
            }

            //不同模板要换的地方，还有模版id哦
            List<WxMaSubscribeData> subscribeDataList = getApplyResultSubscribeDataList2(shopId);

            //设置推送消息
            WxMaSubscribeMessage subscribeMessage = WxMaSubscribeMessage.builder()
                    .toUser(shop.getOpenid())//要推送的用户openid
                    .templateId("AGRhyY6S2Vrb8Lg1lLDjh7MU0JD-46Mvase6bmXGBIQ")//推送的模版id（在小程序后台设置）
                    .data(subscribeDataList)//订阅消息
                    .page("pages/userdata/userdata")//要跳转到小程序那个页面
                    .build();
            //4，发起推送
            try {
                wxService.getMsgService().sendSubscribeMsg(subscribeMessage);
            } catch (WxErrorException e) {
                logger.info("推送失败：" + e.getMessage());
                map.put("error", "推送失败：" + e.getMessage());
                return map;
            }
            logger.info("推送该门店" + shop.getShopName() + "(id=" + shopId + ")的审核结果消息发送成功："+subscribeDataList);
            map.put("message", "推送审核结果提醒消息成功");
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("后台发生异常！！(推送门店审核结果提醒消息)");
            map.put("error", "后台发生异常！！");
            return map;
        }

    }

    /**
     * 推送给门店的“审核结果”订阅消息数据列表
     *
     * @param shopId 门店id
     * @return "审核结果"订阅消息模版数据列表
     */
    private List<WxMaSubscribeData> getApplyResultSubscribeDataList2(Integer shopId) {

        Shop shop = shopService.findShopById(shopId);


        String applyTimeString;
        String resultString;
        String resultDescriptionString;

        DateFormat df2 = DateFormat.getDateTimeInstance();
        applyTimeString = df2.format(shop.getApplyTime());

        switch (shop.getApplyStatus()) {
            case -1:
                resultString = "通过";
                break;
            case 1:
                resultString = "未通过";
                break;
            case 0:
                resultString = "审核中..";
                break;
            default:
                resultString = "状态异常";
        }

        if (shop.getApplyResultDescription() == null || shop.getApplyResultDescription().length() == 0)
            resultDescriptionString = "无";
        else
            resultDescriptionString = shop.getApplyResultDescription();


//        logger.info("获取到的预约时间："+bookTimeString);
        List<WxMaSubscribeData> subscribeDataList = new ArrayList<>();
        //设置订阅消息;
        WxMaSubscribeData shopName = new WxMaSubscribeData("name10", shop.getShopName());
        WxMaSubscribeData phone = new WxMaSubscribeData("phone_number2", shop.getPhone());
        WxMaSubscribeData applyTime = new WxMaSubscribeData("date6", applyTimeString);
        WxMaSubscribeData result = new WxMaSubscribeData("phrase5", resultString);
        WxMaSubscribeData resultDescription = new WxMaSubscribeData("thing8", resultDescriptionString);
        subscribeDataList.add(shopName);
        subscribeDataList.add(phone);
        subscribeDataList.add(applyTime);
        subscribeDataList.add(result);
        subscribeDataList.add(resultDescription);

        return subscribeDataList;
    }
}