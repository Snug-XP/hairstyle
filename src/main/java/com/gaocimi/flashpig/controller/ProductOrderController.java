package com.gaocimi.flashpig.controller;

import com.gaocimi.flashpig.entity.*;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author xp
 * @date 2020-5-2 14:39:16
 * @description 商品订单相关业务
 */
@RestController
@ResponseResult
@Api(value = "商品订单服务相关业务", description = "商品订单相关业务")
public class ProductOrderController {
    protected static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    ProductOrderService productOrderService;
    @Autowired
    UserService userService;
    @Autowired
    ProductService productService;
    @Autowired
    UserAddressService userAddressService;

    @ApiOperation(value = "用户创建商品订单")
    @PostMapping("/user/addProductOrder")
    public Map addProductOrder(@RequestParam String myOpenid, @RequestParam Integer productId,
                               @RequestParam Integer productQuantity, @RequestParam String userPhone,
                               @RequestParam Integer addressId) {
        Map map = new HashMap();
        try {

            User user = userService.findUserByOpenid(myOpenid);
            if (user == null) {
                logger.info("（" + myOpenid + "）该用户不存在！(创建商品订单)");
                map.put("error", "无效的用户！！");
                return map;
            }

            Product product = productService.findProductById(productId);
            if (product == null) {
                logger.info("（productId=" + productId + "）该商品不存在！(创建商品订单)");
                map.put("error", "该商品不存在！");
                return map;
            }
            UserAddress address = userAddressService.findUserAddressById(addressId);
            if (address == null) {
                logger.info("（addressId=" + addressId + "）该地址不存在！(创建商品订单)");
                map.put("error", "该地址不存在！");
                return map;
            }

            ProductOrder productOrder = new ProductOrder();
            productOrder.setUser(user);
            productOrder.setProduct(product);
            productOrder.setProductQuantity(productQuantity);
            productOrder.setTotalPrice(product.getPrice()*productQuantity);
            productOrder.setUserPhone(userPhone);
            productOrderService.save(productOrder);
            
            logger.info("用户“" + user.getName() + "”（id=" + user.getId() + "）创建了一个“" + product.getName() + "”（id=" + productOrder.getId() + "，number="+productQuantity+"）的商品订单(id="+productOrder.getId()+")");
            map.put("message", "订单创建成功！");
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("商品订单创建失败！！（后端发生某些错误）");
            map.put("error", "订单创建失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

//    @ApiOperation(value = "删除商品订单")
//    @DeleteMapping("/user/deleteProductOrder")
//    public Map deleteProductOrder(@RequestParam String myOpenid, @RequestParam Integer productOrderId) {
//
//        Map map = new HashMap();
//        try {
//            ProductOrder productOrder = productOrderService.findProductOrderById(productOrderId);
//            if (productOrder == null) {
//                logger.info("id为" + productOrderId + "的商品订单不存在（删除商品订单）！");
//                map.put("error", "该商品订单不存在！！");
//                return map;
//            }
//
//            User user = userService.findUserByOpenid(myOpenid);
//            if (user == null) {
//                logger.info("删除商品订单失败！！（没有权限！！）");
//                map.put("error", "无权限！");
//                return map;
//            }
//
//            logger.info("用户“" + user.getName() + "”（id=" + user.getId() + "）删除了商品订单“" + productOrder.getName() + "”（id=" + productOrder.getId() + "）");
//
//            productOrderService.delete(productOrderId);
//            map.put("message", "商品订单删除成功！");
//            return map;
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//            logger.info("商品订单删除失败！！（后端发生某些错误）");
//            map.put("error", "商品订单删除失败！！（后端发生某些错误）");
//            e.printStackTrace();
//            return map;
//        }
//    }
//
//    @ApiOperation(value = "修改商品订单信息", notes = "没有相关属性将不修改其原有信息")
//    @PutMapping("/user/updateProductOrder")
//    public Map updateProductOrder(@RequestParam String myOpenid, @RequestParam Integer productOrderId,
//                             @RequestParam(value = "name", required = false) String name,
//                             @RequestParam(value = "introduction", required = false) String introduction,
//                             @RequestParam(value = "price", required = false) Double price,
//                             @RequestParam(value = "remainingQuantity", required = false) Integer remainingQuantity,
//                             @RequestParam(value = "tagList", required = false) List<String> tagList,
//                             @RequestParam(value = "imgUrlList", required = false) List<String> imgUrlList) {
//        Map map = new HashMap();
//        try {
//
//            ProductOrder productOrder = productOrderService.findProductOrderById(productOrderId);
//
//            if (productOrder == null) {
//                logger.info("id为" + productOrderId + "的商品订单不存在（修改商品订单信息）！");
//                map.put("error", "该商品订单不存在！！");
//                return map;
//            }
//
//            User user = userService.findUserByOpenid(myOpenid);
//            if (user == null) {
//                logger.info("非用户用户操作！！（修改商品订单信息:操作openid=" + myOpenid + "）");
//                map.put("error", "无权操作！！");
//                return map;
//            }
//
//            if (name != null)
//                productOrder.setName(name);
//            if (introduction != null)
//                productOrder.setIntroduction(introduction);
//            if (price != null)
//                productOrder.setPrice(price);
//            if (remainingQuantity != null)
//                productOrder.setRemainingQuantity(remainingQuantity);
//            if (tagList != null)
//                productOrder.setTag(tagList);
//
//            productOrderService.edit(productOrder);
//
//            if (imgUrlList != null && !imgUrlList.isEmpty()) {
//                //删除原有商品订单的图片url
//                imageUrlService.deleteAllByProductOrderId(productOrderId);
//
//                //储存商品订单的图片url列表
//                for (String imageUrlStr : imgUrlList) {
//                    ProductOrderImageUrl imageUrl = new ProductOrderImageUrl();
//                    imageUrl.setProductOrder(productOrder);
//                    imageUrl.setImageUrl(imageUrlStr);
//
//                    imageUrlService.save(imageUrl);
//                }
//            }
//            logger.info("用户“" + user.getName() + "”（id=" + user.getId() + "）修改了商品订单“" + productOrder.getName() + "”（id=" + productOrder.getId() + "）的信息：");
//            map.put("message", "商品订单信息修改成功！");
//            return map;
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//            logger.info("商品订单信息修改失败！！（后端发生某些错误）");
//            map.put("error", "商品订单信息修改失败！！（后端发生某些错误）");
//            e.printStackTrace();
//            return map;
//        }
//    }
//
//
//    @ApiOperation(value = "获取自己创建的的商品订单列表(分页展示)", notes = "权限：发型师(用户会有一个发型师记录)")
//    @GetMapping("/user/getMyProductOrderList")
//    public Map getMyProductOrderList(@RequestParam String myOpenid,
//                                @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
//                                @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
//
//        Map map = new HashMap();
//        try {
//            User user = userService.findUserByOpenid(myOpenid);
//            if (user == null) {
//                logger.info("非用户用户操作！！（获取自己创建的的商品订单列表:操作openid=" + myOpenid + "）");
//                map.put("error", "无权操作！！");
//                return map;
//            }
//
//            List<ProductOrder> tempProductOrderList = user.getProductOrderList();
//
//            if (tempProductOrderList == null || tempProductOrderList.isEmpty()) {
//                map.put("message", "你还没有创建过商品订单哦~");
//                return map;
//            }
//
//            // 按创建时间倒序排序
//            Collections.sort(tempProductOrderList, (o1, o2) -> {
//                if (o2.getCreateTime().after(o1.getCreateTime())) {
//                    return 1;
//                } else if (o1.getCreateTime().after(o2.getCreateTime())) {
//                    return -1;
//                }
//                return 0; //相等为0
//            });
//
//            map.put("page", MyUtils.getPage(tempProductOrderList, pageNum, pageSize));
//            return map;
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//            logger.info("获取自己创建的商品订单列表失败！！（后端发生某些错误）");
//            map.put("error", "获取自己创建的商品订单列表失败！！（后端发生某些错误）");
//            e.printStackTrace();
//            return map;
//        }
//    }
//
//
//    @ApiOperation(value = "获取单个商品订单信息", produces = "application/json")
//    @GetMapping("/productOrder/getOne")
//    public Map getOne(@RequestParam String myOpenid, @RequestParam Integer productOrderId) {
//        Map map = new HashMap();
//        try {
//            User user = userService.findUserByOpenid(myOpenid);
//            ProductOrder productOrder = productOrderService.findProductOrderById(productOrderId);
//            if (user == null) {
//                logger.info("（" + myOpenid + "）该用户不存在！(获取单个商品订单信息)");
//                map.put("error", "无效的用户！！");
//                return map;
//            }
//            if (productOrder == null) {
//                logger.info("id为" + productOrderId + "的商品订单不存在！(获取单个商品订单信息)");
//                map.put("error", "该商品订单不存在！");
//                return map;
//            }
//
//            if (userToProductOrderService.findByUserAndProductOrder(user.getId(), productOrderId) != null) {
//                map.put("isCollected", "yes");
//            } else {
//                map.put("isCollected", "no");
//            }
//            map.put("productOrder", productOrder);
//            return map;
//
//        } catch (Exception e) {
//            logger.info("后端发生异常(获取单个商品订单信息)：\n");
//            map.put("error", "抱歉，后端发生异常!!");
//            e.printStackTrace();
//            return map;
//        }
//    }
//
//    @ApiOperation(value = "获取所有商品订单列表（分页展示）")
//    @GetMapping("/productOrders/getAll")
//    public Page<ProductOrder> getAllByPage(@RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
//                                      @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
//    ) {
//        Page<ProductOrder> page = productOrderService.findAll(pageNum, pageSize);
//        return page;
//    }

//    
//    @ApiOperation(value = "普通用户分页获取自己收藏的商品订单列表")
//    @GetMapping("/productOrder/getMyCollection")
//    public Map getMyCollectionByPage(@RequestParam String myOpenid,
//                                     @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
//                                     @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
//        Map map = new HashMap();
//        try {
//
//            User user = userService.findUserByOpenid(myOpenid);
//            if (user == null) {
//                logger.info("（" + myOpenid + "）该用户不存在！(获取自己收藏的商品订单列表)");
//                map.put("error", "无效的用户！！");
//                return map;
//            }
//            List<UserToProductOrder> tempProductOrderList = user.getProductOrderRecordList();
//            List<ProductOrder> resultProductOrderList = new ArrayList<>();
//
//            if (tempProductOrderList == null || tempProductOrderList.isEmpty()) {
//                map.put("message", "你还没有收藏商品订单哦~");
//                return map;
//            }
//
//            // 按时间倒序排序
//            Collections.sort(tempProductOrderList, (o1, o2) -> {
//                if (o2.getCreateTime().after(o1.getCreateTime())) {
//                    return 1;
//                } else if (o1.getCreateTime().after(o2.getCreateTime())) {
//                    return -1;
//                }
//                return 0; //相等为0
//            });
//
//            //获取所求页数的商品订单数据
//            int first = pageNum * pageSize;
//            int last = pageNum * pageSize + pageSize - 1;
//            for (int i = first; i <= last && i < tempProductOrderList.size(); i++) {
//                resultProductOrderList.add(tempProductOrderList.get(i).productOrder);
//            }
//
//            //包装分页数据
//            Pageable pageable = PageRequest.of(pageNum, pageSize);
//            Page<ProductOrder> page = new PageImpl<>(resultProductOrderList, pageable, tempProductOrderList.size());
//
//            map.put("page", page);
//            return map;
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//            logger.info("获取自己收藏的商品订单列表失败！！（后端发生某些错误）");
//            map.put("error", "获取收藏列表失败！！（后端发生某些错误）");
//            e.printStackTrace();
//            return map;
//        }
//    }

}
