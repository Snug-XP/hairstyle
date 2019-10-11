package com.gaocimi.flashpig.controller;

import com.gaocimi.flashpig.entity.*;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author liyutg
 * @date 2019/6/12 2:14
 * @description
 */
@RestController
@ResponseResult
@Api(value = "管理端发型师服务", description = "管理员操作发型师相关业务")
public class HairstylistController {
    protected static final Logger logger = LoggerFactory.getLogger(HairstylistController.class);


    @Autowired
    HairstylistService hairstylistService;

    @Autowired
    HairServiceService hairServiceService;

    @Autowired
    HairstylistImageUrlService hairstylistImageUrlService;

    @Autowired
    AdministratorService administratorService;

    @Autowired
    RecordHairstylisToUserService recordHairstylisToUserService;

    @ApiOperation(value = "发型师注册申请", notes = "m1")
    @PostMapping("/hairstylist/register/apply")
    public Map addHairstylist(@Validated Hairstylist hairstylist,
                              @RequestParam(value = "", required = false) int[] timeList,
                              @RequestParam(value = "", required = false) List<String> hairService,
                              @RequestParam(value = "", required = false) List<String> description,
                              @RequestParam(value = "", required = false) List<Double> price,
                              @RequestParam(value = "", required = false) List<String> imageList) {
        Map map = new HashMap();

        try {
            //之前
            if (hairstylistService.findHairstylistByOpenid(hairstylist.getOpenid()) != null) {
                //之前有申请过
                Hairstylist h = hairstylistService.findHairstylistByOpenid(hairstylist.getOpenid());
                //之前申请失败了，删除记录
                if (h.getApplyState() == -1) {
                    logger.info("删除" + h.getHairstylistName() + "之前申请失败的记录,并重新申请");
                    hairstylistService.delete(h.getId());
                } else {
                    logger.info(h.getHairstylistName() + "重复申请！！你的申请正在审核中或者已通过，无需重复提交！！");
                    map.put("message", "你的申请正在审核中或者已通过，无需重复提交！！");
                    return map;
                }
            }

            Date date = new Date(System.currentTimeMillis());

            hairstylist.setCreateTime(date);//设置注册时间
            hairstylist.setApplyState(0);//设置申请状态为申请中
            hairstylistService.save(hairstylist);
            hairstylist = hairstylistService.findHairstylistByOpenid(hairstylist.getOpenid());//重新从数据库获取刚进去的数据（主要为了取得id）
            setTime(hairstylist.getOpenid(), timeList);//设置可预约时间


            //下面保存该理发师的服务项目
            if (hairService != null) {
                int serviceSize = hairService.size();
                if (serviceSize != price.size()) {
                    logger.info("服务与价格不匹配！！");
                    map.put("error", "服务与价格不匹配！！");
                    return map;
                }
                for (int i = 0; i < serviceSize; i++) {
                    HairService hairService1 = new HairService();
                    hairService1.setHairstylist(hairstylist);
                    hairService1.setServiceName(hairService.get(i));
//                    hairService1.setDescription(description.get(i));//暂时前端还没加
                    hairService1.setPrice(price.get(i));
                    hairServiceService.save(hairService1);
                }
                logger.info("hairService有" + serviceSize + "个： " + hairService);
                logger.info("price有" + price.size() + "个： " + hairService);
            }

            //下面保存该理发师的作品url
            if (imageList != null) {
                int imageSize = imageList.size();
                for (int i = 0; i < imageSize; i++) {
                    HairstylistImageUrl hairstylistImageUrl = new HairstylistImageUrl();
                    hairstylistImageUrl.setHairstylist(hairstylist);
                    hairstylistImageUrl.setImageUrl(imageList.get(i));
                    hairstylistImageUrlService.save(hairstylistImageUrl);
                }
                logger.info("imageList有" + imageSize + "个： " + imageList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("error", "发型师提交申请失败！请检查输入数据（也有可能后端发生错误）");
            return map;
        }
        map.put("message", "发型师提交申请成功！");
        return map;
    }

    @ApiOperation(value = "根据发型师id，删除发型师", notes = "m1")
    @DeleteMapping("/hairstylist/{myOpenid}/{hairstylistId}")
    public Map deleteHairstylist(@PathVariable("myOpenid") String openid, @PathVariable("hairstylistId") Integer hairstylistId) {
        Map map = new HashMap();
        try {
            if (hairstylistService.findHairstylistById(hairstylistId) == null) {
                logger.info("该用户不存在！！");
                map.put("error", "该用户不存在！！");
                return map;
            }
            Hairstylist hairstylist = hairstylistService.findHairstylistById(hairstylistId);
            if (openid.equals(hairstylist.getOpenid()) || administratorService.isExist(openid)) {
                hairstylistService.delete(hairstylistId);
                logger.info("删除用户" + hairstylist.getHairstylistName() + "成功！！");
                map.put("message", "删除用户" + hairstylist.getHairstylistName() + "成功！！");
                return map;
            } else {
                logger.info("删除用户" + hairstylist.getHairstylistName() + "失败！！（没有权限！！）");
                map.put("error", "删除用户" + hairstylist.getHairstylistName() + "失败！！（没有权限！！）");
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("error", "删除发型师失败！（后端发生某些错误，例如数据库连接失败）");
            return map;
        }

    }

    @ApiOperation(value = "根据发型师id，修改发型师信息", notes = "m1")
    @PutMapping("/hairstylist/{myOpenid}")
    public Map updateHairstylist(@PathVariable("myOpenid") String openid, int hairstylistId,
                                 String hairstylistName, String personalPhotoUrl, String personalProfile, String shopName,
                                 String province, String city, String district, String address,
                                 @RequestParam(value = "longitude", required = false) Double longitude,
                                 @RequestParam(value = "latitude", required = false) Double latitude,
                                 @RequestParam(value = "", required = false) List<String> hairService,
                                 @RequestParam(value = "", required = false) List<String> description,
                                 @RequestParam(value = "", required = false) List<Double> price) {
        Map map = new HashMap();
        try {
            if (hairstylistService.findHairstylistById(hairstylistId) == null) {
                logger.info("该发型师用户不存在！！");

                map.put("error", "该发型师用户不存在！！");
                return map;
            }
            Hairstylist hairstylist = hairstylistService.findHairstylistById(hairstylistId);
            if (openid.equals(hairstylist.getOpenid()) || administratorService.isExist(openid)) {

                hairstylist.setHairstylistName(hairstylistName);
                hairstylist.setPersonalPhotoUrl(personalPhotoUrl);
                hairstylist.setPersonalProfile(personalProfile);
                hairstylist.setShopName(shopName);
                hairstylist.setProvince(province);
                hairstylist.setCity(city);
                hairstylist.setDistrict(district);
                hairstylist.setAddress(address);
                if (longitude != null && latitude != null) {
                    hairstylist.setLongitude(longitude);
                    hairstylist.setLatitude(latitude);
                }
                hairstylistService.edit(hairstylist);

                //下面保存该理发师的服务项目
                //先删除原来对应的的服务项目
                if (hairService != null) {
                    hairServiceService.deleteAllByHairstylistId(hairstylistId);
                    int serviceSize = hairService.size();
                    if (serviceSize != price.size()) {
                        logger.info("服务与价格不匹配！！");
                        map.put("error", "服务与价格不匹配！！");
                        return map;
                    }
                    for (int i = 0; i < serviceSize; i++) {
                        HairService hairService1 = new HairService();
                        hairService1.setHairstylist(hairstylist);
                        hairService1.setServiceName(hairService.get(i));
//                        hairService1.setDescription(description.get(i));
                        hairService1.setPrice(price.get(i));
                        hairServiceService.save(hairService1);
                    }
                }

                logger.info("发型师信息修改成功！");
                map.put("message", "发型师信息修改成功！");
                return map;
            } else {
                logger.info("发型师信息修改失败！！（没有权限！！）");
                map.put("error", "发型师信息修改失败！！（没有权限！！）");
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("error", "发型师信息修改失败！！（后端发生某些错误）");
            return map;
        }
    }


    @ApiOperation(value = "获取单个发型师信息", notes = "m1", produces = "application/json")
    @GetMapping("/hairstylist/{myOpenid}/{hairstylistId}")
    public Map getOne(@PathVariable("myOpenid") String openid, @PathVariable("hairstylistId") Integer hairstylistId) {
        Map map = new HashMap();
        try {
            if (hairstylistService.findHairstylistById(hairstylistId) == null) {
                logger.info("该发型师用户不存在！！");
                map.put("error", "该发型师用户不存在！！");
                return map;
            }
            Hairstylist hairstylist = hairstylistService.findHairstylistById(hairstylistId);
            if (openid.equals(hairstylist.getOpenid()) || administratorService.isExist(openid)) {
                map.put("hairstylist", hairstylist);
                return map;
            } else {
                logger.info("获取发型师信息失败！！（没有权限！！）");
                map.put("error", "获取发型师信息失败！！（没有权限！！）");
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("error", "获取发型师信息失败！！（后端发生某些错误，例如数据库连接失败）");
            return map;
        }

    }

    @ApiOperation(value = "获取所有发型师列表", notes = "m1", produces = "application/json")
    @GetMapping("/hairstylists/all/{myOpenid}")
    public Map getPage(@PathVariable("myOpenid") String openid,
                       @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                       @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
    ) {
        Map map = new HashMap();
        try {
            if (administratorService.isExist(openid)) {
                Page<Hairstylist> page = hairstylistService.findAll(pageNum, pageSize);
                map.put("page", page);
                logger.info("获取发型师列表信息成功！");
                return map;
            } else {
                logger.info("获取发型师信息失败！！（没有权限！！）");
                map.put("error", "获取发型师信息失败！！（没有权限！！）");
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("error", "获取发型师列表信息失败！！（后端发生某些错误，例如数据库连接失败）");
            return map;
        }
    }

    @ApiOperation(value = "发型师设置可预约时间", notes = "m1")
    @PostMapping("/hairstylist/setTime/{myOpenid}")
    public Map setTime(@PathVariable("myOpenid") String openid,
                       @RequestParam(value = "", required = false) int[] timeList) {
        Map map = new HashMap();
        try {
            if (hairstylistService.findHairstylistByOpenid(openid) == null || hairstylistService.findHairstylistByOpenid(openid).getApplyState()!=1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else {
                Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(openid);
                String str = "";
                for (int time : timeList) {
                    if (str.length() > 0)
                        str = str + "," + time;
                    else
                        str = "" + time;
                }
                hairstylist.setAvailableTime(str);
                hairstylistService.edit(hairstylist);
                logger.info("可预约时间设置成功");
                map.put("message", "可预约时间设置成功");
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("error", "操作失败！！（后端发生某些错误，例如数据库连接失败）");
            return map;
        }
    }


    @ApiOperation(value = "获取一个时间段的预约列表", notes = "days的值表示获取几天前到现在的数据，days默认为0，表示只获取今天的订单,days=1表示获取昨天和今天的所有订单,days=-1表示获取今天之后的所有订单")
    @GetMapping("/hairstylist/getOrderList/{myOpenid}")
    public Map getOrderList(@PathVariable("myOpenid") String openid,
                            @RequestParam(defaultValue = "0", required = true) int days) {
        Map map = new HashMap();
        try {
            if (hairstylistService.findHairstylistByOpenid(openid) == null || hairstylistService.findHairstylistByOpenid(openid).getApplyState()!=1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else {
                Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(openid);
                List<HaircutOrder> tempOrderList = hairstylist.haircutOrderList;
                List<HaircutOrder> resultOrderList = new ArrayList<HaircutOrder>();
                Date orderBookTime;

                //获取今天23点59分59秒的时间
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                Date todayLastTime = calendar.getTime();

                for (HaircutOrder order : tempOrderList) {
                    orderBookTime = order.getBookTime();
                    long day = todayLastTime.getTime() / 86400000 - orderBookTime.getTime() / 86400000;//与今天23点59分相差天数
//                    System.out.println("订单id="+order.getId()+"的预约时间为"+orderBookTime+"，  与今天相差"+day+"天");
                    if (days == -1) {
                        if (day < 0) resultOrderList.add(order);
                    } else if (0 <= day && day <= days) {
                        resultOrderList.add(order);
                    }
                }
                if (resultOrderList.size() > 0)
                    map.put("orderList", resultOrderList);
                else
                    map.put("message", "没有匹配的订单");

                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("error", "操作失败！！（后端发生某些错误，例如数据库连接失败）");
            return map;
        }
    }

    @ApiOperation(value = "获取某个顾客关于自己的预约列表", notes = "返回的是预约单列表，请从预约单中选取你要的属性就好")
    @GetMapping("/hairstylist/getOrderListFromOneUser/{myOpenid}")
    public Map getOrderListFromOneUser(@PathVariable("myOpenid") String openid,int user_id) {
        Map map = new HashMap();
        try {
            if (hairstylistService.findHairstylistByOpenid(openid) == null || hairstylistService.findHairstylistByOpenid(openid).getApplyState()!=1 ) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else {
                Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(openid);
                List<HaircutOrder> tempOrderList = hairstylist.haircutOrderList;
                List<HaircutOrder> resultOrderList = new ArrayList<HaircutOrder>();
                for (HaircutOrder order : tempOrderList) {
                    if(order.user.getId() == user_id){
                        resultOrderList.add(order);
                    }
                }
                if (resultOrderList.size() > 0)
                    map.put("orderList", resultOrderList);
                else
                    map.put("message", "没有匹配的订单");
                return map;
            }
        }catch (Exception e) {
            e.printStackTrace();
            map.put("error", "操作失败！！（后端发生某些错误，例如数据库连接失败）");
            return map;
        }
    }

    @ApiOperation(value = "获取个人作品图片url列表", notes = "m1")
    @GetMapping("/hairstylist/getImageUrlList/{myOpenid}")
    public Map getImageUrlList(@PathVariable("myOpenid") String openid) {
        Map map = new HashMap();
        try {
            if (hairstylistService.findHairstylistByOpenid(openid) == null || hairstylistService.findHairstylistByOpenid(openid).getApplyState()!=1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else {
                Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(openid);
                map.put("imageUrlList", hairstylist.hairstylistImageUrlList);
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("error", "操作失败！！（后端发生某些错误，例如数据库连接失败）");
            return map;
        }
    }

    @ApiOperation(value = "添加个人作品图片url列表", notes = "m1")
    @PostMapping("/hairstylist/addImageUrlList/{myOpenid}")
    public Map addImageUrlList(@PathVariable("myOpenid") String openid, @RequestParam(value = "", required = false) List<String> imageList) {
        Map map = new HashMap();
        try {
            if (hairstylistService.findHairstylistByOpenid(openid) == null || hairstylistService.findHairstylistByOpenid(openid).getApplyState()!=1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else {
                Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(openid);
                //下面保存该理发师的作品url
                if (imageList != null) {
                    int imageSize = imageList.size();
                    for (int i = 0; i < imageSize; i++) {
                        HairstylistImageUrl hairstylistImageUrl = new HairstylistImageUrl();
                        hairstylistImageUrl.setHairstylist(hairstylist);
                        hairstylistImageUrl.setImageUrl(imageList.get(i));
                        hairstylistImageUrlService.save(hairstylistImageUrl);
                        map.put("error", "保存成功！");
                    }
                    logger.info("imageList有" + imageSize + "个： " + imageList);
                } else {
                    map.put("error", "请选择图片！！");
                }
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("error", "操作失败！！（后端发生某些错误，例如数据库连接失败）");
            return map;
        }
    }

    @ApiOperation(value = "根据id,删除个人作品图片url列表", notes = "m1")
    @DeleteMapping("/hairstylist/deleteImageUrlList/{myOpenid}")
    public Map deleteImageUrlList(@PathVariable("myOpenid") String openid, int id) {
        Map map = new HashMap();
        try {
            if (hairstylistService.findHairstylistByOpenid(openid) == null || hairstylistService.findHairstylistByOpenid(openid).getApplyState()!=1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "非发型师用户操作！！");
                return map;
            } else {
                HairstylistImageUrl imageUrl = hairstylistImageUrlService.findHairstylistImageUrlById(id);
                if(imageUrl==null){
                    logger.info("图片不存在！");
                    map.put("error","图片不存在！");
                    return map;
                }
                //判断该图片是不是该用户的
                if(openid.equals(imageUrl.getHairstylist().getOpenid())) {
                    hairstylistImageUrlService.delete(id);
                    map.put("message", "删除成功！");
                    return map;
                }else{
                    logger.info("没有权限");
                    map.put("error","对不起，删除失败(没有权限)");
                    return map;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("error", "操作失败！！（后端发生某些错误，例如数据库连接失败）");
            return map;
        }
    }


    @ApiOperation(value = "获取个人的服务列表", notes = "m1")
    @GetMapping("/hairstylist/getServiceList/{myOpenid}")
    public Map getServiceList(@PathVariable("myOpenid") String openid) {
        Map map = new HashMap();
        try {
            if (hairstylistService.findHairstylistByOpenid(openid) == null || hairstylistService.findHairstylistByOpenid(openid).getApplyState()!=1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else {
                Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(openid);
                map.put("serviceList", hairstylist.hairServiceList);
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
            map.put("error", "操作失败！！（后端发生某些错误，例如数据库连接失败）");
            return map;
        }
    }



//
//    @ApiOperation(value = "获取某个顾客关于自己的预约记录", notes = "m1")
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
//
//
//                return map;
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//            map.put("error", "操作失败！！（后端发生某些错误，例如数据库连接失败）");
//            return map;
//        }
//    }
//
//
//    @ApiOperation(value = "获取某个顾客关于自己的预约记录", notes = "m1")
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
//
//
//                return map;
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//            map.put("error", "操作失败！！（后端发生某些错误，例如数据库连接失败）");
//            return map;
//        }
//    }

}
