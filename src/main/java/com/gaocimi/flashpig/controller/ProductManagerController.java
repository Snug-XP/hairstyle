package com.gaocimi.flashpig.controller;

import com.gaocimi.flashpig.entity.ProductManager;
import com.gaocimi.flashpig.entity.ProductOrder;
import com.gaocimi.flashpig.entity.User;
import com.gaocimi.flashpig.entity.UserAddress;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.*;
import com.gaocimi.flashpig.utils.xp.MyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author xp
 * @date 2020-5-16 10:22:06
 */
@RestController
@ResponseResult
@Api(value = "商品管理员服务", description = "商品管理员操作相关业务")
public class ProductManagerController {
    protected static final Logger logger = LoggerFactory.getLogger(ProductManagerController.class);

    @Autowired
    ProductManagerService productManagerService;
    @Autowired
    HairstylistService hairstylistService;
    @Autowired
    AdministratorService administratorService;
    @Autowired
    UserService userService;
    @Autowired
    ProductOrderService productOrderService;
    @Autowired
    PushSubscribeMessageController pushSubscribeMessageController;

    @ApiOperation(value = "商品管理员登录")
    @PostMapping("/productManager/login")
    public Map login(@RequestParam String myOpenid, @RequestParam String phone, @RequestParam String password) {
        Map map = new HashMap();

        User user = userService.findUserByOpenid(myOpenid);
        if (user == null) {
            logger.info("无效的用户！（商品管理员登录）");
            map.put("message", "无效的用户！");
            return map;
        }

        ProductManager productManager = productManagerService.findProductManagerByPhone(phone);
        if (productManager == null) {
            logger.info("账号（" + phone + "）不存在!");
            map.put("error", "账号不存在!");
            return map;
        }
        if (!password.equals(productManager.getPassword())) {
            map.put("error", "密码错误！");
            return map;
        }
        if (productManager.getStatus() != 1) {
            logger.info("账号（" + phone + "）状态异常！暂时禁止登录");
            map.put("error", "账号状态异常！暂时禁止登录");
            return map;
        }

        //登录成功，进行相应处理

        //去除该微信用户在其他商品管理员的登录标记（一个微信仅允许登录一个商品管理员账户）
        ProductManager productManager1 = productManagerService.findProductManagerByOpenid(myOpenid);
        while (productManager1 != null) {
            productManager1.setOpenid(null);
            productManagerService.edit(productManager1);
            productManager1 = productManagerService.findProductManagerByOpenid(myOpenid);
        }

        productManager.setOpenid(myOpenid);
        if (productManager.getAvatarUrl() == null) {
            //商品管理员第一次登录会自动设置其头像为微信头像
            productManager.setAvatarUrl(user.getPictureUrl());
        }
        productManagerService.edit(productManager);
        logger.info("商品管理员“" + productManager.getName() + "”（id=" + productManager.getId() + "）登录成功！");

        map.put("productManager", productManager);
        map.put("message", "登陆成功!");
        return map;
    }


//    @ApiOperation(value = "商品管理员忘记密码，进行重设密码")
//    @PostMapping("/productManager/reSetPassword")
//    public Map reSetPassword(@RequestParam String myOpenid, @RequestParam String phone, @RequestParam String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
//        Map map = new HashMap();
//
//        ProductManager productManager = productManagerService.findProductManagerByPhone(phone);
//        if (productManager == null) {
//            logger.info("账号（" + phone + "）不存在!");
//            map.put("error", "账号不存在!");
//            return map;
//        }
//
//
//        if (!MyMD5Util.validPassword(password, productManager.getPassword())) {
//            logger.info("商品管理员“" + productManager.getName() + "”（id=" + productManager.getId() + "）登录密码错误!（phone：" + phone + " ，wrongPassword:" + password );
//            map.put("error", "密码错误！");
//            return map;
//        }
//
//        //去除该微信用户在其他商品管理员的登录标记（一个微信仅允许登录一个商品管理员账户）
//        ProductManager productManager1 = productManagerService.findProductManagerByOpenid(myOpenid);
//        while (productManager1 != null) {
//            productManager1.setOpenid(null);
//            productManagerService.edit(productManager1);
//            productManager1 = productManagerService.findProductManagerByOpenid(myOpenid);
//        }
//
//        productManager.setOpenid(myOpenid);
//        productManagerService.edit(productManager);
//        logger.info("商品管理员“" + productManager.getName() + "”（id=" + productManager.getId() + "）登录成功！");
//
//
//        map.put("productManager", productManager);
//        map.put("message", "登陆成功!");
//        return map;
//    }


