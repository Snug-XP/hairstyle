package com.gaocimi.flashpig.controller;

import com.gaocimi.flashpig.entity.*;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.*;
import com.gaocimi.flashpig.utils.MyUtils;
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
@Api(value = "发型师服务", description = "发型师操作相关业务")
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
                              @RequestParam(value = "timeList", required = false) int[] timeList,
                              @RequestParam(value = "hairService", required = false) List<String> hairService,
                              @RequestParam(value = "description", required = false) List<String> description,
                              @RequestParam(value = "price", required = false) List<Double> price,
                              @RequestParam(value = "imageList", required = false) List<String> imageList) {
        Map map = new HashMap();

        try {
            if (hairstylistService.findHairstylistByOpenid(hairstylist.getOpenid()) != null) {
                //之前有申请过
                Hairstylist h = hairstylistService.findHairstylistByOpenid(hairstylist.getOpenid());
                //之前申请失败了，删除记录
                if (h.getApplyStatus() == -1) {
                    logger.info("删除" + h.getHairstylistName() + "之前申请失败的记录,并重新申请\n");
                    hairstylistService.delete(h.getId());
                } else {
                    logger.info(h.getHairstylistName() + "重复申请！！你的申请正在审核中或者已通过，无需重复提交！！");
                    map.put("message", "你的申请正在审核中或者已通过，无需重复提交！！");
                    return map;
                }
            }

            Date date = new Date(System.currentTimeMillis());

            hairstylist.setCreateTime(date);//设置注册时间
            hairstylist.setApplyStatus(0);//设置申请状态为申请中
            hairstylistService.save(hairstylist);
            hairstylist = hairstylistService.findHairstylistByOpenid(hairstylist.getOpenid());//重新从数据库获取刚进去的数据（主要为了取得id）
            setTime(hairstylist.getOpenid(), timeList);//跳转方法 设置可预约时间


            //下面保存该理发师的服务项目
            if (hairService != null) {
                int serviceSize = hairService.size();
                if (serviceSize != price.size()) {
                    logger.info("服务与价格不匹配！！");
                    map.put("error", "服务与价格不匹配！！");
                    return map;
                }
                if (serviceSize != description.size()) {
                    logger.info("服务与描述不匹配！！");
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
            logger.error(String.valueOf(e));
            e.printStackTrace();
            map.put("error", "发型师提交申请失败！请检查输入数据（也有可能后端发生错误）");
            return map;
        }
        map.put("message", "发型师提交申请成功！");
        return map;
    }

    @ApiOperation(value = "根据发型师id，删除发型师", notes = "权限：发型师本人或管理员")
    @DeleteMapping("/hairstylist")
    public Map deleteHairstylist(String myOpenid, Integer hairstylistId) {
        Map map = new HashMap();
        try {
            if (hairstylistService.findHairstylistById(hairstylistId) == null) {
                logger.info("要删除的发型师用户不存在！！");
                map.put("error", "该用户不存在！！");
                return map;
            }
            Hairstylist hairstylist = hairstylistService.findHairstylistById(hairstylistId);
            if (myOpenid.equals(hairstylist.getOpenid()) || administratorService.isExist(myOpenid)) {
                hairstylistService.delete(hairstylistId);
                logger.info("删除用户" + hairstylist.getHairstylistName() + "成功！");
                map.put("message", "删除用户" + hairstylist.getHairstylistName() + "成功！");
                return map;
            } else {
                logger.info("删除用户" + hairstylist.getHairstylistName() + "失败！！（没有权限！！）");
                map.put("error", "删除用户" + hairstylist.getHairstylistName() + "失败！！（没有权限！！）");
                return map;
            }
        } catch (Exception e) {
            logger.error(String.valueOf(e));
            logger.info("删除发型师失败！（后端发生某些错误，例如数据库连接失败）");
            map.put("error", "删除发型师失败！（后端发生某些错误，例如数据库连接失败）");
            e.printStackTrace();
            return map;
        }

    }

    @ApiOperation(value = "根据发型师id，修改发型师信息", notes = "权限：发型师本人或管理员")
    @PutMapping("/hairstylist")
    public Map updateHairstylist(String myOpenid, int hairstylistId,
                                 String hairstylistName, String personalPhotoUrl, String personalProfile, String shopName,
                                 String province, String city, String district, String address,
                                 @RequestParam(value = "longitude", required = false) Double longitude,
                                 @RequestParam(value = "latitude", required = false) Double latitude
                                 ) {
        Map map = new HashMap();
        try {
            if (hairstylistService.findHairstylistById(hairstylistId) == null) {
                logger.info("该发型师用户不存在！！");

                map.put("error", "该发型师用户不存在！！");
                return map;
            }
            Hairstylist hairstylist = hairstylistService.findHairstylistById(hairstylistId);
            if (myOpenid.equals(hairstylist.getOpenid()) || administratorService.isExist(myOpenid)) {

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

                logger.info("发型师用户 "+hairstylist.getHairstylistName()+"（"+myOpenid+"）重新修改了基本信息");
                map.put("message", "发型师信息修改成功！");
                return map;
            } else {
                logger.info("发型师信息修改失败！！（没有权限！！）");
                map.put("error", "发型师信息修改失败！！（没有权限！！）");
                return map;
            }
        } catch (Exception e) {
            logger.error(String.valueOf(e));
            logger.info("发型师信息修改失败！！（后端发生某些错误）");
            map.put("error", "发型师信息修改失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


    @ApiOperation(value = "获取单个发型师信息（包括总预约人数和今日预约人数）", notes = "权限：发型师本人或管理员", produces = "application/json")
    @GetMapping("/hairstylist")
    public Map getOne(String myOpenid, Integer hairstylistId) {
        Map map = new HashMap();
        try {
            if (hairstylistService.findHairstylistById(hairstylistId) == null) {
                logger.info("该发型师用户不存在！！");
                map.put("error", "该发型师用户不存在！！");
                return map;
            }
            Hairstylist hairstylist = hairstylistService.findHairstylistById(hairstylistId);
            if (myOpenid.equals(hairstylist.getOpenid()) || administratorService.isExist(myOpenid)) {
                List<HaircutOrder> haircutOrderList = hairstylist.getHaircutOrderList();
                int todayCount = 0;

                for (HaircutOrder haircutOrder : haircutOrderList) {
                    Long day = MyUtils.getDifferenceToday(haircutOrder.getBookTime());//取得预约时间与今天23点59分相差的天数
                    if (day == 0) {
                        todayCount++;
                    }
                }
                map.put("todayCount", todayCount);
                map.put("sumCount", haircutOrderList.size());
                map.put("hairstylist", hairstylist);
                return map;
            } else {
                logger.info("获取发型师信息失败！！（没有权限！！）");
                map.put("error", "获取发型师信息失败！！（没有权限！！）");
                return map;
            }
        } catch (Exception e) {
            logger.error(String.valueOf(e));
            logger.info("获取发型师信息失败！！（后端发生某些错误，例如数据库连接失败）");
            map.put("error", "获取发型师信息失败！！（后端发生某些错误，例如数据库连接失败）");
            e.printStackTrace();
            return map;
        }

    }

    @ApiOperation(value = "分页获取所有发型师列表", notes = "仅管理员有权限", produces = "application/json")
    @GetMapping("/hairstylists/all")
    public Map getairstylistsPage(String myOpenid,
                                  @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                  @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
    ) {
        Map map = new HashMap();
        try {
            if (administratorService.isExist(myOpenid)) {
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
            logger.error(String.valueOf(e));
            logger.info("获取发型师列表信息失败！！（后端发生某些错误，例如数据库连接失败）");
            map.put("error", "获取发型师列表信息失败！！（后端发生某些错误，例如数据库连接失败）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "发型师设置可预约时间", notes = "m1")
    @PostMapping("/hairstylist/setTime")
    public Map setTime(String myOpenid,
                       @RequestParam(value = "", required = false) int[] timeList) {
        Map map = new HashMap();
        try {
            if (hairstylistService.findHairstylistByOpenid(myOpenid) == null || hairstylistService.findHairstylistByOpenid(myOpenid).getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else {
                Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
                String str = "";
                for (int time : timeList) {
                    if (str.length() > 0)
                        str = str + "," + time;
                    else
                        str = "" + time;
                }
                hairstylist.setAvailableTime(str);
                hairstylistService.edit(hairstylist);
                logger.info("发型师用户 "+hairstylist.getHairstylistName()+"（"+myOpenid+"）重新设置了可预约时间");
                map.put("message", "可预约时间设置成功");
                return map;
            }
        } catch (Exception e) {
            logger.error(String.valueOf(e));
            logger.info("设置可预约时间操作失败！！（后端发生某些错误，例如数据库连接失败）");
            map.put("error", "设置可预约时间操作失败！！（后端发生某些错误，例如数据库连接失败）");
            e.printStackTrace();
            return map;
        }
    }




    @ApiOperation(value = "获取个人作品图片url列表", notes = "m1")
    @GetMapping("/hairstylist/getImageUrlList")
    public Map getImageUrlList(String myOpenid) {
        Map map = new HashMap();
        try {
            if (hairstylistService.findHairstylistByOpenid(myOpenid) == null || hairstylistService.findHairstylistByOpenid(myOpenid).getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else {
                Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
                map.put("imageUrlList", hairstylist.hairstylistImageUrlList);
                return map;
            }
        } catch (Exception e) {
            logger.error(String.valueOf(e));
            logger.info("获取个人作品图片操作失败！！（后端发生某些错误，例如数据库连接失败）\n\n");
            map.put("error", "操作失败！！（后端发生某些错误，例如数据库连接失败）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "添加个人作品图片url列表", notes = "m1")
    @PostMapping("/hairstylist/addImageUrlList")
    public Map addImageUrlList(String myOpenid, @RequestParam(value = "imageList", required = false) List<String> imageList) {
        Map map = new HashMap();
        try {
            if (hairstylistService.findHairstylistByOpenid(myOpenid) == null || hairstylistService.findHairstylistByOpenid(myOpenid).getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else {
                Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
                //下面保存该理发师的作品url
                if (imageList != null) {
                    int imageSize = imageList.size();
                    for (int i = 0; i < imageSize; i++) {
                        HairstylistImageUrl hairstylistImageUrl = new HairstylistImageUrl();
                        hairstylistImageUrl.setHairstylist(hairstylist);
                        hairstylistImageUrl.setImageUrl(imageList.get(i));
                        hairstylistImageUrlService.save(hairstylistImageUrl);
                    }
                    logger.info("发型师用户 "+hairstylist.getHairstylistName()+"（"+myOpenid+"）保存了"+imageSize+"张个人照片图片： " + imageList);
                    map.put("messgae", "保存成功！");
                } else {
                    map.put("error", "请选择图片！！");
                }
                return map;
            }
        } catch (Exception e) {
            logger.error(String.valueOf(e));
            logger.info("添加个人作品图片url列表操作失败！！（后端发生某些错误，例如数据库连接失败）");
            map.put("error", "操作失败！！（后端发生某些错误，例如数据库连接失败）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "根据图片对应id,删除个人作品图片url", notes = "m1")
    @DeleteMapping("/hairstylist/deleteImageUrlList")
    public Map deleteImageUrlList(String myOpenid, int id) {
        Map map = new HashMap();
        try {
            if (hairstylistService.findHairstylistByOpenid(myOpenid) == null || hairstylistService.findHairstylistByOpenid(myOpenid).getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "非发型师用户操作！！");
                return map;
            } else {
                HairstylistImageUrl imageUrl = hairstylistImageUrlService.findHairstylistImageUrlById(id);
                if (imageUrl == null) {
                    logger.info("要删除的图片不存在！");
                    map.put("error", "图片不存在！");
                    return map;
                }
                //判断该图片是不是该用户的
                if (myOpenid.equals(imageUrl.getHairstylist().getOpenid())) {
                    hairstylistImageUrlService.delete(id);
                    logger.info("发型师用户 "+imageUrl.getHairstylist().getHairstylistName()+"（"+myOpenid+"）删除图片 \""+imageUrl.getImageUrl()+"\"");
                    map.put("message", "删除成功！");
                    return map;
                } else {
                    logger.info("删除个人作品图片url失败 - 没有权限");
                    map.put("error", "对不起，删除失败(没有权限)");
                    return map;
                }
            }
        } catch (Exception e) {
            logger.error(String.valueOf(e));
            logger.info("删除个人作品图片url操作失败！！（后端发生某些错误，例如数据库连接失败）");
            map.put("error", "操作失败！！（后端发生某些错误，例如数据库连接失败）");
            e.printStackTrace();
            return map;
        }
    }


//    @ApiOperation(value = "获取某个顾客关于自己的预约记录", notes = "m1")
//    @GetMapping("/hairstylist/getOrderListFromOneUser/{myOpenid}")
//    public Map getOrderListFromOneUser( String openid,int user_id) {
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
//            logger.error(String.valueOf(e));
//            e.printStackTrace();
//            map.put("error", "操作失败！！（后端发生某些错误，例如数据库连接失败）");
//            return map;
//        }
//    }
//
//
//    @ApiOperation(value = "获取某个顾客关于自己的预约记录", notes = "m1")
//    @GetMapping("/hairstylist/getOrderListFromOneUser/{myOpenid}")
//    public Map getOrderListFromOneUser( String openid,int user_id) {
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
//            logger.error(String.valueOf(e));
//            e.printStackTrace();
//            map.put("error", "操作失败！！（后端发生某些错误，例如数据库连接失败）");
//            return map;
//        }
//    }

}
