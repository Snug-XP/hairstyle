package com.gaocimi.flashpig.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.gaocimi.flashpig.entity.User;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.HairstylistService;
import com.gaocimi.flashpig.service.UserService;
import com.gaocimi.flashpig.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.error.WxErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xp
 * @Date 2019-9-15 19:59
 * @decription 微信小程序获取用户微信号信息进行登录
 */
@RestController
@ResponseResult
@Api(value = "微信用户登录服务", description = "微信小程序获取用户微信号信息进行登录")
public class WxLoginController {

    protected static final Logger logger = LoggerFactory.getLogger(WxLoginController.class);

    @Autowired
    UserService userService;
    @Autowired
    HairstylistService hairstylistService;

    @Autowired
    private WxMaService wxService;

    @ApiOperation(value = "使用用户登录的临时凭证请求微信服务器换取得到对应的openid与session_key传回给用户,并且导入（或修改）用户信息到数据库")
    @PostMapping("/wxLogin")
    public Map wxLogin(String code,
                       @RequestParam(value = "rawData", required = false) String rawData,
                       @RequestParam(value = "name", required = false) String name,
                       @RequestParam(value = "pictureUrl", required = false) String pictureUrl,
                       @RequestParam(value = "phoneNum", required = false) String phoneNum) throws WxErrorException {
        Map map = new HashMap();

        if (code == null) {
            logger.info("临时登录凭证获取失败\n\n\n\n");
            map.put("error", "临时登录凭证获取失败");
            return map;
        }
        Map<String, String> rawDataMap = new HashMap<>();
        if (rawData != null) {
            rawDataMap = JsonUtils.jsonToPojo(rawData, rawDataMap.getClass());
//            logger.info("wxlogin临时凭证  -  code:  " + code + "");
//            logger.info("获取的rawDataMap数据为： " + rawDataMap.toString());
        }

        if (name == null) {
            name = rawDataMap.get("nickName");
        }
        if (pictureUrl == null) {
            pictureUrl = rawDataMap.get("avatarUrl");
        }

//        WXSessionModel wxModel;
//        try {

//            String url = "https://api.weixin.qq.com/sns/jscode2session";
//
//            Map<String, String> param = new HashMap<>();
////            xp的小程序
//            param.put("appid", "wx3b612db5165b11b6");//(小程序ID)
//            param.put("secret", "3f31f17374c5406f24cdf5657085da92");//(小程序密钥)
////            param.put("appid", "wx0eebc4a396708dee");//(小程序ID)
////            param.put("secret", "ff3774682602c7c21040c09ce1bf9ea1");//(小程序密钥)
//            param.put("js_code", code);//用户登录的临时凭证
//            param.put("grant_type", "authorization_code");
//
//            String wxResult = HttpClientUtil.doGet(url, param);
//            logger.info("wxResult的数据为"+wxResult);
//            wxModel = JsonUtils. jsonToPojo(wxResult, WXSessionModel.class);


        WxMaJscode2SessionResult session = wxService.getUserService().getSessionInfo(code);
        map.put("openid", session.getOpenid());
        map.put("sessionKey", session.getSessionKey());

//        logger.info("登录用户openid为  " + session.getOpenid());
//        logger.info("   sessionKey为  " + session.getSessionKey() + "\n");

        User user = userService.findUserByOpenid(session.getOpenid());
        if (user == null) {
            logger.info("该用户未登录过本平台！进行自动注册\n\n");

            user = new User();
            user.setOpenid(session.getOpenid());
            user.setSessionKey(session.getSessionKey());
            user.setName(name);
            user.setPhoneNum(phoneNum);
            user.setPictureUrl(pictureUrl);
            user.setIsVip(0);//设置为非vip
            user.setLastLoginTime(new Date());
            userService.save(user);
            logger.info("<新用户> " + user.getName() + " （" + user.getOpenid() + "）登录成功！\n\n\n\n");

        } else {
            //以前登录过，更新信息
            if (name != null)
                user.setName(name);

            if (phoneNum != null)
                user.setPhoneNum(phoneNum);

            if (pictureUrl != null)
                user.setPictureUrl(pictureUrl);

            user.setSessionKey(session.getSessionKey());
            user.setLastLoginTime(new Date());
            userService.edit(user);
            logger.info("<老用户>  " + user.getName() + " （" + user.getOpenid() + "）登录成功！\n\n\n\n");
        }
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//            logger.info("临时登录凭证错误-不对应\n\n\n\n");
//            e.printStackTrace();
//            map.put("error", "临时登录凭证错误");
//        }
        return map;
    }


    @ApiOperation(value = "获取微信用户绑定的手机号码")
    @PostMapping("/getPhoneNumber")
    public Map getPhoneNumber(@RequestParam String myOpenid,
                              @RequestParam(value = "sessionKey", required = false) String sessionKey,
                              @RequestParam(value = "encryptedData") String encryptedData,
                              @RequestParam(value = "iv") String iv) {
        Map map = new HashMap();

        User user = userService.findUserByOpenid(myOpenid);
        if (user == null) {
            logger.info("openid为" + myOpenid + "的普通用户不存在！");
            map.put("error", "无效的用户！！");
            return map;
        }
        if (sessionKey == null)
            sessionKey = user.getSessionKey();

        // 解密用户电话信息
        WxMaPhoneNumberInfo userInfo = this.wxService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);
//        System.out.println("获取到用户电话号码： "+userInfo+"\n");
        try {
            user.setPhoneNum(userInfo.getPhoneNumber());
            userService.edit(user);
        }catch (Exception e){
            logger.error("往数据库存储用户电话号码时发生错误！错误信息："+e.getCause());
        }

        map.put("userInfo", userInfo);
        return map;
    }

    @ApiOperation(value = "解密用户普通信息数据")
    @PostMapping("/decryptingData")
    public Map decryptingData(@RequestParam String myOpenid,
                              @RequestParam(value = "sessionKey", required = false) String sessionKey,
                              @RequestParam(value = "encryptedData") String encryptedData,
                              @RequestParam(value = "iv") String iv) {
        Map map = new HashMap();

        User user = userService.findUserByOpenid(myOpenid);
        if (user == null) {
            logger.info("openid为" + myOpenid + "的普通用户不存在！");
            map.put("error", "无效的用户！！");
            return map;
        }
        if (sessionKey == null)
            sessionKey = user.getSessionKey();

        // 解密用户信息
        try {
            WxMaUserInfo userInfo = this.wxService.getUserService().getUserInfo(sessionKey, encryptedData, iv);
            System.out.println("获取到用户信息： " + userInfo + "\n");
            map.put("userInfo", userInfo);
        } catch (Exception e) {
            logger.info(e.getMessage());
            map.put("error", e.getMessage());
        }
        return map;
    }

}





