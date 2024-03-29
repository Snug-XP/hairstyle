package com.gaocimi.flashpig.controller;

import com.alibaba.fastjson.JSONObject;
import com.gaocimi.flashpig.entity.*;
import com.gaocimi.flashpig.model.RankingData;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.*;
import com.gaocimi.flashpig.utils.xp.MyMD5Util;
import com.gaocimi.flashpig.utils.xp.MyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author xp
 * @date 2019-11-14 20:18:35
 * @description
 */
@RestController
@ResponseResult
@Api(value = "门店服务", description = "门店操作相关业务")
public class ShopController {
    protected static final Logger logger = LoggerFactory.getLogger(ShopController.class);

    @Autowired
    ShopService shopService;
    @Autowired
    HairstylistService hairstylistService;
    @Autowired
    AdministratorService administratorService;
    @Autowired
    ShopImageUrlService shopImageUrlService;
    @Autowired
    PushSubscribeMessageController pushWxMsg;
    @Autowired
    UserService userService;

    @ApiOperation(value = "门店登录")
    @PostMapping("/shop/login")
    public Map shopLogin(@RequestParam String myOpenid, @RequestParam String phone, @RequestParam String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Map map = new HashMap();
        User user = userService.findUserByOpenid(myOpenid);
        if(user==null){
            logger.info("无效的用户！（门店登录）");
            map.put("message","无效的用户！");
            return map;
        }
        Shop shop = shopService.findShopByPhone(phone);
        if (shop == null) {
            logger.info("账号（" + phone + "）不存在!");
            map.put("error", "账号不存在!");
            return map;
        }


        if (!MyMD5Util.validPassword(password, shop.getPassword())) {
            logger.info("门店“" + shop.getShopName() + "”（id=" + shop.getId() + "）登录密码错误!（phone：" + phone + " ，wrongPassword:" + password );
            map.put("error", "密码错误！！");
            return map;
        }

//        if (shop.getApplyStatus() == 0) {
//            logger.info("账号（" + phone + "）正在审核中!");
//            map.put("message", "正在审核中!");
//            return map;
//        } else if (shop.getApplyStatus() == -1) {
//            logger.info("账号（" + phone + "）审核未通过!，请重新入驻申请");
//            map.put("message", "审核未通过!，请重新注册");
//            return map;
//        }
//
//        if (shop.getApplyStatus() != 1) {
//            logger.info("账号（" + phone + "）状态异常！！暂时禁止登录");
//            map.put("error", "账号状态异常！！暂时禁止登录");
//            return map;
//        }

        //去除该微信用户在其他门店的登录标记（一个微信仅允许登录一个门店账户）
        Shop shop1 = shopService.findShopByOpenid(myOpenid);
        while (shop1 != null) {
            shop1.setOpenid(null);
            shopService.edit(shop1);
            shop1 = shopService.findShopByOpenid(myOpenid);
        }

        shop.setOpenid(myOpenid);
        shopService.edit(shop);
        logger.info("门店“" + shop.getShopName() + "”（id=" + shop.getId() + "）登录成功！");


        map.put("shop", shop);
        map.put("message", "登陆成功!");
        return map;
    }


//    @ApiOperation(value = "门店忘记密码，进行重设密码")
//    @PostMapping("/shop/reSetPassword")
//    public Map reSetPassword(@RequestParam String myOpenid, @RequestParam String phone, @RequestParam String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
//        Map map = new HashMap();
//
//        Shop shop = shopService.findShopByPhone(phone);
//        if (shop == null) {
//            logger.info("账号（" + phone + "）不存在!");
//            map.put("error", "账号不存在!");
//            return map;
//        }
//
//
//        if (!MyMD5Util.validPassword(password, shop.getPassword())) {
//            logger.info("门店“" + shop.getShopName() + "”（id=" + shop.getId() + "）登录密码错误!（phone：" + phone + " ，wrongPassword:" + password );
//            map.put("error", "密码错误！！");
//            return map;
//        }
//
//        //去除该微信用户在其他门店的登录标记（一个微信仅允许登录一个门店账户）
//        Shop shop1 = shopService.findShopByOpenid(myOpenid);
//        while (shop1 != null) {
//            shop1.setOpenid(null);
//            shopService.edit(shop1);
//            shop1 = shopService.findShopByOpenid(myOpenid);
//        }
//
//        shop.setOpenid(myOpenid);
//        shopService.edit(shop);
//        logger.info("门店“" + shop.getShopName() + "”（id=" + shop.getId() + "）登录成功！");
//
//
//        map.put("shop", shop);
//        map.put("message", "登陆成功!");
//        return map;
//    }


