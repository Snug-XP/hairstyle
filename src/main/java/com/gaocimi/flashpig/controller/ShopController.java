package com.gaocimi.flashpig.controller;

import com.alibaba.fastjson.JSONObject;
import com.gaocimi.flashpig.entity.*;
import com.gaocimi.flashpig.model.RankingData;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.*;
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
    HairServiceService hairServiceService;
    @Autowired
    AdministratorService administratorService;










    @ApiOperation(value = "门店登录")
    @PostMapping("/shop/login")
    public Map shopLogin(@RequestParam String openid,@RequestParam String phone,@RequestParam String password){
        Map map = new HashMap();

        Shop shop = shopService.findShopByPhone(phone);
        if(shop==null){
            logger.info("账号（"+phone+"）不存在!");
            map.put("error","账号（"+phone+"）不存在!");
            return map;
        }
        if(!shop.getProvince().equals(password)){
            logger.info("登录密码错误!（phone："+phone+" ，wrongPassword:"+password+",rightpassword:"+shop.getPassword()+"）");
            map.put("error","密码错误！！");
            return map;
        }

        shop.setOpenid(openid);
        shopService.edit(shop);
        logger.info("门店“"+shop.getShopName()+"”（id="+shop.getId()+"）登录成功！");
        map.put("message","登陆成功!");
        return map;
    }


    @ApiOperation(value = "门店退出登录")
    @GetMapping("/shop/exit")
    public Map exit( @RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Shop shop = shopService.findShopByOpenid(myOpenid);
            if ( shop == null || shop.getApplyStatus() != 1) {
                map.put("message", "已退出！");
                return map;
            } else {
                shop.setOpenid(null);
                shopService.edit(shop);
                logger.info("门店“"+shop.getShopName()+"”（id="+shop.getId()+"）退出登录！");
                map.put("message", "已退出！");
                return map;
            }
        }catch (Exception e) {
            logger.info("获取个人的顾客预约数情况列表失败！！（后端发生某些错误）\n\n");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }







    @ApiOperation(value = "门店注册申请")
    @PostMapping("/shop/register/apply")
    public Map addShop(HttpServletRequest request, @RequestParam String openid,
                       @RequestParam(value = "shopName", required = false) String shopName,
                       @RequestParam(value = "phone", required = false) String phone,
                       @RequestParam(value = "password", required = false) String password,
                       @RequestParam(value = "shopPhotoUrl", required = false) String shopPhotoUrl,
                       @RequestParam(value = "operatingLicensePictureUrl", required = false) String operatingLicensePictureUrl,
                       @RequestParam(value = "province", required = false) String province,
                       @RequestParam(value = "city", required = false) String city,
                       @RequestParam(value = "district", required = false) String district,
                       @RequestParam(value = "address", required = false) String address,
                       @RequestParam(value = "longitude", required = false) Double longitude,
                       @RequestParam(value = "latitude", required = false) Double latitude) {
        Map map = new HashMap();


        if (shopPhotoUrl == null || phone == null || password == null || shopPhotoUrl == null || operatingLicensePictureUrl == null || shopName == null || province == null || city == null || district == null || address == null || longitude == null || latitude == null) {
            logger.info("请完整填写个人资料!(门店注册申请)");
            logger.info("传入的数据：" + JSONObject.toJSON(request.getParameterMap()) + "\n");
            map.put("message", "请完整填写个人资料！");
            return map;
        }

        Shop shop = shopService.findShopByOpenid(openid);
        try {
            if (shop != null) {
                //之前有申请过

                //之前申请失败了，删除记录
                if (shop.getApplyStatus() == -1) {
                    logger.info("删除" + shop.getShopName() + "之前申请失败的记录,并重新申请\n");
                    shopService.delete(shop.getId());
                } else {
                    logger.info(shop.getShopName() + "重复申请！！你的申请正在审核中或者已通过，无需重复提交！！");
                    map.put("message", "你的申请正在审核中或者已通过，无需重复提交！！");
                    return map;
                }
            }

            shop = new Shop();

            if(!MyUtils.isMobileNO(phone)){
                logger.info("电话号码（"+phone+"）不合法！！");
                map.put("error","电话号码（"+phone+"）不合法！！");
                return map;
            }

            shop.setOpenid(openid);
            shop.setShopName(shopName);
            shop.setPhone(phone);
            shop.setPassword(password);
            shop.setShopPhotoUrl(shopPhotoUrl);
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
        logger.info("id为" + shop.getId() + "的门店“" + shop.getShopName() + "”提交申请成功！");

        map.put("message", "门店提交申请成功！");
        return map;
    }

    @ApiOperation(value = "根据门店id，删除门店", notes = "权限：门店本人或管理员")
    @DeleteMapping("/shop")
    public Map deleteShop(@RequestParam String myOpenid, @RequestParam Integer shopId) {
        Map map = new HashMap();
        try {
            if (shopService.findShopById(shopId) == null) {
                logger.info("要删除的门店不存在！！");
                map.put("error", "该门店不存在！！");
                return map;
            }
            Shop shop = shopService.findShopById(shopId);
            if (myOpenid.equals(shop.getOpenid()) || administratorService.isExist(myOpenid)) {
                shopService.delete(shopId);
                logger.info("删除门店" + shop.getShopName() + "成功！");
                map.put("message", "删除门店" + shop.getShopName() + "成功！");
                return map;
            } else {
                logger.info("删除门店" + shop.getShopName() + "失败！！（没有权限！！）");
                map.put("error", "删除门店" + shop.getShopName() + "失败！！（没有权限！！）");
                return map;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("删除门店失败！（后端发生某些错误）");
            map.put("error", "删除门店失败！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }

    }

    @ApiOperation(value = "修改门店信息", notes = "权限：门店本人")
    @PutMapping("/shop")
    public Map updateShop(HttpServletRequest request, @RequestParam String myOpenid,
                          @RequestParam(value = "shopName", required = false) String shopName,
                          @RequestParam(value = "phone", required = false) String phone,
                          @RequestParam(value = "password", required = false) String password,
                          @RequestParam(value = "shopPhotoUrl", required = false) String shopPhotoUrl,
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

                map.put("error", "门店不存在！！");
                return map;
            }

            if (myOpenid.equals(shop.getOpenid()) || administratorService.isExist(myOpenid)) {

                if (shopName != null)
                    shop.setShopName(shopName);
                if (phone != null){
                    if(!MyUtils.isMobileNO(phone)){
                        logger.info("电话号码（"+phone+"）不合法！！");
                        map.put("error","电话号码（"+phone+"）不合法！！");
                        return map;
                    }
                    shop.setPhone(phone);
                }
                if (password != null)
                    shop.setPassword(password);
                if (shopPhotoUrl != null)
                    shop.setShopPhotoUrl(shopPhotoUrl);
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
                logger.info("门店信息修改失败！！（没有权限！！）");
                logger.info("传入的数据：" + JSONObject.toJSON(request.getParameterMap()) + "\n");
                map.put("error", "门店信息修改失败！！（没有权限！！）");
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


    @ApiOperation(value = "根据门店临时登录微信的openid,获取单个门店信息（包括总预约人数和今日预约人数）", produces = "application/json")
    @GetMapping("/shop")
    public Map getOne(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Shop shop = shopService.findShopByOpenid(myOpenid);
            if (shop == null) {
                logger.info("还未登录(获取单个门店信息)");
                map.put("error", "请先登录！！");
                return map;
            }
            map = getOneById(shop.getId());
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取门店信息失败！！（后端发生某些错误）");
            map.put("error", "获取门店信息失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "根据门店id(不是openid),获取单个门店信息（包括总预约人数和今日预约人数）", produces = "application/json")
    @GetMapping("/getShopById")
    public Map getOneById(@RequestParam Integer shopId) {
        Map map = new HashMap();
        try {
            Shop shop = shopService.findShopById(shopId);

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

    @ApiOperation(value = "分页获取所有门店列表", notes = "仅管理员有权限", produces = "application/json")
    @GetMapping("/shop/getAll")
    public Map getShopsPage(@RequestParam String myOpenid,
                            @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {
            if (administratorService.isExist(myOpenid)) {
                Page<Shop> page = shopService.findAll(pageNum, pageSize);
                map.put("page", page);
                logger.info("获取门店列表信息成功！");
                return map;
            } else {
                logger.info("获取门店信息失败！！（没有权限！！）");
                map.put("error", "获取门店信息失败！！（没有权限！！）");
                return map;
            }
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
            if (shop == null || shop.getApplyStatus() != 1) {
                logger.info("未登录操作！！");
                map.put("error", "对不起，你还未登录，无权操作！！");
                return map;
            } else {
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
            }
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
            if (shop == null || shop.getApplyStatus() != 1) {
                logger.info("未登录操作！！");
                map.put("error", "对不起，你还未登录，无权操作！！");
                return map;
            } else {
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
            }
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
            if (shop == null || shop.getApplyStatus() != 1) {
                logger.info("未登录操作！！");
                map.put("error", "对不起，你还未登录，无权操作！！");
                return map;
            } else {
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
            }
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
            if (shop == null || shop.getApplyStatus() != 1) {
                logger.info("未登录操作！！");
                map.put("error", "对不起，你还未登录，无权操作！！");
                return map;
            } else {
                Double radius = 0.001;//0.001经纬度相对大概100米
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
                map.put("sumNum", tempList.size());
                map.put("myRankings", myRankings);

                return map;
            }
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
            if (shop == null || shop.getApplyStatus() != 1) {
                logger.info("未登录操作！！");
                map.put("error", "对不起，你还未登录，无权操作！！");
                return map;
            } else {
                Double radius = 0.001;//0.001经纬度相对大概100米
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
                map.put("sumNum", tempList.size());
                map.put("myRankings", myRankings);


                return map;
            }
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
            if (shop == null || shop.getApplyStatus() != 1) {
                logger.info("未登录操作！！");
                map.put("error", "对不起，你还未登录，无权操作！！");
                return map;
            } else {
                Double radius = 0.001;//0.001经纬度相对大概100米
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
                map.put("sumNum", tempList.size());
                map.put("myRankings", myRankings);

                return map;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取区域排行列表失败！！（后端发生某些错误）\n\n");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


//    @ApiOperation(value = "获取店内排行")
//    @GetMapping("/shop/getInStoreRanking")
//    public Map getInStoreRanking( @RequestParam String myOpenid) {
//        Map map = new HashMap();
//        try {
//            Shop shop = shopService.findShopByOpenid(myOpenid);
//            if ( shop == null || shop.getApplyStatus() != 1) {
//                logger.info("未登录操作！！");
//                map.put("error", "对不起，你还未登录，无权操作！！");
//                return map;
//            } else {
//
//
//                return map;
//            }
//        }catch (Exception e) {
//            logger.error(e.getMessage());
//            logger.info("获取个人的顾客预约数情况列表失败！！（后端发生某些错误）\n\n");
//            map.put("error", "操作失败！！（后端发生某些错误）");
//            e.printStackTrace();
//            return map;
//        }
//    }
}
