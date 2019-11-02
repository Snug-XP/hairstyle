package com.gaocimi.flashpig.controller;

import com.alibaba.fastjson.JSONObject;
import com.gaocimi.flashpig.entity.*;
import com.gaocimi.flashpig.model.CountUser;
import com.gaocimi.flashpig.model.RankingData;
import com.gaocimi.flashpig.model.UserReservation;
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
import org.springframework.http.HttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author xp
 * @date 2019-09-16 17:53:16
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
    @Autowired
    UserService userService;

    @ApiOperation(value = "发型师注册申请")
    @PostMapping("/hairstylist/register/apply")
    public Map addHairstylist(HttpServletRequest request,@RequestParam String openid,
                              @RequestParam(value = "hairstylistName", required = false) String hairstylistName,
                              @RequestParam(value = "personalPhotoUrl", required = false) String personalPhotoUrl,
                              @RequestParam(value = "personalPhone", required = false) String personalPhone,
                              @RequestParam(value = "personalProfile", required = false) String personalProfile,
                              @RequestParam(value = "shopName", required = false) String shopName,
                              @RequestParam(value = "province", required = false) String province,
                              @RequestParam(value = "city", required = false) String city,
                              @RequestParam(value = "district", required = false) String district,
                              @RequestParam(value = "address", required = false) String address,
                              @RequestParam(value = "attention", required = false) String attention,
                              @RequestParam(value = "longitude", required = false) Double longitude,
                              @RequestParam(value = "latitude", required = false) Double latitude,
                              @RequestParam(value = "timeList", required = false) List<String> timeList,
                              @RequestParam(value = "hairService", required = false) List<String> hairService,
                              @RequestParam(value = "description", required = false) List<String> description,
                              @RequestParam(value = "price", required = false) List<Double> price,
                              @RequestParam(value = "imageList", required = false) List<String> imageList) {
        Map map = new HashMap();


        if (hairstylistName ==null||personalPhone==null||personalPhotoUrl ==null||personalProfile ==null||shopName ==null||province ==null||city ==null||district ==null||address ==null||longitude ==null||latitude ==null)
        {
            logger.info("请完整填写个人资料!(发型师注册申请)");
            logger.info("传入的数据："+JSONObject.toJSON(request.getParameterMap())+"\n");
            map.put("message","请完整填写个人资料！");
            return map;
        }

        Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(openid);
        try {
            if (hairstylist != null) {
                //之前有申请过

                //之前申请失败了，删除记录
                if (hairstylist.getApplyStatus() == -1) {
                    logger.info("删除" + hairstylist.getHairstylistName() + "之前申请失败的记录,并重新申请\n");
                    hairstylistService.delete(hairstylist.getId());
                } else {
                    logger.info(hairstylist.getHairstylistName() + "重复申请！！你的申请正在审核中或者已通过，无需重复提交！！");
                    map.put("message", "你的申请正在审核中或者已通过，无需重复提交！！");
                    return map;
                }
            }

            hairstylist = new Hairstylist();

            hairstylist.setOpenid(openid);
            hairstylist.setHairstylistName(hairstylistName);
            hairstylist.setPersonalPhotoUrl(personalPhotoUrl);
            hairstylist.setPersonalPhone(personalPhone);
            hairstylist.setShopName(shopName);
            hairstylist.setProvince(province);
            hairstylist.setCity(city);
            hairstylist.setDistrict(district);
            hairstylist.setAddress(address);
            hairstylist.setLongitude(longitude);
            hairstylist.setLatitude(latitude);

            hairstylist.setAttention(attention);


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
                    if(hairstylistImageUrlService.findByImgUrl(imageList.get(i))!=null){
                        logger.info("存在相同的个人作品图片，本次图片不上传");
                        continue;
                    }
                    HairstylistImageUrl hairstylistImageUrl = new HairstylistImageUrl();
                    hairstylistImageUrl.setHairstylist(hairstylist);
                    hairstylistImageUrl.setImageUrl(imageList.get(i));
                    hairstylistImageUrlService.save(hairstylistImageUrl);
                }
                logger.info("imageList有" + imageSize + "(实际上还要减去重复个数)个： " + imageList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("传入的数据："+JSONObject.toJSON(request.getParameterMap())+"\n");
            map.put("error", "发型师提交申请失败！请检查输入数据（也有可能后端发生错误）");
            return map;
        }
        logger.info("传入的数据："+JSONObject.toJSON(request.getParameterMap())+"\n");
        logger.info("id为" + hairstylist.getId() + "的发型师“" + hairstylist.getHairstylistName() + "”提交申请成功！");

        map.put("message", "发型师提交申请成功！");
        return map;
    }

    @ApiOperation(value = "根据发型师id，删除发型师", notes = "权限：发型师本人或管理员")
    @DeleteMapping("/hairstylist")
    public Map deleteHairstylist(@RequestParam String myOpenid,@RequestParam Integer hairstylistId) {
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
            logger.error(e.getMessage());
            logger.info("删除发型师失败！（后端发生某些错误）");
            map.put("error", "删除发型师失败！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }

    }

    @ApiOperation(value = "发型师修改个人信息", notes = "权限：发型师本人")
    @PutMapping("/hairstylist")
    public Map updateHairstylist(HttpServletRequest request,@RequestParam String myOpenid,
                                 @RequestParam(value = "hairstylistName", required = false) String hairstylistName,
                                 @RequestParam(value = "personalPhotoUrl", required = false) String personalPhotoUrl,
                                 @RequestParam(value = "personalProfile", required = false) String personalProfile,
                                 @RequestParam(value = "shopName", required = false) String shopName,
                                 @RequestParam(value = "province", required = false) String province,
                                 @RequestParam(value = "city", required = false) String city,
                                 @RequestParam(value = "district", required = false) String district,
                                 @RequestParam(value = "address", required = false) String address,
                                 @RequestParam(value = "attention", required = false) String attention,
                                 @RequestParam(value = "longitude", required = false) Double longitude,
                                 @RequestParam(value = "latitude", required = false) Double latitude) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null) {
                logger.info("该发型师用户不存在（修改信息）！！");
                logger.info("传入的数据："+JSONObject.toJSON(request.getParameterMap())+"\n");

                map.put("error", "发型师用户不存在！！");
                return map;
            }

            if (myOpenid.equals(hairstylist.getOpenid()) || administratorService.isExist(myOpenid)) {

                if (hairstylistName != null)
                    hairstylist.setHairstylistName(hairstylistName);
                if (personalPhotoUrl != null)
                    hairstylist.setPersonalPhotoUrl(personalPhotoUrl);
                if (personalProfile != null)
                    hairstylist.setPersonalProfile(personalProfile);
                if (shopName != null)
                    hairstylist.setShopName(shopName);
                if (province != null)
                    hairstylist.setProvince(province);
                if (city != null)
                    hairstylist.setCity(city);
                if (district != null)
                    hairstylist.setDistrict(district);
                if (address != null)
                    hairstylist.setAddress(address);
                if (attention != null)
                    hairstylist.setAttention(attention);
                if (longitude != null)
                    hairstylist.setLongitude(longitude);
                if (latitude != null)
                    hairstylist.setLatitude(latitude);

                hairstylistService.edit(hairstylist);


                logger.info("发型师用户 " + hairstylist.getHairstylistName() + "（" + myOpenid + "）重新修改了基本信息");
                logger.info("传入的数据："+JSONObject.toJSON(request.getParameterMap())+"\n");
                map.put("message", "发型师信息修改成功！");
                return map;
            } else {
                logger.info("发型师信息修改失败！！（没有权限！！）");
                logger.info("传入的数据："+JSONObject.toJSON(request.getParameterMap())+"\n");
                map.put("error", "发型师信息修改失败！！（没有权限！！）");
                return map;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("发型师信息修改失败！！（后端发生某些错误）");
            map.put("error", "发型师信息修改失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


    @ApiOperation(value = "根据发型师openid,获取单个发型师信息（包括总预约人数和今日预约人数）- 用于发型师首页", produces = "application/json")
    @GetMapping("/hairstylist")
    public Map getOne(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null) {
                logger.info("未找到该发型师用户(获取单个发型师信息)");
                map.put("error", "未找到该发型师用户！！");
                return map;
            }
            map = getOneById(hairstylist.getId());
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取发型师信息失败！！（后端发生某些错误）");
            map.put("error", "获取发型师信息失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "根据发型师id(不是openid),获取单个发型师信息（包括总预约人数和今日预约人数）", produces = "application/json")
    @GetMapping("/getHairstylistById")
    public Map getOneById(@RequestParam Integer hairstylistId) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistById(hairstylistId);

            map.put("hairstylist", hairstylist);//总预约人数和今日预约人数已经在Hairstylist的get方法里面，会直接被当做属性放进去
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取发型师信息失败！！（后端发生某些错误）");
            map.put("error", "获取发型师信息失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "分页获取所有发型师列表", notes = "仅管理员有权限", produces = "application/json")
    @GetMapping("/hairstylists/getAll")
    public Map getairstylistsPage(@RequestParam String myOpenid,
                                  @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                  @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
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
            logger.error(e.getMessage());
            logger.info("获取发型师列表信息失败！！（后端发生某些错误）");
            map.put("error", "获取发型师列表信息失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "发型师设置可预约时间")
    @PostMapping("/hairstylist/setTime")
    public Map setTime(@RequestParam String myOpenid,
                       @RequestParam(value = "timeList", required = false) List<String> timeList) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null || hairstylist.getApplyStatus() != 1) {
                logger.info("非发型师用户操作（设置可预约时间）！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else {
                String str = "";
                for (String time : timeList) {
                    if (str.length() > 0)
                        str = str + "," + time;
                    else
                        str = "" + time;
                }
                hairstylist.setAvailableTime(str);
                hairstylistService.edit(hairstylist);
                logger.info("发型师用户 " + hairstylist.getHairstylistName() + "（" + myOpenid + "）重新设置了可预约时间:" + timeList.toString());
                map.put("message", "可预约时间设置成功");
                return map;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("设置可预约时间操作失败！！（后端发生某些错误）");
            map.put("error", "设置可预约时间操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


    @ApiOperation(value = "根据发型师id，获取发型师的可预约时间")
    @GetMapping("/getHairstylistTime")
    public Map getHairstylistTime(@RequestParam Integer hairstylistId) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistById(hairstylistId);
            if (hairstylist == null) {
                logger.info("未找到该发型师用户(获取发型师的可预约时间)");
                map.put("error", "未找到该发型师用户！！·");
                return map;
            }
            map = getTime(hairstylist.getOpenid());
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取发型师信息失败！！（后端发生某些错误）");
            map.put("error", "获取发型师信息失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "根据openid，获取发型师的可预约时间")
    @GetMapping("/hairstylist/getTime")
    public Map getTime(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null || hairstylist.getApplyStatus() != 1) {
                logger.info("所查询的发型师不存在！！");
                map.put("error", "该发型师不存在！");
                return map;
            }

            DateFormat df3 = new SimpleDateFormat("HH:mm:ss");
            ;//只显示出时时分秒（12:43:37）的格式
            List<String> timeList = new ArrayList<>();
            String[] availableTime = hairstylist.getAvailableTime().split(",");
            for (String str : availableTime) {
                int hour;
                try {
                    hour = Integer.parseInt(str);
                    timeList.add(df3.format(MyUtils.getTime(hour)));
                } catch (Exception e) {
                    logger.info("可预约时间转换失败（数据为：" + str + "）");
                    map.put("error", "获取可预约时间失败！---可预约时间转换失败（数据为：" + str + "）");
                    return map;
                }
            }
            map.put("availableTimeList", timeList);
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取可预约时间操作失败！！（后端发生某些错误）");
            map.put("error", "获取可预约时间失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "获取个人作品图片url列表")
    @GetMapping("/hairstylist/getImageUrlList")
    public Map getImageUrlList(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null || hairstylist.getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else {
                map.put("imageUrlList", hairstylist.hairstylistImageUrlList);
                return map;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取个人作品图片操作失败！！（后端发生某些错误）\n\n");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "添加个人作品图片url列表")
    @PostMapping("/hairstylist/addImageUrlList")
    public Map addImageUrlList(@RequestParam String myOpenid,
                               @RequestParam(value = "imageList", required = false) List<String> imageList) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null || hairstylist.getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else {
                //下面保存该理发师的作品url
                if (imageList != null) {
                    int imageSize = imageList.size();
                    for (int i = 0; i < imageSize; i++) {
                        if(hairstylistImageUrlService.findByImgUrl(imageList.get(i))!=null){
                            logger.info("存在相同的个人作品图片，本次图片不上传");
                            continue;
                        }
                        HairstylistImageUrl hairstylistImageUrl = new HairstylistImageUrl();
                        hairstylistImageUrl.setHairstylist(hairstylist);
                        hairstylistImageUrl.setImageUrl(imageList.get(i));
                        hairstylistImageUrlService.save(hairstylistImageUrl);
                    }
                    logger.info("发型师用户 " + hairstylist.getHairstylistName() + "（" + myOpenid + "）保存了" + imageSize + "(实际上还要减去重复个数)张个人照片图片： " + imageList);
                    map.put("messgae", "保存成功！");
                } else {
                    map.put("error", "请选择图片！！");
                }
                return map;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("添加个人作品图片url列表操作失败！！（后端发生某些错误）");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "根据图片对应id,删除个人作品图片url")
    @DeleteMapping("/hairstylist/deleteImageUrlList")
    public Map deleteImageUrlList(@RequestParam String myOpenid,@RequestParam Integer id) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null || hairstylist.getApplyStatus() != 1) {
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
                    logger.info("发型师用户 " + imageUrl.getHairstylist().getHairstylistName() + "（" + myOpenid + "）删除图片 \"" + imageUrl.getImageUrl() + "\"");
                    map.put("message", "删除成功！");
                    return map;
                } else {
                    logger.info("删除个人作品图片url失败 - 没有权限");
                    map.put("error", "对不起，删除失败(没有权限)");
                    return map;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("删除个人作品图片url操作失败！！（后端发生某些错误）");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "获取个人的普通顾客预约数情况列表（降序排序） -  用于“发型师-数据中心-顾客列表”")
    @GetMapping("/hairstylist/getOrdinaryCustomerList")
    public Map getOrdinaryCustomerList(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null || hairstylist.getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            }

            //先获取所有的顾客预约数情况列表
            map = getCustomerList(myOpenid);
            List<CountUser> resultList = (List<CountUser>) map.get("resultList");

            if (resultList == null || resultList.size() == 0) {
                //发生错误，或者目前没有被预约过，直接返回
                return map;
            }
            map.clear();

            for (int i = 0; i < resultList.size(); ) {
                CountUser countUser = resultList.get(i);
                User user = userService.findUserById(countUser.getUserId());

                //如果用户收藏了该发型师
                if (user.isLoyalToHairstylist(hairstylist.getId())) {
                    resultList.remove(countUser);
                } else {
                    i++;
                }
            }

            map.put("resultList", resultList);
            if (resultList.size() == 0)
                map.put("message", "你目前没有普通顾客!");
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("个人的普通顾客预约数情况列表失败！！（后端发生某些错误）\n\n");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "获取个人的忠实（粉丝）顾客预约数情况列表（降序排序）  -  用于“发型师-数据中心-顾客列表”页面")
    @GetMapping("/hairstylist/getLoyalCustomerList")
    public Map getLoyalCustomerList(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null || hairstylist.getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            }

            //先获取所有的顾客预约数情况列表
            map = getCustomerList(myOpenid);
            List<CountUser> resultList = (List<CountUser>) map.get("resultList");

            if (resultList == null || resultList.size() == 0) {
                //发生错误，或者目前没有被预约过，直接返回
                return map;
            }
            map.clear();

            for (int i = 0; i < resultList.size(); ) {
                CountUser countUser = resultList.get(i);
                User user = userService.findUserById(countUser.getUserId());

                //如果用户没有收藏该发型师
                if (!user.isLoyalToHairstylist(hairstylist.getId())) {
                    resultList.remove(countUser);
                } else {
                    i++;
                }
            }
            map.put("resultList", resultList);
            if (resultList.size() == 0)
                map.put("message", "你目前没有忠实（粉丝）顾客!");
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("个人的忠实（粉丝）顾客预约数情况列表失败！！（后端发生某些错误）\n\n");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "获取个人的所有顾客预约数情况列表（降序排序）  -  用于“发型师-数据中心-顾客列表”")
    @GetMapping("/hairstylist/getCustomerList")
    public Map getCustomerList(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null || hairstylist.getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            }
            List<CountUser> resultList = new ArrayList<>();

            List<HaircutOrder> orderList = hairstylist.getHaircutOrderList();
            HaircutOrder order;
            while (orderList.size() > 0) {
                order = orderList.get(0);//一直获取发型师的预约单列表的第一个记录
                CountUser countUser = new CountUser();

                countUser.setUserId(order.user.getId());
                countUser.setUserName(order.getUserName());
                countUser.setHeadImgUrl(order.user.getPictureUrl());

                //找到该顾客的其他预约记录，计数后删除
                int count = 0;
                for (int i = 0; i < orderList.size(); ) {
                    HaircutOrder o = orderList.get(i);
                    if (o.user.getId() == order.user.getId()) {
                        count++;
                        orderList.remove(o);
                    } else {
                        i++;
                    }
                }
                countUser.setCount(count);
                resultList.add(countUser);
            }//while结束


            // 按计数倒序排序
            Collections.sort(resultList, (r1, r2) -> {
                if (r1.getCount() > r2.getCount()) {
                    return -1;
                } else if (r2.getCount() < r1.getCount()) {
                    return 1;
                }
                return 0; //相等为0
            });

            map.put("resultList", resultList);
            if (resultList.size() == 0)
                map.put("message", "你目前没有顾客!");
            return map;

        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取个人的顾客预约数情况列表失败！！（后端发生某些错误）\n\n");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "获取店内排行 - 全部")
    @GetMapping("/hairstylist/getInStoreRanking/all")
    public Map getInStoreRankingAll(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null || hairstylist.getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else {
                Double radius = 0.001;//0.001经纬度相对大概100米
                List<Hairstylist> hairstylists = hairstylistService.getHairstylistsByRadiusAndShopName(hairstylist.getLongitude(), hairstylist.getLatitude(), radius, hairstylist.getShopName());
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
                int myRankings = -1;//我的排名

                for (int i = 0; i < hairstylists.size(); i++) {
                    Hairstylist h = hairstylists.get(i);
                    RankingData data = new RankingData(i + 1, h, h.getOrderSum());
                    resultList.add(data);
                    if (data.getHairstylistId() == hairstylist.getId())
                        myRankings = i + 1;//获取我的排名
                }
                map.put("resultList", resultList);
                map.put("sumNum", resultList.size());
                map.put("myRankings", myRankings);
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
    @GetMapping("/hairstylist/getInStoreRanking/today")
    public Map getInStoreRankingToday(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null || hairstylist.getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else {
                Double radius = 0.001;//0.001经纬度相对大概100米
                List<Hairstylist> hairstylists = hairstylistService.getHairstylistsByRadiusAndShopName(hairstylist.getLongitude(), hairstylist.getLatitude(), radius, hairstylist.getShopName());
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
                int myRankings = -1;//我的排名

                for (int i = 0; i < hairstylists.size(); i++) {
                    Hairstylist h = hairstylists.get(i);
                    RankingData data = new RankingData(i + 1, h, h.getTodayOrderSum());
                    resultList.add(data);
                    if (data.getHairstylistId() == hairstylist.getId())
                        myRankings = i + 1;//获取我的排名
                }
                map.put("resultList", resultList);
                map.put("sumNum", resultList.size());
                map.put("myRankings", myRankings);
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
    @GetMapping("/hairstylist/getInStoreRanking/month")
    public Map getInStoreRankingMonth(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null || hairstylist.getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else {
                Double radius = 0.001;//0.001经纬度相对大概100米
                List<Hairstylist> hairstylists = hairstylistService.getHairstylistsByRadiusAndShopName(hairstylist.getLongitude(), hairstylist.getLatitude(), radius, hairstylist.getShopName());
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
                    if (data.getHairstylistId() == hairstylist.getId())
                        myRankings = i + 1;//获取我的排名
                }
                map.put("resultList", resultList);
                map.put("sumNum", resultList.size());
                map.put("myRankings", myRankings);
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
    @GetMapping("/hairstylist/getRegionalRanking/all")
    public Map getRegionalRankingAll(@RequestParam String myOpenid,
                                     @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                     @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null || hairstylist.getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else {
                Double radius = 0.001;//0.001经纬度相对大概100米
                List<Hairstylist> hairstylists = hairstylistService.getHairstylistsByRadius(hairstylist.getLongitude(), hairstylist.getLatitude(), radius);
                // 按完成订单数倒序排序
                Collections.sort(hairstylists, (o1, o2) -> {
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

                for (int i = 0; i < hairstylists.size(); i++) {
                    Hairstylist h = hairstylists.get(i);
                    RankingData data = new RankingData(i + 1, h, h.getOrderSum());
                    tempList.add(data);
                    if (data.getHairstylistId() == hairstylist.getId())
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
    @GetMapping("/hairstylist/getRegionalRanking/today")
    public Map getRegionalRankingToday(@RequestParam String myOpenid,
                                       @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                       @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null || hairstylist.getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else {
                Double radius = 0.001;//0.001经纬度相对大概100米
                List<Hairstylist> hairstylists = hairstylistService.getHairstylistsByRadius(hairstylist.getLongitude(), hairstylist.getLatitude(), radius);
                // 按今日预约的订单数倒序排序
                Collections.sort(hairstylists, (o1, o2) -> {
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

                for (int i = 0; i < hairstylists.size(); i++) {
                    Hairstylist h = hairstylists.get(i);
                    RankingData data = new RankingData(i + 1, h, h.getTodayOrderSum());
                    tempList.add(data);
                    if (data.getHairstylistId() == hairstylist.getId())
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
    @GetMapping("/hairstylist/getRegionalRanking/month")
    public Map getRegionalRankingMonth(@RequestParam String myOpenid,
                                       @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                       @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null || hairstylist.getApplyStatus() != 1) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
                return map;
            } else {
                Double radius = 0.001;//0.001经纬度相对大概100米
                List<Hairstylist> hairstylists = hairstylistService.getHairstylistsByRadius(hairstylist.getLongitude(), hairstylist.getLatitude(), radius);
                // 按完成订单数倒序排序
                Collections.sort(hairstylists, (o1, o2) -> {
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

                for (int i = 0; i < hairstylists.size(); i++) {
                    Hairstylist h = hairstylists.get(i);
                    RankingData data = new RankingData(i + 1, h, h.getCurrentMonthOrderSum());
                    tempList.add(data);
                    if (data.getHairstylistId() == hairstylist.getId())
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
//    @GetMapping("/hairstylist/getInStoreRanking")
//    public Map getInStoreRanking( @RequestParam String myOpenid) {
//        Map map = new HashMap();
//        try {
//            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
//            if ( hairstylist == null || hairstylist.getApplyStatus() != 1) {
//                logger.info("非发型师用户操作！！");
//                map.put("error", "对不起，你还不是发型师用户，无权操作！！");
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
