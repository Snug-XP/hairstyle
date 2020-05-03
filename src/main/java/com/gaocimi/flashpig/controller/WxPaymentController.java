package com.gaocimi.flashpig.controller;

import com.gaocimi.flashpig.entity.ProductOrder;
import com.gaocimi.flashpig.entity.User;
import com.gaocimi.flashpig.entity.WxPayOrder;
import com.gaocimi.flashpig.service.ProductOrderService;
import com.gaocimi.flashpig.service.UserService;
import com.gaocimi.flashpig.service.WxPayOrderService;
import com.gaocimi.flashpig.utils.xp.IpUtil;
import com.gaocimi.flashpig.utils.xp.MyUtils;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.BaseWxPayResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.bytebuddy.implementation.bytecode.Throw;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xp
 * @Date 2020-2-22 20:24:11
 * @decription 微信用户进行支付操作的相关处理
 */
@RestController
@Api(value = "微信支付相关操作", description = "微信用户进行支付操作的相关处理")
public class WxPaymentController {

    private static Logger logger = LoggerFactory.getLogger(WxPaymentController.class);


    @Autowired
    WxPayService wxPayService;
    @Autowired
    WxPayOrderService wxPayOrderService;
    @Autowired
    ProductOrderService productOrderService;
    @Autowired
    UserService userService;

    /**
     * 发起与支付
     * 调用统一下单接口，获取“预支付交易会话标识”
     */
    @ApiOperation(value = "调用统一下单接口，获取“预支付交易会话标识”")
    @PostMapping("/creatPayOrder")
    public Map creatPayOrder(HttpServletRequest request,
                             @RequestParam String myOpenid,
                             @RequestParam Integer money,
                             @RequestParam Integer type,
                             @RequestParam String body,
                             @RequestParam(value = "productOrderId", required = false) Integer productOrderId) throws WxPayException {
        Map map = new HashMap();

        User user = userService.findUserByOpenid(myOpenid);
        if (user == null) {
            logger.info("openid为" + myOpenid + "的普通用户不存在！(调用统一下单接口)");
            map.put("error", "无效的用户！！");
            return map;
        }

        ProductOrder productOrder = null;
        if (productOrderId != null) {

            if (type != -1) {
                logger.info("调用商品订单的支付操作（type应该为-1），但是选择的支付类型错误：type=" + type);
                map.put("error", "订单类型错误！（商品订单的支付订单类型应为-1）");
                return map;
            }

            productOrder = productOrderService.findById(productOrderId);
            if (productOrder == null) {
                logger.info("id为" + productOrderId + "的商品订单不存在！（调用统一下单接口）");
                map.put("error", "无效的商品订单！");
                return map;
            }
//            productOrder.setWxPayOrder(w);
        }


        WxPayOrder payOrder = new WxPayOrder();

        payOrder.setUser(user);
        payOrder.setProductOrder(productOrder);
        payOrder.setMoney(money);//单位：分
        payOrder.setType(type);
        payOrder.setBody(body);


        wxPayOrderService.save(payOrder);


        try {
            WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();   //商户订单类
            orderRequest.setBody(body);
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
            logger.error("【微信支付】生成支付订单失败(订单号={}) 原因:“{}”", payOrder.getId(), e.getMessage());
            map.put("error", "支付失败," + e.getMessage());
            e.printStackTrace();
            wxPayOrderService.delete(payOrder.getId());
            return map;
        }
    }


