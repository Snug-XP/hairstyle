package com.gaocimi.flashpig.controller;

import com.gaocimi.flashpig.entity.HaircutOrder;
import com.gaocimi.flashpig.entity.Hairstylist;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.AdministratorService;
import com.gaocimi.flashpig.service.HaircutOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liyutg
 * @date 2019/6/12 2:15
 * @description
 */
@RestController
@ResponseResult
@Api(value = "用户订单操作服务",description = "操作用户订单相关业务")
public class HaircutOrderController {
    protected static final Logger logger = LoggerFactory.getLogger(HairstylistController.class);

    @Autowired
    HaircutOrderService haircutOrderService;

    @Autowired
    AdministratorService administratorService;

//    @ApiOperation(value = "添加用户订单",notes = "m1")
//    @PostMapping("/haircutOrder")
//    @ResponseStatus(HttpStatus.CREATED)
//    public Map addHaircutOrder(@Validated HaircutOrder haircutOrders) {
//        Map map = new HashMap();
//        try{
//            haircutOrderService.save(haircutOrders);
//            logger.info("添加用户订单成功！");
//            map.put("message","添加用户订单成功！");
//        } catch (Exception e) {
//            e.printStackTrace();
//            map.put("error", "操作失败！！（后端发生某些错误，例如数据库连接失败）");
//        }
//        return map;
//    }
//
//    @ApiOperation(value = "删除用户订单",notes = "m1")
//    @DeleteMapping("/haircutOrder/{haircutOrderId}")
//    public void deleteHaircutOrder(@PathVariable("haircutOrderId") Integer haircutOrderId) {
//        haircutOrderService.delete(haircutOrderId);
//    }
//
//    @ApiOperation(value = "修改用户订单",notes = "m1")
//    @PutMapping("/haircutOrder")
//    public void updateHaircutOrder(@Validated HaircutOrder haircutOrders) {
//        haircutOrderService.edit(haircutOrders);
//    }
//
//
//    @ApiOperation(value = "获取单个用户订单信息",notes = "m1",produces = "application/json")
//    @GetMapping("/haircutOrder/{haircutOrderId}")
//    public HaircutOrder getOne( @PathVariable("haircutOrderId") Integer haircutOrderId) {
//        return haircutOrderService.findHaircutOrderById(haircutOrderId);
//    }

    @ApiOperation(value = "分页获取所有订单列表",notes = "仅管理员有权限",produces = "application/json")
    @GetMapping("/haircutOrders/all/{myOpenid}")
    public Map getHaircutOrdersPage(@PathVariable("myOpenid") String openid,
                                    @RequestParam(name="pageNum",defaultValue="0") int pageNum,
                                    @RequestParam(name="pageSize",defaultValue="10") int pageSize
    ) {
        Map map = new HashMap();
        try {
            if (administratorService.isExist(openid)) {
                Page<HaircutOrder> page = haircutOrderService.findAll(pageNum, pageSize);
                map.put("page", page);
                logger.info("获取订单列表成功！");
                return map;
            } else {
                logger.info("获取订单列表失败！！（没有权限！！）");
                map.put("error", "获取订单列表失败！！（没有权限！！）");
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("error", "获取订单列表失败！！（后端发生某些错误，例如数据库连接失败）");
            return map;
        }
    }

//    @ApiOperation(value = "获取某个顾客关于自己的预约列表", notes = "返回的是预约单列表，请从预约单中选取你要的属性就好")
//    @GetMapping("/hairstylist/getOrderListFromOneUser/{myOpenid}")
//    public Map getOrderListFromOneUser(@PathVariable("myOpenid") String openid,int user_id) {
//        Map map = new HashMap();
//        try {
//            if (hairstylistService.findHairstylistByOpenid(openid) == null || hairstylistService.findHairstylistByOpenid(openid).getApplyState()!=1 ) {
//                logger.info("非发型师用户操作！！");
//                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
//                return map;
//            } else {
//                Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(openid);
//                List<HaircutOrder> tempOrderList = hairstylist.haircutOrderList;
//                List<HaircutOrder> resultOrderList = new ArrayList<HaircutOrder>();
//                for (HaircutOrder order : tempOrderList) {
//                    if(order.user.getId() == user_id){
//                        resultOrderList.add(order);
//                    }
//                }
//                if (resultOrderList.size() > 0)
//                    map.put("orderList", resultOrderList);
//                else
//                    map.put("message", "没有匹配的订单");
//                return map;
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//            map.put("error", "操作失败！！（后端发生某些错误，例如数据库连接失败）");
//            return map;
//        }
//    }

}
