//package com.gaocimi.flashpig.controller;
//
//import com.alibaba.fastjson.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.util.Map;
//
//@RestController
//@RequestMapping(value = "/payment/")
//public class WxPaymentController2 {
//
//	private static Logger logger = LoggerFactory.getLogger(WxPaymentController2.class);
//	@Value("${hcc.wx.domain}")
//	private String orderDomain;
//
//	@Autowired
//	private PaymentService paymentService;
//
//     /**
//	 * <p>统一下单入口</p>
//	 *
//	 * @param request
//	 * @param response
//	 * @throws Exception
//	 */
//	@ResponseBody
//    @RequestMapping(value="toPay", method= RequestMethod.POST,
//produces ={"application/json;charset=UTF-8"})
//	public JSONObject toPay(HttpServletRequest request) throws Exception {
//		String requestStr = RequestStr.getRequestStr(request);
//		if (StringUtils.isEmpty(requestStr)) {
//			throw new ParamException();
//		}
//		JSONObject jsonObj = JSONObject.parseObject(requestStr);
//		if(StringUtils.isEmpty(jsonObj.getString("orderNo")) || StringUtils.isEmpty(jsonObj.getString("openId"))){
//		   	throw new ParamException();
//		}
//	   	OrderInfo orderInfo = .....//此处写获取订单信息方法
//	   	if(orderInfo == null){
//	   		return AjaxUtil.renderFailMsg("订单不存在！");
//	   	}else if(orderInfo.getPayAmount() == null || orderInfo.getPayAmount() <= 0){
//	   		return AjaxUtil.renderFailMsg("订单有误，请确认！");
//	   	}else if(orderInfo.getOrderStatus() != 1){//1待付款
//	   		String msg = orderInfo.getOrderStatus() >1 ?"此订单已支付！":"订单未提交，请确认！";
//	   		return AjaxUtil.renderFailMsg(msg);
//	   	}else{
//	   		logger.info("【小程序支付服务】请求订单编号:["+orderInfo.getOrderNo()+"]");
//		   	Map<String, String> resMap = paymentService.xcxPayment(+orderInfo.getOrderNo(),orderInfo.getPayAmount(),jsonObj.getString("openId"));
//		   	if("SUCCESS".equals(resMap.get("returnCode")) && "OK".equals(resMap.get("returnMsg"))){
//		   		//统一下单成功
//		   		resMap.remove("returnCode");
//		   		resMap.remove("returnMsg");
//		   		logger.info("【小程序支付服务】支付下单成功！");
//		   		return AjaxUtil.renderSuccessMsg(resMap);
//		   	}else{
//		   		logger.info("【小程序支付服务】支付下单失败！原因:"+resMap.get("returnMsg"));
//		   		return AjaxUtil.renderFailMsg(resMap.get("returnMsg"));
//		   }
//	   	}
//
//	}
//    /**
//	 * <p>回调Api</p>
//	 *
//	 * @param request
//	 * @param response
//	 * @throws Exception
//	 */
//    @RequestMapping(value="xcxNotify")
//	public void xcxNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
// 		InputStream inputStream =  request.getInputStream();
// 		//获取请求输入流
//		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        byte[] buffer = new byte[1024];
//        int len = 0;
//        while ((len=inputStream.read(buffer))!=-1){
//            outputStream.write(buffer,0,len);
//        }
//        outputStream.close();
//        inputStream.close();
//        Map<String,Object> map = BeanToMap.getMapFromXML(new String(outputStream.toByteArray(),"utf-8"));
//        logger.info("【小程序支付回调】 回调数据： \n"+map);
//        String resXml = "";
//        String returnCode = (String) map.get("return_code");
//        if ("SUCCESS".equalsIgnoreCase(returnCode)) {
//            String returnmsg = (String) map.get("result_code");
//            if("SUCCESS".equals(returnmsg)){
//            	//更新数据
//            	int result = paymentService.xcxNotify(map);
//            	if(result > 0){
//	            	//支付成功
//	                resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
//	                        + "<return_msg><![CDATA[OK]]></return_msg>"+"</xml>";
//            	}
//            }else{
//                resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
//                        + "<return_msg><![CDATA[报文为空]></return_msg>" + "</xml>";
//                logger.info("支付失败:"+resXml);
//            }
//        }else{
//            resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
//                    + "<return_msg><![CDATA[报文为空]></return_msg>" + "</xml>";
//            logger.info("【订单支付失败】");
//        }
//
//        logger.info("【小程序支付回调响应】 响应内容：\n"+resXml);
//        response.getWriter().print(resXml);
//	}
//}
