//package com.gaocimi.flashpig.controller;
//
//
//import cn.binarywang.wx.miniapp.api.WxMaService;
//import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
//import com.gaocimi.flashpig.configuration.shiro.MockToken;
//import com.gaocimi.flashpig.entity.User;
//import com.gaocimi.flashpig.exception.BusinessException;
//import com.gaocimi.flashpig.result.ResponseResult;
//import com.gaocimi.flashpig.result.ResultCode;
//import com.gaocimi.flashpig.service.IUserService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import me.chanjar.weixin.common.error.WxErrorException;
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.session.Session;
//import org.apache.shiro.subject.Subject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
///**
// * @author liyutg
// * @Date 2018/9/27 16:38
// * @description
// */
//@RestController
//@ResponseResult
//@Api(value = "管理端登录服务",description = "")
//public class LoginController {
//
//    @Autowired
//    private WxMaService wxService;
//
//    @Autowired
//    IUserService userService;
//
//
//    @ApiOperation(value = "登录",notes = "m1")
//    @GetMapping("/login")
//    public User login(String code, String state) throws WxErrorException {
//        if(code==null||"".equals(code)){
//            throw new BusinessException(ResultCode.USER_NOT_LOGGED_IN,this);
//        }
//
//        WxMaJscode2SessionResult sessionResult = this.wxService.getUserService().getSessionInfo(code);
//        String openid = sessionResult.getOpenid();
//        if(openid==null||"".equals(openid)){
//            throw new BusinessException(ResultCode.USER_LOGIN_ERROR,this);
//        }
//        Subject subject = SecurityUtils.getSubject();
//        MockToken mockToken = new MockToken();
//        mockToken.setCode(code);
//        mockToken.setOpenid(openid);
//        subject.login(mockToken);
//        subject = SecurityUtils.getSubject();
//        Session session = subject.getSession(true);
//        if(session==null){
//            System.out.println("session==null");
//            return null;
//        }else {
//            User user=userService.selectByOpenid(openid);
//            user.setExtendS1((String) session.getId());
//            return user;
//        }
//
//    }
//}