    @ApiOperation(value = "商品管理员退出登录")
    @PostMapping("/productManager/exit")
    public Map exit(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            ProductManager productManager = productManagerService.findProductManagerByOpenid(myOpenid);
            if (productManager == null || productManager.getStatus() != 1) {
                map.put("message", "当前未处于登录状态！");
                return map;
            } else {
                productManager.setOpenid(null);
                productManagerService.edit(productManager);
                logger.info("商品管理员“" + productManager.getName() + "”（id=" + productManager.getId() + "）退出登录！");
                map.put("message", "已退出！");
                return map;
            }
        } catch (Exception e) {
            logger.info("获取个人的顾客预约数情况列表失败！（后端发生某些错误）\n\n");
            map.put("error", "操作失败！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "商品管理员修改姓名密码（根据openid）")
    @PutMapping("/productManager/updateNameOrPassword")
    public Map updateNameOrPassword(@RequestParam String myOpenid,
                                    @RequestParam(value = "name", required = false) String name,
                                    @RequestParam(value = "password", required = false) String password) {
        Map map = new HashMap();
        try {
            ProductManager productManager = productManagerService.findProductManagerByOpenid(myOpenid);
            if (productManager == null) {
                map.put("error", "未登录！");
                return map;
            }

            if (name != null)
                productManager.setName(name);
            if (password != null) {
                productManager.setPassword(password);
            }

            productManagerService.edit(productManager);


            logger.info("商品管理员 " + productManager.getName() + "（id=" + productManager.getId() + "）重新修改了姓名或密码");
            map.put("message", "修改成功！");
            return map;

        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("商品管理员信息修改失败！（后端发生某些错误）");
            map.put("error", "商品管理员信息修改失败！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


    @ApiOperation(value = "根据登录微信的openid,获取商品管理员信息", produces = "application/json")
    @GetMapping("/productManager")
    public Map getProductManager(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            ProductManager productManager = productManagerService.findProductManagerByOpenid(myOpenid);
            if (productManager == null) {
                logger.info("还未登录(根据openid获取商品管理员信息)");
                map.put("error", "请先登录！");
                return map;
            }
            map.put("productManager", productManager);
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取商品管理员信息失败！（后端发生某些错误）");
            map.put("error", "获取商品管理员信息失败！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "商品管理员设置某个订单的物流运单号")
    @PutMapping("/productManager/productOrder/setLogisticsNumber")
    public Map setLogisticsNumber(@RequestParam String myOpenid, @RequestParam Integer productOrderId,
                                  @RequestParam String logisticsNumber) {
        Map map = new HashMap();
        try {
            ProductManager productManager = productManagerService.findProductManagerByOpenid(myOpenid);
            if (productManager == null) {
                logger.info("还未登录(商品管理员设置某个订单的物流运单号)");
                map.put("error", "请先登录！");
                return map;
            }
            ProductOrder productOrder = productOrderService.findById(productOrderId);
            if (productOrder == null) {
                logger.info("id为" + productOrderId + "的商品订单不存在（修改商品订单信息）！");
                map.put("error", "该商品订单不存在！");
                return map;
            }

            productOrder.setLogisticsNumber(logisticsNumber);
            productOrderService.edit(productOrder);
            logger.info("商品管理员 " + productManager.getName() + "（id=" + productManager.getId() + "）设置了订单（id=" + productOrderId + "）的物流运单号:" + logisticsNumber);
            map.put("message", "设置成功");
            Map map1 = pushSubscribeMessageController.pushShippedMessage(productOrder);
            if (map1.get("message") != null)
                map.put("message2", map1.get("message"));
            if (map1.get("error") != null)
                map.put("error2", map1.get("error"));
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("商品管理员设置某个订单的物流运单号！（后端发生某些错误）");
            map.put("error", "操作失败！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "商品管理员获取最近的商品订单列表(分页展示)")
    @GetMapping("/productManager/productOrder/getRecentProductOrderList")
    public Map getRecentProductOrderList(@RequestParam String myOpenid,
                                     @RequestParam(name = "status", required = false) Integer status,
                                     @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                     @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {

        Map map = new HashMap();
        try {
            ProductManager productManager = productManagerService.findProductManagerByOpenid(myOpenid);
            if (productManager == null) {
                logger.info("还未登录(商品管理员获取所有最近的商品订单列表)");
                map.put("error", "请先登录！");
                return map;
            }
            Page<ProductOrder> page = null;
            if (status != null)
                page= productOrderService.findAllByStatus(status,pageNum,pageSize);//已按订单创建时间倒序排序
            else
                page = productOrderService.findAll(pageNum,pageSize);//已按订单创建时间倒序排序

            map.put("page", page);
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取所有最近的商品订单列表失败！！（后端发生某些错误）");
            map.put("error", "获取所有最近的商品订单列表失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }
}
