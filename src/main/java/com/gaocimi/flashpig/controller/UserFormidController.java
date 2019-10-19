package com.gaocimi.flashpig.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateData;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateMessage;
import com.gaocimi.flashpig.entity.HaircutOrder;
import com.gaocimi.flashpig.entity.User;
import com.gaocimi.flashpig.entity.UserFormid;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.HaircutOrderService;
import com.gaocimi.flashpig.service.UserFormidService;
import com.gaocimi.flashpig.service.UserService;
import com.gaocimi.flashpig.utils.MyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.error.WxErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 *
 * @author xp
 * @date 2019-10-17 18:14:29
 */
@RestController
@ResponseResult
@Api(value = "用户的formid的相关操作", description = "对用户的formid的相关操作")
public class UserFormidController {
    protected static final Logger logger = LoggerFactory.getLogger(WXLoginController.class);

    @Autowired
    private HaircutOrderService haircutOrderService;
    @Autowired
    private UserFormidService userFormidService;
    @Autowired
    private UserService userService;

    @ApiOperation(value = "添加用户的Formid")
    @PostMapping("/user/addUserFormid")
    public Map addUserFormid(String myOpenid,String formid) {
        Map map = new HashMap();

        User user = userService.findUserByOpenid(myOpenid);
        UserFormid userFormid = new UserFormid();

        userFormid.setUser(user);
        userFormid.setFormid(formid);
        userFormid.setOpenid(myOpenid);

        Date date = new Date(System.currentTimeMillis());
        userFormid.setCreatTime(date);

        userFormidService.save(userFormid);

        logger.info("id为"+user.getId()+"的用户“"+user.getName()+"”提交一个formid："+formid);
        map.put("message","提交formid并存入数据库成功");
        return map;
    }

    /**获取用户可使用的（7天内）的Formid，并删除7天以上和被取出来的Formid*/
    public UserFormid getOneUserFormid(List<UserFormid> formidList) {
        UserFormid userFormid;
        // 将Formid列表按提交的时间顺序排序
        Collections.sort(formidList, (r1, r2) -> {
            if (r1.getCreatTime().after(r2.getCreatTime())) {
                return 1;
            } else if (r2.getCreatTime().after(r1.getCreatTime())) {
                return -1;
            }
            return 0; //相等为0
        });

        //选择7天内的Formid，并删除7天以上的Formid
        while(formidList.size() > 0 ){

            userFormid = formidList.get(0);//获取最早提交的Formid
            Long day = MyUtils.getDifferenceNow(userFormid.getCreatTime());//取得提交Formid的时间与当前时刻相差的天数

            if (day<7) {
                formidList.remove(userFormid);
                userFormidService.delete(userFormid.getId());
                return userFormid;
            }else{
                formidList.remove(userFormid);
                userFormidService.delete(userFormid.getId());
            }
        }
        return null;
    }


    @ApiOperation(value = "测试Formid列表排序")
    @GetMapping("/formidTest")
    public Map test(int orderId) {

        Map map = new HashMap();
        List<UserFormid> formidList = haircutOrderService.findHaircutOrderById(orderId).user.getUserFormidList();

        if(formidList==null){
            System.out.println("为空");
            map.put("formidList",formidList);
            return map;
        }
        if(formidList.size()==0)
        {
            System.out.println("为0");
            map.put("formidList",formidList);
            return map;
        }
        // 将Formid列表按提交的时间顺序排序
        Collections.sort(formidList, (r1, r2) -> {
            if (r1.getCreatTime().after(r2.getCreatTime())) {
                return 1;
            } else if (r2.getCreatTime().after(r1.getCreatTime())) {
                return -1;
            }
            return 0; //相等为0
        });

        map.put("formidList",formidList);
        return map;
    }
}