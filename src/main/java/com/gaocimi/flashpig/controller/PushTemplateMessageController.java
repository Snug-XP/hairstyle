package com.gaocimi.flashpig.controller;

import com.gaocimi.flashpig.entity.HaircutOrder;
import com.gaocimi.flashpig.entity.UserFormid;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.HaircutOrderService;
import com.gaocimi.flashpig.service.UserFormidService;
import com.gaocimi.flashpig.utils.MyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateData;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import me.chanjar.weixin.common.error.WxErrorException;

/**
 * 参考https://www.jianshu.com/p/35da86f309d4
 *
 * @author xp
 * @date 2019-10-17 14:25:42
 */
@RestController
@ResponseResult
@Api(value = "模版消息的服务", description = "微信小程序获推送模版消息给用户")
public class PushTemplateMessageController {
    protected static final Logger logger = LoggerFactory.getLogger(WXLoginController.class);

    @Autowired
    private WxMaService wxService;
    @Autowired
    private HaircutOrderService haircutOrderService;
    @Autowired
    private UserFormidService userFormidService;
    @Autowired
    private UserFormidController userFormidController;


    @ApiOperation(value = "根据订单id，推送消息测试-预约成功的通知")
    @PostMapping("/pushSuccessMessage")
//    public String pushSuccessMessage(@RequestParam String openid, @RequestParam String formid) {
    public Map pushSuccessMessage(int orderId) {

        Map map = new HashMap();
        List<WxMaTemplateData> templateDataList = getSuccessTemplateDataList(orderId);

        HaircutOrder order = haircutOrderService.findHaircutOrderById(orderId);
        UserFormid userFormid = userFormidController.getOneUserFormid(order.user.getUserFormidList());

        if (userFormid == null) {
            logger.info("该用户没有合适的Formid用于发送模版消息，记得想办法获取用户的Formid哦");
            map.put("message", "该用户没有合适的Formid用于发送模版消息，记得想办法获取用户的Formid哦");
            return map;
        }
        //设置推送消息
        WxMaTemplateMessage templateMessage = WxMaTemplateMessage.builder()
                .toUser(userFormid.getOpenid())//要推送的用户openid
                .formId(userFormid.getFormid())//收集到的formid
                .templateId("RcF6gCn0tPrr1UKT0PPs5IFCuAG4Ww7Pz0I6ab17JNI")//推送的模版id（在小程序后台设置）
                .data(templateDataList)//模版信息
                .page("pages/index/index")//要跳转到小程序那个页面
                .build();
        //4，发起推送
        try {
            wxService.getMsgService().sendTemplateMsg(templateMessage);
        } catch (WxErrorException e) {
            logger.info("推送失败：" + e.getMessage());
            map.put("error", "推送失败，模版消息的设置错误（例如小程序id不对应）");
            return map;
        }
        logger.info("推送消息发送成功：", templateDataList);
        return map;
    }


    /**
     * 使用订单id获取成功消息模版数据列表
     *
     * @param orderId 订单id
     * @return 成功消息模版数据列表
     */
    public List<WxMaTemplateData> getSuccessTemplateDataList(int orderId) {

        //这边必须从数据库获取一遍，不能直接传一个HaircutOrder实体类，因为懒加载原因可能不包含发型师的数据
        HaircutOrder order = haircutOrderService.findHaircutOrderById(orderId);

        List<WxMaTemplateData> templateDataList = new ArrayList<>(2);

        //设置模版信息（keyword1：类型，keyword2：内容）
        WxMaTemplateData reservationNum = new WxMaTemplateData("keyword1", order.getReservationNum());
        WxMaTemplateData creatTime = new WxMaTemplateData("keyword2", order.getCreateTime().toString());
        WxMaTemplateData shop = new WxMaTemplateData("keyword7", order.hairstylist.getShopName());
        WxMaTemplateData bookTime = new WxMaTemplateData("keyword3", order.getBookTime().toString());
        WxMaTemplateData address = new WxMaTemplateData("keyword4", order.getAddress());
        WxMaTemplateData hairstylistName = new WxMaTemplateData("keyword5", order.hairstylist.getHairstylistName());
        WxMaTemplateData hairstylistPhone = new WxMaTemplateData("keyword6", order.hairstylist.getPersonalPhone());
        WxMaTemplateData serviceName = new WxMaTemplateData("keyword8", order.getServiceName());
        templateDataList.add(reservationNum);
        templateDataList.add(creatTime);
        templateDataList.add(bookTime);
        templateDataList.add(shop);
        templateDataList.add(address);
        templateDataList.add(hairstylistName);
        templateDataList.add(hairstylistPhone);
        templateDataList.add(serviceName);

        return templateDataList;
    }

    @ApiOperation(value = "测试Formid列表排序")
    @GetMapping("/Formidtest")
    public Map test(int orderId) {

        Map map = new HashMap();
        List<UserFormid> formidList = haircutOrderService.findHaircutOrderById(orderId).user.getUserFormidList();

        if (formidList == null) {
            System.out.println("为空");
            map.put("formidList", formidList);
            return map;
        }
        if (formidList.size() == 0) {
            System.out.println("为0");
            map.put("formidList", formidList);
            return map;
        }
        // ...将Formid列表按提交的时间顺序排序
        Collections.sort(formidList, (r1, r2) -> {
            if (r1.getCreatTime().after(r2.getCreatTime())) {
                return 1;
            } else if (r2.getCreatTime().after(r1.getCreatTime())) {
                return -1;
            }
            return 0; //相等为0
        });

        map.put("formidList", formidList);
        return map;
    }
}