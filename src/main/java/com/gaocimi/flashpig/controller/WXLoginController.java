package com.gaocimi.flashpig.controller;

import com.gaocimi.flashpig.entity.User;
import com.gaocimi.flashpig.model.WXSessionModel;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.UserService;
import com.gaocimi.flashpig.utils.HttpClientUtil;
import com.gaocimi.flashpig.utils.JsonUtils;
import com.gaocimi.flashpig.utils.LogUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiangpeng
 * @Date 2019-9-15 19:59
 * @decription 微信小程序获取用户微信号信息进行登录
 */
@RestController
@ResponseResult
@Api(value = "微信用户登录服务", description = "微信小程序获取用户微信号信息进行登录")
public class WXLoginController {

    protected static final Logger logger = LoggerFactory.getLogger(WXLoginController.class);

    @Autowired
    UserService userService;


    @ApiOperation(value = "使用用户登录的临时凭证请求微信服务器换取得到对应的openid与session_key传回给用户")
    @PostMapping("/wxLogin")
    public WXSessionModel wxLogin(String code) {
        Logger log = LogUtils.getExceptionLogger();
        WXSessionModel wxModel = null;
        try {

            logger.info("wxlogin临时凭证  -  code:  " + code);
            String url = "https://api.weixin.qq.com/sns/jscode2session";

            Map<String, String> param = new HashMap<>();
            param.put("appid", "wx3b612db5165b11b6");//(小程序ID)
            param.put("secret", "3f31f17374c5406f24cdf5657085da92");//(小程序密钥)
            param.put("js_code", code);//用户登录的临时凭证
            param.put("grant_type", "authorization_code");

            String wxResult = HttpClientUtil.doGet(url, param);
            logger.info("wxResult的数据为"+wxResult);

            wxModel = JsonUtils. jsonToPojo(wxResult, WXSessionModel.class);
            User user = userService.findUserByOpenid(wxModel.getOpenid());
            if (user == null) {
                logger.info("该用户未登录过本平台！");

                user = new User();
                user.setOpenid(wxModel.getOpenid());
                userService.save(user);
            } else{
                logger.info("用户 " + user.getName() + " 登录成功！！");
            }
        }catch (Exception e){
            log.info(e.getMessage());
        }



        return wxModel;
    }

//    @ApiOperation(value = "用户登录成功后输入用户数据")
//    @PostMapping("/setUserInfo")
//    public String setUserInfo(@RequestParameter Map<String,Object> param) {
//        JSONObject jsonParam = this.getJSONParam(request);
//            logger.info(json);
//        return "helllooooooooooo！";
//    }


}





