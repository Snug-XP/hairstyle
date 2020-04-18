package com.gaocimi.flashpig.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaCodeLineColor;
import com.gaocimi.flashpig.entity.Hairstylist;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.HairstylistService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xp
 * @date 2019-12-27 15:09:25
 */
@RestController
@ResponseResult
@Api(value = "小程序码生成相关操作", description = "微信小程序码相关业务")
public class WxAppletCodeController {
    protected static final Logger logger = LoggerFactory.getLogger(WxLoginController.class);

    @Autowired
    private HairstylistService hairstylistService;
    @Autowired
    private WxMaService wxService;
    @Autowired
    private OssAccessController ossController;


    @ApiOperation(value = "生成跳转发型师主页面的小程序码")
    @PostMapping("/hairstylist/appletCodeGeneration")
    public Map appletCodeGeneration(@RequestParam String myOpenid, @RequestParam(value = "scene", required = false) String scene,
                                    @RequestParam(value = "page", required = false) String page) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null) {
                logger.info("该发型师用户不存在！！生成小程序码失败）");
                map.put("error", "该发型师用户不存在！！生成小程序码失败");
                return map;
            }

            if (scene == null)
                scene =hairstylist.getId().toString();
            if (page == null)
                page = "pages/shares/shares";
            WxMaCodeLineColor wxMaCodeLineColor = new WxMaCodeLineColor("0","0","0");
            File wxacode = wxService.getQrcodeService().createWxaCodeUnlimit(scene, page ,1,false,wxMaCodeLineColor,false);
            String pathAndName = "hairstylist/" + hairstylist.getId() + "/MyAppletCode/appletCode";
            map = ossController.saveObject(wxacode, pathAndName);
            if (map.get("image_src") != null) {
                logger.info("生成发型师“"+hairstylist.getHairstylistName()+"”(id = " + hairstylist.getId() + ")的主页小程序码成功");
                hairstylist.setMyAppletCodeUrl(map.get("image_src").toString());
                hairstylistService.edit(hairstylist);
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("后台发生异常！！(生成跳转发型师主页面的小程序码)");
            map.put("error", "后台发生异常！！");
            return map;
        }
    }
}