    @ApiOperation(value = "门店退出登录")
    @GetMapping("/shop/exit")
    public Map exit(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Shop shop = shopService.findShopByOpenid(myOpenid);
            if (shop == null || shop.getApplyStatus() != 1) {
                map.put("message", "当前未处于登录状态！");
                return map;
            } else {
                shop.setOpenid(null);
                shopService.edit(shop);
                logger.info("门店“" + shop.getShopName() + "”（id=" + shop.getId() + "）退出登录！");
                map.put("message", "已退出！");
                return map;
            }
        } catch (Exception e) {
            logger.info("门店退出登录失败！！（后端发生某些错误）\n\n");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


    @ApiOperation(value = "门店注册")
    @PostMapping("/shop/register/apply")
    public Map addShop(HttpServletRequest request,
                       @RequestParam String myOpenid,
                       @RequestParam(value = "shopName", required = false) String shopName,
                       @RequestParam(value = "phone", required = false) String phone,
                       @RequestParam(value = "password", required = false) String password,
                       @RequestParam(value = "logoUrl", required = false) String logoUrl,
                       @RequestParam(value = "operatingLicensePictureUrl", required = false) String operatingLicensePictureUrl,
                       @RequestParam(value = "province", required = false) String province,
                       @RequestParam(value = "city", required = false) String city,
                       @RequestParam(value = "district", required = false) String district,
                       @RequestParam(value = "address", required = false) String address,
                       @RequestParam(value = "longitude", required = false) Double longitude,
                       @RequestParam(value = "latitude", required = false) Double latitude) {
        Map map = new HashMap();


        if (shopName == null || phone == null || password == null || province == null || city == null || district == null || address == null || longitude == null || latitude == null) {
            logger.info("请完整填写个人资料!(门店注册申请)");
            logger.info("传入的数据：" + JSONObject.toJSON(request.getParameterMap()) + "\n");
            map.put("message", "请完整填写个人资料！");
            return map;
        }

        Shop shop = shopService.findShopByPhone(phone);
        try {
            if (shop != null) {
                //之前有申请过

                //之前申请失败了，删除记录
                if (shop.getApplyStatus() == -1) {
                    logger.info("删除“" + shop.getShopName() + "”（id=" + shop.getId() + "）之前申请失败的记录,并重新申请\n");
                    shopService.delete(shop.getId());
                } else {
                    logger.info(shop.getShopName() + "重复申请！！你的申请正在审核中或者已通过，无需重复提交！！");
                    map.put("message", "您的申请正在审核中或者已通过，无需重复提交！！");
                    return map;
                }
            }

            shop = new Shop();

            if (!MyUtils.isMobileNO(phone)) {
                logger.info("电话号码（" + phone + "）不合法！！");
                map.put("error", "电话号码（" + phone + "）不合法！！");
                return map;
            }

            //去除该微信用户在其他门店的登录标记（一个微信仅允许登录一个门店账户）
            Shop shop1 = shopService.findShopByOpenid(myOpenid);
            while (shop1 != null) {
                shop1.setOpenid(null);
                shopService.edit(shop1);
                shop1 = shopService.findShopByOpenid(myOpenid);
            }

            //将密码加密存储
            String encryptedPassword = null;
            try {
                encryptedPassword = MyMD5Util.getEncryptedPwd(password);
                shop.setPassword(encryptedPassword);
            } catch (NoSuchAlgorithmException e) {
                map.put("error", "加密算法在当前环境中不可用，注册失败！");
                logger.info("加密算法在当前环境中不可用，注册失败！");
                e.printStackTrace();
                return map;
            } catch (UnsupportedEncodingException e) {
                map.put("error", "密码包含不支持的字符编码，注册失败！");
                logger.info("密码包含不支持的字符编码，注册失败！");
                e.printStackTrace();
                return map;
            }

            shop.setOpenid(myOpenid);

            shop.setShopName(shopName);
            shop.setPhone(phone);
            shop.setLogoUrl(logoUrl);
            shop.setOperatingLicensePictureUrl(operatingLicensePictureUrl);
            shop.setProvince(province);
            shop.setCity(city);
            shop.setDistrict(district);
            shop.setAddress(address);
            shop.setLongitude(longitude);
            shop.setLatitude(latitude);


            shopService.save(shop);


        } catch (Exception e) {
            e.printStackTrace();
            logger.info("传入的数据：" + JSONObject.toJSON(request.getParameterMap()) + "\n");
            map.put("error", "门店提交申请失败！请检查输入数据（也有可能后端发生错误）");
            return map;
        }
        logger.info("传入的数据：" + JSONObject.toJSON(request.getParameterMap()) + "\n");
        logger.info("id为" + shop.getId() + "的门店“" + shop.getShopName() + "”注册成功！");

        map.put("message", "注册成功！");
        return map;
    }

    @ApiOperation(value = "门店认证申请")
    @PostMapping("/shop/certify")
    public Map certify(@RequestParam String myOpenid,
                       @RequestParam(value = "operatingLicensePictureUrl", required = false) String operatingLicensePictureUrl) {
        Map map = new HashMap();
        Shop shop = shopService.findShopByOpenid(myOpenid);
        try {
            if (shop == null) {
                logger.info("未登录操作！！");
                map.put("error", "请先登录！！");
                return map;
            }
            if (shop.getApplyStatus() == 1) {
                logger.info("门店“" + shop.getShopName() + "”(" + shop.getId() + ")门店认证已通过！无需再次认证！");
                map.put("message", "该门店认证已通过！无需再次认证！");
                return map;
            }
            if (shop.getOperatingLicensePictureUrl() != null && shop.getOperatingLicensePictureUrl().length() > 1) {
                logger.info("门店“" + shop.getShopName() + "”(" + shop.getId() + ")已提交过认证申请！禁止重复认证！");
                map.put("error", "已提交过认证申请！禁止重复认证！");
                return map;
            }
            shop.setOperatingLicensePictureUrl(operatingLicensePictureUrl);
            shopService.edit(shop);
            logger.info("门店“" + shop.getShopName() + "”(" + shop.getId() + ")提交了门店认证申请！");
            map.put("message", "已提交认证申请！");
            return map;
        } catch (Exception e) {
            logger.info("提交门店认证申请失败！（后端发生某些错误）");
            map.put("error", "提交门店认证申请失败！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


    @ApiOperation(value = "根据门店id，取消门店的认证(未认证的将取消删除认证的申请)")
    @DeleteMapping("/shop/decertify")
    public Map decertify(@RequestParam String myOpenid, @RequestParam Integer shopId) {
        Map map = new HashMap();
        Shop shop = shopService.findShopById(shopId);
        try {
            if (shop == null) {
                logger.info("要取消认证的门店不存在！！");
                map.put("error", "该门店不存在！！");
                return map;
            }
            if (myOpenid.equals(shop.getOpenid()) || administratorService.isExist(myOpenid)) {

                if (shop.getOperatingLicensePictureUrl() == null || shop.getOperatingLicensePictureUrl().length() < 1) {
                    logger.info("门店“" + shop.getShopName() + "”(" + shop.getId() + ")还没有认证申请！（门店取消认证）");
                    map.put("message", "未提交过认证申请！");
                    return map;
                }
                shop.setOperatingLicensePictureUrl(null);
                shop.setApplyStatus(0);
                shopService.edit(shop);
                if (shop.getApplyStatus() == 1) {
                    logger.info("门店“" + shop.getShopName() + "”(" + shop.getId() + ")门店认证已通过！将取消认证！");
                    map.put("message", "取消认证成功！");
                } else {
                    map.put("message", "取消认证申请成功！");
                }
                logger.info("门店“" + shop.getShopName() + "”(" + shop.getId() + ")取消认证成功！");
                return map;
            } else {
                logger.info("门店“" + shop.getShopName() + "”(" + shop.getId() + ")取消认证申请失败！（无权限）");
                map.put("error", "取消认证失败！！（无权限）");
                return map;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("门店“" + shop.getShopName() + "”(" + shop.getId() + ")取消认证申请失败！（后端发生某些错误）");
            map.put("error", "操作失败！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


    @ApiOperation(value = "根据门店id，注销门店", notes = "权限：门店本人或管理员")
    @DeleteMapping("/shop")
    public Map deleteShop(@RequestParam String myOpenid, @RequestParam Integer shopId) {
        Map map = new HashMap();
        Shop shop = shopService.findShopById(shopId);
        try {
            if (shop == null) {
                logger.info("要注销的门店不存在！！");
                map.put("error", "该门店不存在！！");
                return map;
            }
            if (myOpenid.equals(shop.getOpenid()) || administratorService.isExist(myOpenid)) {
                shopService.delete(shopId);
                logger.info("注销门店“" + shop.getShopName() + "”(" + shopId + ")成功！");
                map.put("message", "注销门店“" + shop.getShopName() + "”成功！");
                return map;
            } else {
                logger.info("注销门店“" + shop.getShopName() + "”(" + shopId + ")失败！！（无权限）");
                map.put("error", "注销门店“" + shop.getShopName() + "”失败！！（无权限）");
                return map;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("注销门店失败！（后端发生某些错误）");
            map.put("error", "注销门店失败！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }

    }

    @ApiOperation(value = "根据当前登录的微信用户的openid，修改门店信息", notes = "权限：门店本人")
    @PutMapping("/shop")
    public Map updateShop(HttpServletRequest request, @RequestParam String myOpenid,
                          @RequestParam(value = "shopName", required = false) String shopName,
                          @RequestParam(value = "phone", required = false) String phone,
                          @RequestParam(value = "password", required = false) String password,
                          @RequestParam(value = "iconUrl", required = false) String iconUrl,
                          @RequestParam(value = "province", required = false) String province,
                          @RequestParam(value = "city", required = false) String city,
                          @RequestParam(value = "district", required = false) String district,
                          @RequestParam(value = "address", required = false) String address,
                          @RequestParam(value = "longitude", required = false) Double longitude,
                          @RequestParam(value = "latitude", required = false) Double latitude) {
        Map map = new HashMap();
        try {
            Shop shop = shopService.findShopByOpenid(myOpenid);
            if (shop == null) {
                logger.info("该门店不存在（修改信息）！！");
                logger.info("传入的数据：" + JSONObject.toJSON(request.getParameterMap()) + "\n");

                map.put("error", "未登录！！");
                return map;
            }

            if (myOpenid.equals(shop.getOpenid()) || administratorService.isExist(myOpenid)) {

                if (shopName != null)
                    shop.setShopName(shopName);
                if (phone != null) {
                    if (!MyUtils.isMobileNO(phone)) {
                        logger.info("电话号码（" + phone + "）不合法！！");
                        map.put("error", "电话号码（" + phone + "）不合法！！");
                        return map;
                    }
                    Shop shop1 = shopService.findShopByPhone(phone);
                    if (shop1.getId() != shop.getId()) {
                        logger.info("该电话号码已被注册！！（修改门店信息）");
                        map.put("error", "该电话号码已被注册!!");
                        return map;
                    }
                    shop.setPhone(phone);
                }
                if (password != null){
                    //将密码加密存储
                    String encryptedPassword;
                    try {
                        encryptedPassword = MyMD5Util.getEncryptedPwd(password);
                        shop.setPassword(encryptedPassword);
                    } catch (NoSuchAlgorithmException e) {
                        map.put("error", "加密算法在当前环境中不可用，修改密码失败！");
                        logger.info("加密算法在当前环境中不可用，修改密码失败！");
                        e.printStackTrace();
                        return map;
                    } catch (UnsupportedEncodingException e) {
                        map.put("error", "密码包含不支持的字符编码，修改密码失败！");
                        logger.info("密码包含不支持的字符编码！");
                        e.printStackTrace();
                        return map;
                    }
                }
                if (iconUrl != null)
                    shop.setLogoUrl(iconUrl);
                if (province != null)
                    shop.setProvince(province);
                if (city != null)
                    shop.setCity(city);
                if (district != null)
                    shop.setDistrict(district);
                if (address != null)
                    shop.setAddress(address);
                if (longitude != null)
                    shop.setLongitude(longitude);
                if (latitude != null)
                    shop.setLatitude(latitude);

                shopService.edit(shop);


                logger.info("门店 " + shop.getShopName() + "（id=" + shop.getId() + "）重新修改了基本信息");
                logger.info("传入的数据：" + JSONObject.toJSON(request.getParameterMap()) + "\n");
                map.put("message", "门店信息修改成功！");
                return map;
            } else {
                logger.info("门店信息修改失败！！（无权限）");
                logger.info("传入的数据：" + JSONObject.toJSON(request.getParameterMap()) + "\n");
                map.put("error", "门店信息修改失败！！（无权限）");
                return map;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("门店信息修改失败！！（后端发生某些错误）");
            map.put("error", "门店信息修改失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


    @ApiOperation(value = "根据门店临时登录微信的openid,获取单个门店信息", produces = "application/json")
    @GetMapping("/shop")
    public Map getOne(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Shop shop = shopService.findShopByOpenid(myOpenid);
            if (shop == null) {
                logger.info("还未登录(根据openid获取单个门店信息)");
                map.put("error", "请先登录！！");
                return map;
            }

            shop.regulateOrderSum();//根据所有订单进行数据校正
            shopService.edit(shop);//更新到数据库

            map.put("shop", shop);//总预约人数和今日预约人数已经在Shop的get方法里面，会直接被当做属性放进去
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取门店信息失败！！（后端发生某些错误）");
            map.put("error", "获取门店信息失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "根据门店id(不是openid),获取单个门店信息", produces = "application/json")
    @GetMapping("/getShopById")
    public Map getOneById(@RequestParam Integer shopId) {
        Map map = new HashMap();
        try {
            Shop shop = shopService.findShopById(shopId);
            if (shop == null) {
                logger.info("查看的门店(id=" + shopId + ")不存在！");
                map.put("error","该门店不存在！");
                return map;
            }

            map.put("shop", shop);//总预约人数和今日预约人数已经在Shop的get方法里面，会直接被当做属性放进去
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取门店信息失败！！（后端发生某些错误）");
            map.put("error", "获取门店信息失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "分页获取所有(审核通过的)门店列表", produces = "application/json")
    @GetMapping("/shop/getAll")
    public Map getShopsPage(@RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {
            Page<Shop> page = shopService.findAllByStatus(1, pageNum, pageSize);
            map.put("page", page);
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取门店列表信息失败！！（后端发生某些错误）");
            map.put("error", "获取门店列表信息失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "管理员按状态分页获取所有门店列表",notes = "权限：仅管理员",produces = "application/json")
    @GetMapping("/shop/getAllByStatus")
    public Map getAllByStatus(@RequestParam String myOpenid,
                              @RequestParam Integer status,
                              @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                              @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {
            if (!administratorService.isExist(myOpenid)) {
                map.put("error", "无权限！");
                return map;
            }

            Page<Shop> page = shopService.findAllByStatus(status, pageNum, pageSize);
            map.put("page", page);
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取门店列表信息失败！！（后端发生某些错误）");
            map.put("error", "获取门店列表信息失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "获取店内排行 - 全部")
    @GetMapping("/shop/getInStoreRanking/all")
    public Map getInStoreRankingAll(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Shop shop = shopService.findShopByOpenid(myOpenid);
            if (shop == null) {
                logger.info("未登录操作！！");
                map.put("error", "请先登录！！");
                return map;
            }
            if (shop.getApplyStatus() != 1) {
                logger.info("未认证的门店操作！！（获取店内排行 - 全部）");
                map.put("error", "请先进行门店认证！");
                return map;
            }
            List<Hairstylist> hairstylists = shop.getHairstylists();

            // 按完成订单数倒序排序
            Collections.sort(hairstylists, (o1, o2) -> {
                if (o1.getOrderSum() < o2.getOrderSum()) {
                    return 1;
                } else if ((o1.getOrderSum() > o2.getOrderSum())) {
                    return -1;
                }
                return 0; //相等为0
            });

            List<RankingData> resultList = new ArrayList<>();

            for (int i = 0; i < hairstylists.size(); i++) {
                Hairstylist h = hairstylists.get(i);
                RankingData data = new RankingData(i + 1, h, h.getOrderSum());
                resultList.add(data);
            }
            map.put("resultList", resultList);
            map.put("sumNum", resultList.size());
            return map;

        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取店内排行列表失败！！（后端发生某些错误）\n\n");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "获取店内排行 - 今天")
    @GetMapping("/shop/getInStoreRanking/today")
    public Map getInStoreRankingToday(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Shop shop = shopService.findShopByOpenid(myOpenid);
            if (shop == null) {
                logger.info("未登录操作！！");
                map.put("error", "请先登录！！");
                return map;
            }
            if (shop.getApplyStatus() != 1) {
                logger.info("未认证的门店操作！！（获取店内排行 - 今天）");
                map.put("error", "请先进行门店认证！");
                return map;
            }
            List<Hairstylist> hairstylists = shop.getHairstylists();
            // 按今日预约的订单数倒序排序
            Collections.sort(hairstylists, (o1, o2) -> {
                if (o1.getTodayOrderSum() < o2.getTodayOrderSum()) {
                    return 1;
                } else if ((o1.getTodayOrderSum() > o2.getTodayOrderSum())) {
                    return -1;
                }
                return 0; //相等为0
            });

            List<RankingData> resultList = new ArrayList<>();

            for (int i = 0; i < hairstylists.size(); i++) {
                Hairstylist h = hairstylists.get(i);
                RankingData data = new RankingData(i + 1, h, h.getTodayOrderSum());
                resultList.add(data);
            }
            map.put("resultList", resultList);
            map.put("sumNum", resultList.size());

            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取店内排行列表失败！！（后端发生某些错误）\n\n");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "获取店内排行 - 本月")
    @GetMapping("/shop/getInStoreRanking/month")
    public Map getInStoreRankingMonth(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Shop shop = shopService.findShopByOpenid(myOpenid);
            if (shop == null) {
                logger.info("未登录操作！！");
                map.put("error", "请先登录！！");
                return map;
            }
            if (shop.getApplyStatus() != 1) {
                logger.info("未认证的门店操作！！（获取店内排行 - 本月）");
                map.put("error", "请先进行门店认证！");
                return map;
            }
            List<Hairstylist> hairstylists = shop.getHairstylists();

            // 按完成订单数倒序排序
            Collections.sort(hairstylists, (o1, o2) -> {
                if (o1.getCurrentMonthOrderSum() < o2.getCurrentMonthOrderSum()) {
                    return 1;
                } else if ((o1.getCurrentMonthOrderSum() > o2.getCurrentMonthOrderSum())) {
                    return -1;
                }
                return 0; //相等为0
            });

            List<RankingData> resultList = new ArrayList<>();
            int myRankings = -1;//我的排名

            for (int i = 0; i < hairstylists.size(); i++) {
                Hairstylist h = hairstylists.get(i);
                RankingData data = new RankingData(i + 1, h, h.getCurrentMonthOrderSum());
                resultList.add(data);
            }
            map.put("resultList", resultList);
            map.put("sumNum", resultList.size());

            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取店内排行列表失败！！（后端发生某些错误）\n\n");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "获取区域排行 - 全部")
    @GetMapping("/shop/getRegionalRanking/all")
    public Map getRegionalRankingAll(@RequestParam String myOpenid,
                                     @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                     @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {
            Shop shop = shopService.findShopByOpenid(myOpenid);
            if (shop == null) {
                logger.info("未登录操作！！");
                map.put("error", "请先登录！！");
                return map;
            }
            if (shop.getApplyStatus() != 1) {
                logger.info("未认证的门店操作！！（获取区域排行 - 全部）");
                map.put("error", "请先进行门店认证！");
                return map;
            }
            Double radius = 0.05;//0.001经纬度相对大概100米
            List<Shop> shops = shopService.getShopsByRadius(shop.getLongitude(), shop.getLatitude(), radius);
            // 按完成订单数倒序排序
            Collections.sort(shops, (o1, o2) -> {
                if (o1.getOrderSum() < o2.getOrderSum()) {
                    return 1;
                } else if ((o1.getOrderSum() > o2.getOrderSum())) {
                    return -1;
                }
                return 0; //相等为0
            });

            List<RankingData> tempList = new ArrayList<>();
            List<RankingData> resultList = new ArrayList<>();
            int myRankings = -1;//我的排名

            for (int i = 0; i < shops.size(); i++) {
                Shop s = shops.get(i);
                RankingData data = new RankingData(i + 1, s, s.getOrderSum());
                tempList.add(data);
                if (data.getId() == shop.getId())
                    myRankings = i + 1;//获取我的排名
            }

            int first = pageNum * pageSize;
            int last = pageNum * pageSize + pageSize - 1;
            for (int i = first; i <= last && i < tempList.size(); i++) {
                resultList.add(tempList.get(i));
            }

            //包装分页数据
            Pageable pageable = PageRequest.of(pageNum, pageSize);
            Page<RankingData> page = new PageImpl<>(resultList, pageable, tempList.size());

            map.put("page", page);
            map.put("sumNum", shops.size());
            map.put("myRankings", myRankings);

            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取区域排行列表失败！！（后端发生某些错误）\n\n");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "获取区域排行 - 今天")
    @GetMapping("/shop/getRegionalRanking/today")
    public Map getRegionalRankingToday(@RequestParam String myOpenid,
                                       @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                       @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {
            Shop shop = shopService.findShopByOpenid(myOpenid);
            if (shop == null) {
                logger.info("未登录操作！！");
                map.put("error", "请先登录！！");
                return map;
            }
            if (shop.getApplyStatus() != 1) {
                logger.info("未认证的门店操作！！（获取区域排行 - 今天）");
                map.put("error", "请先进行门店认证！");
                return map;
            }

            Double radius = 0.05;//0.001经纬度相对大概100米
            List<Shop> shops = shopService.getShopsByRadius(shop.getLongitude(), shop.getLatitude(), radius);
            // 按今日预约的订单数倒序排序
            Collections.sort(shops, (o1, o2) -> {
                if (o1.getTodayOrderSum() < o2.getTodayOrderSum()) {
                    return 1;
                } else if ((o1.getTodayOrderSum() > o2.getTodayOrderSum())) {
                    return -1;
                }
                return 0; //相等为0
            });

            List<RankingData> tempList = new ArrayList<>();
            List<RankingData> resultList = new ArrayList<>();
            int myRankings = -1;//我的排名

            for (int i = 0; i < shops.size(); i++) {
                Shop s = shops.get(i);
                RankingData data = new RankingData(i + 1, s, s.getTodayOrderSum());
                tempList.add(data);
                if (data.getId() == shop.getId())
                    myRankings = i + 1;//获取我的排名
            }

            int first = pageNum * pageSize;
            int last = pageNum * pageSize + pageSize - 1;
            for (int i = first; i <= last && i < tempList.size(); i++) {
                resultList.add(tempList.get(i));
            }

            //包装分页数据
            Pageable pageable = PageRequest.of(pageNum, pageSize);
            Page<RankingData> page = new PageImpl<>(resultList, pageable, tempList.size());

            map.put("page", page);
            map.put("sumNum", shops.size());
            map.put("myRankings", myRankings);


            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取区域排行列表失败！！（后端发生某些错误）\n\n");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "获取区域排行 - 本月")
    @GetMapping("/shop/getRegionalRanking/month")
    public Map getRegionalRankingMonth(@RequestParam String myOpenid,
                                       @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                       @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {
            Shop shop = shopService.findShopByOpenid(myOpenid);
            if (shop == null) {
                logger.info("未登录操作！！");
                map.put("error", "请先登录！！");
                return map;
            }
            if (shop.getApplyStatus() != 1) {
                logger.info("未认证的门店操作！！（获取区域排行 - 本月）");
                map.put("error", "请先进行门店认证！");
                return map;
            }

            Double radius = 0.05;//0.001经纬度相对大概100米
            List<Shop> shops = shopService.getShopsByRadius(shop.getLongitude(), shop.getLatitude(), radius);
            // 按今日预约的订单数倒序排序
            Collections.sort(shops, (o1, o2) -> {
                if (o1.getCurrentMonthOrderSum() < o2.getCurrentMonthOrderSum()) {
                    return 1;
                } else if ((o1.getCurrentMonthOrderSum() > o2.getCurrentMonthOrderSum())) {
                    return -1;
                }
                return 0; //相等为0
            });

            List<RankingData> tempList = new ArrayList<>();
            List<RankingData> resultList = new ArrayList<>();
            int myRankings = -1;//我的排名

            for (int i = 0; i < shops.size(); i++) {
                Shop s = shops.get(i);
                RankingData data = new RankingData(i + 1, s, s.getCurrentMonthOrderSum());
                tempList.add(data);
                if (data.getId() == shop.getId())
                    myRankings = i + 1;//获取我的排名
            }

            int first = pageNum * pageSize;
            int last = pageNum * pageSize + pageSize - 1;
            for (int i = first; i <= last && i < tempList.size(); i++) {
                resultList.add(tempList.get(i));
            }

            //包装分页数据
            Pageable pageable = PageRequest.of(pageNum, pageSize);
            Page<RankingData> page = new PageImpl<>(resultList, pageable, tempList.size());

            map.put("page", page);
            map.put("sumNum", shops.size());
            map.put("myRankings", myRankings);

            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取区域排行列表失败！！（后端发生某些错误）\n\n");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


    @ApiOperation(value = "获取待审核或审核已通过的发型师信息列表(分页展示)（status=0表示“待审核”status=1表示“审核通过”，status=-1表示“审核未通过”", notes = "仅门店临时登录的微信openid有权限")
    @GetMapping("/shop/hairstylist/getHairstylistsByStatus")
    public Map getHairstylistsByStatus(@RequestParam String myOpenid,
                                       @RequestParam Integer status,
                                       @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                       @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {

            Shop shop = shopService.findShopByOpenid(myOpenid);
            if (shop == null) {
                logger.info("未登录操作（获取待审核或审核已通过的发型师）");
                map.put("error", "请先登录！！");
                return map;
            }

            List<Hairstylist> resultList = shop.getHairstylistsByStatus(status);

            Page<Hairstylist> page = MyUtils.getPage(resultList, pageNum, pageSize);

            map.put("page", page);
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取正在申请入驻的发型师列表失败！！（后端发生某些错误）");
            map.put("error", "获取正在申请入驻的发型师列表失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }

    }


    @ApiOperation(value = "同意或拒绝发型师的入驻申请（decide=1表示同意，decide=-1表示不同意）", notes = "仅门店临时登录的微信openid有权限")
    @PostMapping("/shop/hairstylist/approveOrReject")
    public Map approveOrReject(@RequestParam String myOpenid, int hairstylistId, int decide) {
        Map map = new HashMap();
        try {
            Shop shop = shopService.findShopByOpenid(myOpenid);
            if (shop == null) {
                logger.info("未登录操作（同意或拒绝发型师的入驻申请）");
                map.put("error", "请先登录！！");
                return map;
            }

            Hairstylist hairstylist = hairstylistService.findHairstylistById(hairstylistId);
            if (hairstylist == null) {
                map.put("error", "所操作的发型师不存在！！");
                logger.info("所操作的发型师不存在！！");
                return map;
            }
            if (hairstylist.getShop() == null || hairstylist.getShop().getId() != shop.getId()) {
                logger.info("该发型师没有入驻本店的申请！！");
                map.put("error", "该发型师没有入驻本店的申请！！");
                return map;
            }
//            if (hairstylist.getShop().getId() == shop.getId()) {
//                if (hairstylist.getApplyStatus() == 1) {
//                    logger.info("该发型师入驻申请已通过，无需重复审核");
//                    map.put("error", "该发型师入驻申请已通过，无需重复审核！");
//                    return map;
//                }
//                if (hairstylist.getApplyStatus() == -1) {
//                    logger.info("该发型师入驻申请已被拒绝，无需重复审核");
//                    map.put("error", "该发型师入驻申请已被拒绝，无需重复审核！");
//                    return map;
//                }
//            }


            switch (decide) {
                case 1:
                    if (hairstylist.getApplyStatus() == 1) {
                        logger.info("该发型师入驻申请已通过，无需重复审核");
                        map.put("message", "该发型师入驻申请已通过，无需重复审核！");
                        return map;
                    }

                    Date date = new Date(System.currentTimeMillis());
                    hairstylist.setSettledTime(date);//设置发型师入驻门店的时间
                    hairstylist.setApplyStatus(1);
                    hairstylist.setBusinessStatus(0);
                    hairstylistService.edit(hairstylist);
                    logger.info("门店“" + shop.getShopName() + "”(id=" + shop.getId() + ")同意id为" + hairstylist.getId() + "的发型师“" + hairstylist.getHairstylistName() + "”的入驻申请");
                    map.put("message", "同意该发型师的入驻申请，操作成功");
                    pushWxMsg.pushApplyResultMessage(hairstylistId);
                    break;
                case -1:
                    if (hairstylist.getApplyStatus() == -1) {
                        logger.info("该发型师入驻申请已被拒绝，无需重复审核");
                        map.put("message", "该发型师入驻申请已被拒绝，无需重复审核！");
                        return map;
                    }
                    hairstylist.setApplyStatus(-1);
                    hairstylist.setBusinessStatus(0);
                    hairstylistService.edit(hairstylist);
                    logger.info("门店“" + shop.getShopName() + "”(id=" + shop.getId() + ")拒绝id为" + hairstylist.getId() + "的发型师“" + hairstylist.getHairstylistName() + "”的入驻申请");
                    map.put("message", "拒绝该发型师的入驻申请，操作成功");
                    //...有时间再加一下拒绝理由模版消息
                    pushWxMsg.pushApplyResultMessage(hairstylistId);
                    break;
                default:
                    logger.info("同意或拒绝该发型师的入驻申请的decide(=" + decide + ")的值错误！！");
                    map.put("error", "decide的值错误（同意为1，拒绝为-1）！！");
                    break;

            }


        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("同意或拒绝门店入驻申请操作失败！！（后端发生某些错误）");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();

        }
        return map;
    }

    @ApiOperation(value = "根据发型师id，将发型师从本门店中移除", notes = "权限：门店当前登录的微信用户")
    @DeleteMapping("/shop/hairstylist/removeHairstylist")
    public Map removeHairstylist(@RequestParam String myOpenid, @RequestParam Integer hairstylistId) {
        Map map = new HashMap();
        try {
            Shop shop = shopService.findShopByOpenid(myOpenid);
            if (shop == null) {
                logger.info("未登录操作（将发型师从本门店中移除）");
                map.put("error", "请先登录！！");
                return map;
            }

            Hairstylist hairstylist = shop.isExistHairstylist(hairstylistId);
            if (hairstylist == null) {
                map.put("error", "门店内不存在该发型师！！");
                return map;
            }

            hairstylist.setShop(null);
            hairstylist.setSettledTime(null);
            hairstylist.setApplyStatus(0);
            hairstylist.setBusinessStatus(0);
            hairstylistService.edit(hairstylist);

            logger.info("门店“" + shop.getShopName() + "”(id=" + shop.getId() + ")移除了发型师“" + hairstylist.getHairstylistName() + "”(id=" + hairstylist.getId() + ")");
            map.put("message", "移除该发型师成功！");
            return map;

        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("注销发型师失败！（后端发生某些错误）");
            map.put("error", "注销发型师失败！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


    @ApiOperation(value = "获取门店场景图片url列表")
    @GetMapping("/shop/getImageUrlList")
    public Map getImageUrlList(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Shop shop = shopService.findShopByOpenid(myOpenid);
            if (shop == null) {
                logger.info("非门店登录用户操作！！（获取门店场景图片url列表）");
                map.put("error", "请先登录！");
                return map;
            }
            map.put("imageUrlList", shop.shopImageUrlList);
            return map;

        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取门店场景图片操作失败！！（后端发生某些错误）\n\n");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "添加门店场景图片url列表")
    @PostMapping("/shop/addImageUrlList")
    public Map addImageUrlList(@RequestParam String myOpenid,
                               @RequestParam(value = "imageList", required = false) List<String> imageList) {
        Map map = new HashMap();
        try {
            Shop shop = shopService.findShopByOpenid(myOpenid);
            if (shop == null) {
                logger.info("非门店登录用户操作！！（添加门店场景图片url列表）");
                map.put("error", "请先登录！");
                return map;
            } else {

                //下面保存该理发师的作品url
                if (imageList == null) {
                    map.put("error", "请选择图片！！");
                    return map;
                }

                int imageSize = imageList.size();
                for (int i = 0; i < imageSize; i++) {
                    if (shopImageUrlService.findByImgUrl(imageList.get(i)) != null) {
                        logger.info("存在相同的门店场景图片，本次图片不上传");
                        continue;
                    }
                    ShopImageUrl shopImageUrl = new ShopImageUrl();
                    shopImageUrl.setShop(shop);
                    shopImageUrl.setImageUrl(imageList.get(i));
                    shopImageUrlService.save(shopImageUrl);
                }
                logger.info("门店“" + shop.getShopName() + "”（id=" + shop.getId() + "）保存了" + imageSize + "(实际上还要减去重复个数)张门店场景图片： \n" + imageList);
                map.put("messgae", "保存成功！");

                return map;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("添加门店场景图片url列表操作失败！！（后端发生某些错误）");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "根据图片对应id,删除门店场景图片url")
    @DeleteMapping("/shop/deleteImageUrl")
    public Map deleteImageUrl(@RequestParam String myOpenid, @RequestParam Integer id) {
        Map map = new HashMap();
        try {
            Shop shop = shopService.findShopByOpenid(myOpenid);
            if (shop == null) {
                logger.info("非门店登录用户操作！！（删除门店场景图片url）");
                map.put("error", "请先登录！");
                return map;
            } else {
                ShopImageUrl imageUrl = shopImageUrlService.findShopImageUrlById(id);
                if (imageUrl == null) {
                    logger.info("要删除的图片不存在！");
                    map.put("error", "图片不存在！");
                    return map;
                }
                //判断该图片是不是该用户的
                if (myOpenid.equals(imageUrl.getShop().getOpenid())) {
                    shopImageUrlService.delete(id);
                    logger.info("门店“" + shop.getShopName() + "”（id=" + shop.getId() + "）删除了图片 " + imageUrl.getImageUrl());
                    map.put("message", "删除成功！");
                    return map;
                } else {
                    logger.info("删除门店场景图片url失败 - 没有权限");
                    map.put("error", "对不起，删除失败(没有权限)");
                    return map;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("删除门店场景图片url操作失败！！（后端发生某些错误）");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


    @ApiOperation(value = "获取门店数据")
    @GetMapping("/shop/getShopData")
    public Map getShopData(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Shop shop = shopService.findShopByOpenid(myOpenid);
            if (shop == null || shop.getApplyStatus() != 1) {
                logger.info("未登录操作！！(获取门店数据)");
                map.put("error", "请先登录！！");
                return map;
            }

            Map shopInfo = new HashMap();
            shopInfo.put("shopName", shop.getShopName());
            shopInfo.put("hairtylistNum", shop.hairstylists.size() + "人");
            shopInfo.put("settledTime", shop.getApplyTime());
            map.put("shopInfo", shopInfo);


            Map employeeData = new HashMap();
            employeeData.put("maxPointPerson", shop.getMaxPointPerson());
            employeeData.put("maxOrderPerson", shop.getMaxOrderPerson());
            employeeData.put("mostPopularPerson", shop.getMostPopularPerson());

            map.put("employeeData", employeeData);
            map.put("customerAnalyzeData", shop.getCustomerAnalyzeData());


            return map;

        } catch (Exception e) {
            logger.info("获取个人的顾客预约数情况列表失败！！（后端发生某些错误）\n\n");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }
}
