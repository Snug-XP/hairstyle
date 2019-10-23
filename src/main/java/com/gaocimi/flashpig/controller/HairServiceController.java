package com.gaocimi.flashpig.controller;

import com.gaocimi.flashpig.entity.HairService;
import com.gaocimi.flashpig.entity.Hairstylist;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.HairServiceService;
import com.gaocimi.flashpig.service.HairstylistService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xp
 * @date 2019-10-13 16:14:56
 * @description 发型服务相关业务
 */
@RestController
@ResponseResult
@Api(value = "管理端发型服务", description = "发型服务相关业务")
public class HairServiceController {
    protected static final Logger logger = LoggerFactory.getLogger(HairstylistController.class);

    @Autowired
    HairServiceService hairServiceService;
    @Autowired
    HairstylistService hairstylistService;

    @ApiOperation(value = "添加发型服务")
    @PostMapping("/hairstylist/addHairService")
    public Map addHairService(String myOpenid, String serviceName, String description, Double price) {

        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if ( hairstylist == null || hairstylist.getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else {
                HairService hairService = new HairService();

                hairService.setHairstylist(hairstylist);
                hairService.setServiceName(serviceName);
                hairService.setDescription(description);
                hairService.setPrice(price);

                hairServiceService.save(hairService);

                logger.info("发型师(" + hairstylist.getHairstylistName() + ")添加服务项目：" + serviceName + "  " + description + "  " + price);
                map.put("message", "添加成功！");
                return map;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("添加发型服务失败！！（后端发生某些错误）");
            map.put("error", "添加发型服务失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "根据服务项目id，修改个人的服务项目")
    @PostMapping("/hairstylist/editHairService")
    public Map editHairService(String myOpenid, int serviceId, String serviceName, String description, Double price) {

        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if ( hairstylist == null || hairstylist.getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            }
            HairService hairService = hairServiceService.findHairServiceById(serviceId);
            if (hairService == null) {
                logger.info("所修改服务项目不存在！");
                map.put("error", "所修改服务项目不存在！");
                return map;
            }
            //判断该服务是不是该用户的
            if (myOpenid.equals(hairService.getHairstylist().getOpenid())) {
                logger.info("发型师(" + hairstylist.getHairstylistName() + ")修改服务项目：" + hairService.getServiceName() + " -> " + serviceName + "  " + description + "  " + price);

                hairService.setServiceName(serviceName);
                hairService.setDescription(description);
                hairService.setPrice(price);

                hairServiceService.edit(hairService);

                map.put("message", "修改成功！");
                return map;
            } else {
                logger.info("没有修改服务项目权限");
                map.put("error", "无修改权限！！");
                return map;
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("修改发型服务失败！！（后端发生某些错误）");
            map.put("error", "修改发型服务失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "根据发型师id，获取发型师的服务列表")
    @GetMapping("/getHairstylistServiceList")
    public Map getHairstylistServiceList(int hairstylistId) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistById(hairstylistId);
            if (hairstylist == null) {
                logger.info("未找到该发型师用户");
                map.put("error", "未找到该发型师用户！！·");
                return map;
            }
            map = getServiceList(hairstylist.getOpenid());
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取发型师信息失败！！（后端发生某些错误）");
            map.put("error", "获取发型师信息失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "获取自己的服务列表")
    @GetMapping("/hairstylist/getServiceList")
    public Map getServiceList(String myOpenid) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if ( hairstylist == null || hairstylist.getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else {
                map.put("serviceList", hairstylist.hairServiceList);
                return map;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取自己的服务列表失败！！（后端发生某些错误）");
            map.put("error", "获取服务列表失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "根据服务项目id,删除个人服务项目")
    @DeleteMapping("/hairstylist/deleteService")
    public Map deleteService(String myOpenid, int serviceId) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if ( hairstylist == null || hairstylist.getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "非发型师用户操作！！");
                return map;
            }
            HairService hairService = hairServiceService.findHairServiceById(serviceId);
            if (hairService == null) {
                logger.info("所删除服务项目不存在！");
                map.put("error", "服务项目不存在！");
                return map;
            }
            //判断该服务是不是该发型师用户的
            if (myOpenid.equals(hairService.getHairstylist().getOpenid())) {
                hairServiceService.delete(serviceId);
                logger.info(hairstylist.getHairstylistName() + "(" + hairstylist.getOpenid() + ")删除服务项目：" + hairService.getServiceName());
                map.put("message", "删除成功！");
                return map;
            } else {
                logger.info("没有删除服务项目权限");
                map.put("error", "无删除权限！");
                return map;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("删除个人服务项目操作失败！！（后端发生某些错误）");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


    /**根据发型师openid，同时修改全部的服务项目列表（目前没用）*/
    public Map deleteServiceByHairstylistId(String myOpenid,@RequestParam(value = "hairService", required = false) List<String> hairService,
                                             @RequestParam(value = "description", required = false) List<String> description,
                                             @RequestParam(value = "price", required = false) List<Double> price){

        Map map =new HashMap();
        Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
        //下面保存该理发师的服务项目
        //先删除原来对应的的服务项目
        hairServiceService.deleteAllByHairstylistId(hairstylist.getId());
        if (hairService != null) {
            int serviceSize = hairService.size();
            if (serviceSize != price.size()) {
                logger.info("服务与价格数量不匹配！！");
                map.put("error", "服务与价格不匹配！！");
                return map;
            }
            if (serviceSize != description.size()) {
                logger.info("服务与描述数量不匹配！！");
                map.put("error", "服务与描述不匹配！！");
                return map;
            }

            for (int i = 0; i < serviceSize; i++) {
                HairService hairService1 = new HairService();
                hairService1.setHairstylist(hairstylist);
                hairService1.setServiceName(hairService.get(i));
                hairService1.setDescription(description.get(i));
                hairService1.setPrice(price.get(i));
                hairServiceService.save(hairService1);
            }

            logger.info("hairService有" + serviceSize + "个： " + hairService);
            map.put("message", "同时修改全部服务项目成功！");
            return map;
        }else{
            //没有新的服务项目传入，说明把原有服务项目都删了
            logger.info("删除全部服务项目");
            map.put("message", "删除全部服务项目成功！");
            return map;
        }

    }
}