    @SuppressWarnings("deprecation")
    @ApiOperation("微信支付回调地址")
    @PostMapping("/notify") // 返回订单号
    public String payNotify(HttpServletRequest request, HttpServletResponse response) {
        String resXml = "";
        logger.info("======================>>>微信支付回调<<======================");
        logger.info("======================>>>===========<<======================");
        try {

            String xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
            WxPayOrderNotifyResult notifyResult = wxPayService.parseOrderNotifyResult(xmlResult);


            if ("SUCCESS".equals(notifyResult.getResultCode())) {
                // 结果正确 outTradeNo
                Integer orderId = Integer.parseInt(notifyResult.getOutTradeNo());
                String tradeNo = notifyResult.getTransactionId();
                String totalFee = BaseWxPayResult.fenToYuan(notifyResult.getTotalFee());
                WxPayOrder payOrder = wxPayOrderService.findWxPayOrderById(orderId);
                if (payOrder != null) {
                    if (payOrder.getStatus() == 1) {
                        logger.info("重复回调的支付订单！");
                        return WxPayNotifyResponse.success("重复回调的支付订单！");
                    }

                    //支付订单有效，下面进行相关处理操作

                    try {
                        User user = payOrder.getUser();
                        switch (payOrder.getType()) {
                            case 0:
                                //0表示普通用户购买会员(90天)的订单
                                if (!user.buyVip(90)) {
                                    logger.info("购买会员失败！！");
                                    logger.info("》》》支付订单有效，但进行相关处理操作时（type=" + payOrder.getType() + "）发生异常,请管理员尽快查看《《《");
                                } else {
                                    userService.edit(user);
                                }
                                break;
                            case 1:
                                //1表示普通用户购买会员(180天)的订单
                                if (!user.buyVip(180)) {
                                    logger.info("购买会员失败！！");
                                    logger.info("》》》支付订单有效，但进行相关处理操作时（type=" + payOrder.getType() + "）发生异常,请管理员尽快查看《《《");
                                } else {
                                    userService.edit(user);
                                }
                                break;
                            case 2:
                                //2表示普通用户购买会员(365天)的订单
                                if (!user.buyVip(365)) {
                                    logger.info("购买会员失败！！");
                                    logger.info("》》》支付订单有效，但进行相关处理操作时（type=" + payOrder.getType() + "）发生异常,请管理员尽快查看《《《");
                                } else {
                                    userService.edit(user);
                                }
                                break;
                            case -1:
                                //-1表示商品购物订单
                                ProductOrder productOrder = payOrder.getProductOrder();
                                if(payOrder==null){
                                    logger.info("》》》未查找到对应商品订单，请管理员尽快查看！！！《《《）");
                                    throw new Exception("未查找到对应商品订单");
                                }
                                productOrder.setStatus(1);
                                productOrder.setWxPayOrder(payOrder);

                                productOrderService.edit(productOrder);
                                break;

                            default:
                                logger.info("》》》支付订单类型错误！！（支付已完成，但是无法判断是何种支付服务，请管理员尽快查看！！！《《《）");
                        }
                    } catch (Exception e) {
                        //...微信支付相关操作信息出现错误，比较敏感，这边可以设置邮箱提醒之类的
                        logger.info(e.getMessage());
                        e.printStackTrace();
                        logger.info("》》》支付订单有效，但进行相关处理操作时发生异常,请管理员尽快查看《《《");

                    }


                    payOrder.setStatus(1);//订单已完成
                    payOrder.setEndTime(new Date());
                    wxPayOrderService.edit(payOrder);

                }


                logger.info("微信订单号状态==>{}", "支付成功！");
                logger.info("微信订单号   ==>{}", tradeNo);
                logger.info("数据库订单号 ==>{}", orderId);
                logger.info("商品名称     ==>{}", payOrder.getBody());
                logger.info("付款总金额   ==>{}元", totalFee);
                logger.info("付款人       ==>{}(id={})", payOrder.getUser().getName(), payOrder.getUser().getId());
                logger.info("付款人电话   ==>{}", payOrder.getUser().getPhoneNum());
                logger.info("==========================================================\n\n");

//另一种直接返回数据
//                resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
//                        + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
//                response.setContentType("text/xml");
//                BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
//                out.write(resXml.getBytes());
//                out.flush();
//                out.close();
//                return resXml;

                //使用已写好的类返回
                return WxPayNotifyResponse.success("成功");

            } else {
                Integer orderId = Integer.parseInt(notifyResult.getOutTradeNo());
                String tradeNo = notifyResult.getTransactionId();

                logger.warn("微信订单号状态==>支付失败！！！！！！！！！！！！！");
                logger.warn("返回信息=======>{}", notifyResult.getReturnMsg());
                logger.info("微信订单号   ==>{}", tradeNo);
                logger.info("数据库订单号 ==>{}", orderId);
                logger.info("==========================================================\n\n");

                //另一种直接返回数据
//                resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
//                        + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
//                response.setContentType("text/xml");
//                BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
//                out.write(resXml.getBytes());
//                out.flush();
//                out.close();
//                return resXml;

                //使用已写好的类返回
                return WxPayNotifyResponse.success("支付失败");
            }

            // 自己处理订单的业务逻辑，需要判断订单是否已经支付过，否则可能会重复调用
            // 通知微信.异步确认成功.必写.不然会一直通知后台.十次之后就认为交易失败了.

        } catch (Exception e) {
//            logger.error("微信回调结果异常,异常原因{}", e.getMessage());
            // WxPayNotifyResponse.fail(e.getMessage());

            logger.error(WxPayNotifyResponse.success("code:" + 9999 + "微信回调结果异常,异常原因:" + e.getMessage()));

            return WxPayNotifyResponse.success("code:" + 9999 + "微信回调结果异常,异常原因:" + e.getMessage());
        }
    }


//    @SuppressWarnings("deprecation")
//    @ApiOperation("微信支付回调地址测试")
//    @PostMapping("/notifyTest") // 返回订单号
//    public T payNotifyTest() {
//
//    }


}
