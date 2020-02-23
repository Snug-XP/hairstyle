package com.gaocimi.flashpig.controller;

import com.gaocimi.flashpig.entity.User;
import com.gaocimi.flashpig.entity.WxPayOrder;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.UserService;
import com.gaocimi.flashpig.service.WxPayOrderService;
import com.gaocimi.flashpig.utils.xp.IpUtil;
import com.gaocimi.flashpig.utils.xp.MyUtils;
import com.github.binarywang.wxpay.bean.order.WxPayAppOrderResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;  //统一下单所需参数类
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xp
 * @Date 2020-2-22 20:24:11
 * @decription 微信用户进行支付操作的相关处理
 */
@RestController
@ResponseResult
@Api(value = "微信支付相关操作", description = "微信用户进行支付操作的相关处理")
public class WxPaymentController {

    private static Logger logger = LoggerFactory.getLogger(WxPaymentController.class);


    @Autowired
    WxPayService wxPayService;
    @Autowired
    WxPayOrderService wxPayOrderService;
    @Autowired
    UserService userService;

    /**
     * 发起与支付
     * 调用统一下单接口，获取“预支付交易会话标识”
     */
    @ApiOperation(value = "调用统一下单接口，获取“预支付交易会话标识”")
    @PostMapping("/creatPayOrder")
    public Map creatPayOrder(HttpServletRequest request, @RequestParam String myOpenid, @RequestParam Integer money) throws WxPayException {
        Map map = new HashMap();

        User user = userService.findUserByOpenid(myOpenid);
        if (user == null) {
            logger.info("openid为" + myOpenid + "的普通用户不存在！");
            map.put("error", "无效的用户！！");
            return map;
        }
        WxPayOrder payOrder = new WxPayOrder();
        payOrder.setUser(user);
        payOrder.setMoney(money);//单位：分
        payOrder.setStatus(0);
        payOrder.setCreateTime(new Date());
        payOrder.setNote("购买会员");
        wxPayOrderService.save(payOrder);


        try {
            WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();   //商户订单类
            orderRequest.setBody("购买会员(支付测试)");
            orderRequest.setOpenid(myOpenid);
            //设置金额
            orderRequest.setTotalFee(payOrder.getMoney());   //注意：传入的金额参数单位为分
            //outTradeNo  订单号
            orderRequest.setOutTradeNo(payOrder.getId().toString());
            //tradeType 支付方式
            orderRequest.setTradeType("JSAPI");
            //用户IP地址
            orderRequest.setSpbillCreateIp(IpUtil.getIpAddress(request));
            orderRequest.setNotifyUrl("https://xp.1998pic.cn:8080/notify");


            WxPayMpOrderResult orderResult = wxPayService.createOrder(orderRequest);//createOrder()会调用统一下单接口，获取“预支付交易会话标识，并生成二次签名，将结果返回前端
            map.put("orderResult", orderResult);

            //将生成的预订单id存入数据库
            String str[] = orderResult.getPackageValue().split("=");
            payOrder.setPrepayId(str[1]);
            wxPayOrderService.save(payOrder);

//            logger.info(map.toString());
            return map;
        } catch (Exception e) {
            logger.error("【微信支付】支付失败(订单号={}) 原因:“{}”", payOrder.getId(), e.getMessage());
            map.put("error", "支付失败," + e.getMessage());
            e.printStackTrace();
            wxPayOrderService.delete(payOrder.getId());
            return map;
        }
    }


    @ApiOperation(value = "创建支付预订单")
    @PostMapping("/cerate")
    public Map create(@RequestParam String myOpenid, @RequestParam Integer orderId) {

        Map map = new HashMap();

        User user = userService.findUserByOpenid(myOpenid);
        if (user == null) {
            logger.info("openid为" + myOpenid + "的普通用户不存在！（付款）");
            map.put("error", "无效的用户！！");
            return map;
        }
        WxPayOrder payOrder = wxPayOrderService.findWxPayOrderById(orderId);
        if (payOrder == null) {
            logger.info("oederId为" + orderId + "的订单不存在！（付款）");
            map.put("error", "无效的订单！！");
            return map;
        }
        if (payOrder.user.getId() != user.getId()) {
            logger.info("用户与订单不匹配！！（付款）");
            map.put("error", "用户与订单不匹配！！");
            return map;
        }

        try {
            WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();   //商户订单类
            orderRequest.setBody("微信支付");
            orderRequest.setOpenid(myOpenid);
            //设置金额
            orderRequest.setTotalFee(payOrder.getMoney());   //注意：传入的金额参数单位为分
            //outTradeNo  订单号
            orderRequest.setOutTradeNo(payOrder.getId().toString());
            //tradeType 支付方式
            orderRequest.setTradeType("JSAPI");
            //用户IP地址
            orderRequest.setSpbillCreateIp("165465464646");
            return wxPayService.createOrder(orderRequest);
        } catch (Exception e) {
            logger.error("【微信支付】支付失败 订单号={} 原因={}", payOrder.getId(), e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    @ApiOperation(value = "支付结果收集处理")
    @PostMapping("/notify")
    public boolean notify(@RequestParam Map<String, Object> map) {
        logger.info("\n\n支付结果:\n" + map + "\n\n");
        return true;
    }


}
