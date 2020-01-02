package com.gaocimi.flashpig.controller;

import com.alibaba.fastjson.JSONObject;
import com.gaocimi.flashpig.entity.*;
import com.gaocimi.flashpig.model.CountUser;
import com.gaocimi.flashpig.model.HairstylistInfo;
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
    ShopService shopService;
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
    @Autowired
    WxAppletCodeController wxAppletCodeController;

    @ApiOperation(value = "发型师注册")
    @PostMapping("/hairstylist/register")
    public Map addHairstylist(HttpServletRequest request, @RequestParam String openid,
                              @RequestParam(value = "hairstylistName", required = false) String hairstylistName,
                              @RequestParam(value = "personalPhotoUrl", required = false) String personalPhotoUrl,
                              @RequestParam(value = "personalPhone", required = false) String personalPhone,
                              @RequestParam(value = "personalProfile", required = false) String personalProfile) {
        Map map = new HashMap();


        if (hairstylistName == null || personalPhone == null || personalPhotoUrl == null) {
            logger.info("请完整填写个人资料!(发型师注册申请)");
            logger.info("传入的数据：" + JSONObject.toJSON(request.getParameterMap()) + "\n");
            map.put("message", "请完整填写个人资料！");
            return map;
        }

        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(openid);
            if (hairstylist != null) {
                logger.info("“" + hairstylist.getHairstylistName() + "”重复申请！！你的申请已通过，无需重复提交！！");
                map.put("message", "你的申请已通过，无需重复提交！！");
                return map;
            }

            hairstylist = hairstylistService.findHairstylistByPhone(personalPhone);
            if (hairstylist != null) {
                logger.info("“" + hairstylist.getHairstylistName() + "”个人电话重复的注册！！");
                map.put("message", "该电话已绑定发型师，不可重复注册！！");
                return map;
            }

            if (!MyUtils.isMobileNO(personalPhone)) {
                logger.info("电话号码（" + personalPhone + "）不合法！！");
                map.put("error", "电话号码（" + personalPhone + "）不合法！！");
                return map;
            }

            hairstylist = new Hairstylist();

            hairstylist.setOpenid(openid);
            hairstylist.setHairstylistName(hairstylistName);
            hairstylist.setPersonalPhotoUrl(personalPhotoUrl);
            hairstylist.setPersonalPhone(personalPhone);
            hairstylist.setPersonalProfile(personalProfile);


            hairstylistService.save(hairstylist);
            wxAppletCodeController.appletCodeGeneration(hairstylist.getOpenid(), null, null);//生成自己的小程序码
            logger.info("传入的数据：" + JSONObject.toJSON(request.getParameterMap()) + "\n");
            logger.info("发型师“" + hairstylist.getHairstylistName() + "”（id=" + hairstylist.getId() + "）注册成功！");

            map.put("message", "注册成功！");
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("传入的数据：" + JSONObject.toJSON(request.getParameterMap()) + "\n");
            map.put("error", "发型师提交申请失败！请检查输入数据（也有可能后端发生错误）");
            return map;
        }

    }

    @ApiOperation(value = "发型师修改个人信息", notes = "权限：发型师本人")
    @PutMapping("/hairstylist")
    public Map updateHairstylist(HttpServletRequest request, @RequestParam String myOpenid,
                                 @RequestParam(value = "hairstylistName", required = false) String hairstylistName,
                                 @RequestParam(value = "personalPhotoUrl", required = false) String personalPhotoUrl,
                                 @RequestParam(value = "personalPhone", required = false) String personalPhone,
                                 @RequestParam(value = "personalProfile", required = false) String personalProfile) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null) {
                logger.info("该发型师用户不存在（修改信息）！！");
                logger.info("传入的数据：" + JSONObject.toJSON(request.getParameterMap()) + "\n");

                map.put("error", "发型师用户不存在！！");
                return map;
            }

            if (myOpenid.equals(hairstylist.getOpenid()) || administratorService.isExist(myOpenid)) {

                if (hairstylistName != null)
                    hairstylist.setHairstylistName(hairstylistName);
                if (personalPhotoUrl != null)
                    hairstylist.setPersonalPhotoUrl(personalPhotoUrl);
                if (personalPhone != null) {
                    Hairstylist h = hairstylistService.findHairstylistByPhone(personalPhone);
                    if (h != null) {

                        if (h.getOpenid().equals(myOpenid)) {
                            map.put("message", "电话号码未修改");
                        } else {
                            logger.info("“" + hairstylist.getHairstylistName() + "”(id=" + hairstylist.getId() + ")修改个人电话与" + h.getHairstylistName() + "(id=" + h.getId() + ")重复！！(发型师修改个人信息)");
                            map.put("error", "该电话已绑定发型师！！");
                            return map;
                        }
                    }

                    if (!MyUtils.isMobileNO(personalPhone)) {
                        logger.info("电话号码（" + personalPhone + "）不合法！！");
                        map.put("error", "电话号码（" + personalPhone + "）不合法！！");
                        return map;
                    }

                    hairstylist.setPersonalPhone(personalPhone);
                }
                if (personalProfile != null)
                    hairstylist.setPersonalProfile(personalProfile);

                hairstylistService.edit(hairstylist);


                logger.info("发型师用户 " + hairstylist.getHairstylistName() + "（" + myOpenid + "）重新修改了基本信息");
                logger.info("传入的数据：" + JSONObject.toJSON(request.getParameterMap()) + "\n");
                if (map.get("message") != null)
                    map.put("message", "信息修改成功！(" + map.get("message") + ")");
                else
                    map.put("message", "信息修改成功！");

                return map;
            } else {
                logger.info("发型师信息修改失败！！（没有权限！！）");
                logger.info("传入的数据：" + JSONObject.toJSON(request.getParameterMap()) + "\n");
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

    @ApiOperation(value = "根据发型师id，注销发型师", notes = "权限：发型师本人或管理员")
    @DeleteMapping("/hairstylist")
    public Map deleteHairstylist(@RequestParam String myOpenid, @RequestParam Integer hairstylistId) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistById(hairstylistId);
            if (hairstylist == null) {
                logger.info("要注销的发型师用户不存在！！");
                map.put("error", "该用户不存在！！");
                return map;
            }
            if (myOpenid.equals(hairstylist.getOpenid()) || administratorService.isExist(myOpenid)) {
                hairstylistService.delete(hairstylistId);
                logger.info("注销发型师“" + hairstylist.getHairstylistName() + "”(id=" + hairstylistId + ")成功！");
                map.put("message", "注销发型师“" + hairstylist.getHairstylistName() + "”成功！");
                return map;
            } else {
                logger.info("注销发型师“" + hairstylist.getHairstylistName() + "”(id=" + hairstylistId + ")失败！！（没有权限！！）");
                map.put("error", "注销发型师“" + hairstylist.getHairstylistName() + "”失败！！（没有权限！！）");
                return map;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("注销发型师失败！（后端发生某些错误）");
            map.put("error", "注销发型师失败！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }

    }

    @ApiOperation(value = "发型师申请入驻门店")
    @PostMapping("/hairstylist/shop/applySettled")
    public Map applySettled(@RequestParam String myOpenid, @RequestParam Integer shopId) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null) {
                logger.info("非发型师用户操作（申请入驻门店）！！");
                map.put("error", "对不起，你不是发型师用户，无权操作！！");
                return map;
            }
            Shop shop = shopService.findShopById(shopId);
            if (shop == null) {
                logger.info("未找到所选门店！！（申请入驻门店）");
                map.put("error", "无效的门店！！");
                return map;
            }
            if (hairstylist.getShop() != null) {
                switch (hairstylist.getApplyStatus()) {
                    case 1:
                        if (hairstylist.getShop().getId() == shopId) {
                            logger.info("“" + hairstylist.getHairstylistName() + "”的申请已通过！无需重复提交");
                            map.put("error", "你的申请已通过，无需重复提交！！");
                        } else {
                            logger.info("“" + hairstylist.getHairstylistName() + "”已经入驻门店“" + hairstylist.getShop() + "”(id=" + hairstylist.getShop().getId() + ")，不可再申请入驻其他门店");
                            map.put("error", "你已经入驻门店“" + hairstylist.getShop() + "”，不可再申请入驻其他门店！！");
                        }
                        return map;
                    case 0:
                        logger.info("“" + hairstylist.getHairstylistName() + "”重复申请！！你的申请正在审核中，禁止同时提交多个申请！！");
                        map.put("error", "你的申请正在审核中，禁止同时提交多个申请！！");
                        return map;
                    case -1:
                        logger.info("“" + hairstylist.getHairstylistName() + "”之前的申请审核未通过，将删除原申请并重新提交本次申请！！");
                        break;
                    default:
                        logger.info("“" + hairstylist.getHairstylistName() + "”之前的申请状态异常!!（状态标志为“" + hairstylist.getApplyStatus() + "”）将清除记录并重新提交本次申请");
                        break;
                }
            }
            hairstylist.setShop(shop);
            hairstylist.setApplyStatus(0);
            hairstylistService.edit(hairstylist);
            logger.info("发型师“" + hairstylist.getHairstylistName() + "”（id=" + hairstylist.getId() + "）提交了对门店“" + shop.getShopName() + "”（id=" + shop.getId() + "）的入驻申请");
            map.put("message", "提交申请成功!");
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("发型师提交入驻申请失败！（后端发生某些错误）");
            map.put("error", "操作失败！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "发型师取消已提交的入驻申请")
    @DeleteMapping("/hairstylist/shop/applyClear")
    public Map applyClear(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null) {
                logger.info("非发型师用户操作（取消入驻申请）！！");
                map.put("error", "对不起，你不是发型师用户，无权操作！！");
                return map;
            }
            if (hairstylist.getShop() == null) {
                logger.info("“" + hairstylist.getHairstylistName() + "”没有正在审核的入驻申请！取消申请失败");
                map.put("error", "你目前没有正在审核的入驻申请！！");
                return map;
            }
            if (hairstylist.getShop() != null) {
                if (hairstylist.getApplyStatus() == 1) {
                    logger.info("“" + hairstylist.getHairstylistName() + "”的申请已通过！取消申请失败");
                    map.put("error", "你的申请已通过，取消失败！！");
                    return map;
                }
            }
            hairstylist.setShop(null);
            hairstylist.setSettledTime(null);
            hairstylist.setApplyStatus(0);
            hairstylistService.edit(hairstylist);
            logger.info("发型师“" + hairstylist.getHairstylistName() + "”（id=" + hairstylist.getId() + "）取消了自己的入驻申请");
            map.put("message", "取消申请成功!");
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("发型师取消入驻申请失败！（后端发生某些错误）");
            map.put("error", "操作失败！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "发型师退出当前门店")
    @PostMapping("/hairstylist/shop/exitCurrentShop")
    public Map exitCurrentShop(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null) {
                logger.info("非发型师用户操作（取消入驻申请）！！");
                map.put("error", "对不起，你不是发型师用户，无权操作！！");
                return map;
            }
            Shop shop = hairstylist.getShop();
            if (shop == null || hairstylist.getApplyStatus() != 1) {
                logger.info("“" + hairstylist.getHairstylistName() + "”当前没有入驻门店（发型师退出当前门店）");
                map.put("error", "你目前没有入驻的门店！！");
                return map;
            }
            hairstylist.setShop(null);
            hairstylist.setSettledTime(null);
            hairstylist.setApplyStatus(0);
            hairstylistService.edit(hairstylist);
            logger.info("发型师“" + hairstylist.getHairstylistName() + "”（id=" + hairstylist.getId() + "）退出了门店“" + shop.getShopName() + "”（id=" + shop.getId() + ")");
            map.put("message", "退出门店成功!");
            return map;
        } catch (Exception e) {
            logger.info("发型师退出门店失败！（后端发生某些错误）");
            map.put("error", "操作失败！（后端发生某些错误）");
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
            map.put("hairstylist", hairstylist);//总预约人数和今日预约人数已经在Hairstylist的get方法里面，会直接被当做属性放进去
            return map;
        } catch (Exception e) {
            logger.info("获取发型师信息失败！！（后端发生某些错误）");
            map.put("error", "获取发型师信息失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "根据发型师id(不是openid),获取单个发型师信息（包括总预约人数和今日预约人数）", produces = "application/json")
    @GetMapping("/getHairstylistById")
    public Map getOneById(@RequestParam String myOpenid, @RequestParam Integer hairstylistId) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistById(hairstylistId);
            User user = userService.findUserByOpenid(myOpenid);
            if (hairstylist != null) {

                if (user.isLoyalToHairstylist(hairstylistId))
                    map.put("isCollected", "yes");
                else
                    map.put("isCollected", "no");
                map.put("hairstylist", hairstylist);//总预约人数和今日预约人数已经在Hairstylist的get方法里面，会直接被当做属性放进去
                return map;
            } else {
                logger.info("查看的发型师(id=" + hairstylistId + ")不存在！");
                map.put("error", "查看的发型师不存在!");
                return map;
            }
        } catch (Exception e) {
            logger.info("获取发型师信息失败！！（后端发生某些错误）");
            map.put("error", "获取发型师信息失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "分页获取所有发型师列表", produces = "application/json")
    @GetMapping("/hairstylists/getAll")
    public Map getHairstylistsPage(@RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                   @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {
            Page<Hairstylist> page = hairstylistService.findAllByStatus(1, pageNum, pageSize);
            map.put("page", page);
            logger.info("获取发型师列表信息成功！::" + map);
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取发型师列表信息失败！！（后端发生某些错误）");
            map.put("error", "获取发型师列表信息失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "发型师转换营业状态")
    @PostMapping("/hairstylist/changeBusinessStatus")
    public Map changeBusinessStatus(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null) {
                logger.info("非发型师用户操作（设置可预约时间）！！");
                map.put("error", "对不起，你不是发型师用户，无权操作！！");
                return map;
            }

            if (hairstylist.getApplyStatus() != 1) {
                logger.info("非入驻发型师用户操作（设置可预约时间）！！");
                map.put("error", "对不起，你还没有入驻门店，不可操作！！");
                return map;
            }

            if (hairstylist.getBusinessStatus() == 1) {
                hairstylist.setBusinessStatus(0);
                map.put("message", "暂停营业");
            } else {
                hairstylist.setBusinessStatus(1);
                map.put("message", "开始营业");
            }
            hairstylistService.edit(hairstylist);
            logger.info("发型师用户 " + hairstylist.getHairstylistName() + "（id=" + hairstylist.getId() + "）设置了自己的营业状态为:" + hairstylist.getBusinessStatus() + "(0表示暂停营业，1表示营业中)");
            return map;

        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("转换营业状态操作失败！！（后端发生某些错误）");
            map.put("error", "转换营业状态操作失败！！（后端发生某些错误）");
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
            if (hairstylist == null) {
                logger.info("非发型师用户操作（设置可预约时间）！！");
                map.put("error", "对不起，你不是发型师用户，无权操作！！");
                return map;
            }

            if (hairstylist.getApplyStatus() != 1) {
                logger.info("非入驻发型师用户操作（设置可预约时间）！！");
                map.put("error", "对不起，你还没有入驻门店，不可操作！！");
                return map;
            }

            hairstylist.setAvailableTime(timeList);
            hairstylistService.edit(hairstylist);
            logger.info("发型师用户 " + hairstylist.getHairstylistName() + "（id=" + hairstylist.getId() + "）重新设置了可预约时间:" + timeList.toString());
            map.put("message", "可预约时间设置成功");
            return map;

        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("设置可预约时间操作失败！！（后端发生某些错误）");
            map.put("error", "设置可预约时间操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


    @ApiOperation(value = "发型师设置公告")
    @PostMapping("/hairstylist/setProclamation")
    public Map setProclamation(@RequestParam String myOpenid,
                               @RequestParam(value = "proclamation") String proclamation) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null) {
                logger.info("非发型师用户操作（设置公告）！！");
                map.put("error", "对不起，你不是发型师用户，无权操作！！");
                return map;
            }

            if (hairstylist.getApplyStatus() != 1) {
                logger.info("非入驻发型师用户操作（设置公告）！！");
                map.put("error", "对不起，你还没有入驻门店，不可操作！！");
                return map;
            }

            hairstylist.setProclamation(proclamation);
            hairstylistService.edit(hairstylist);
            logger.info("发型师用户 " + hairstylist.getHairstylistName() + "（id=" + hairstylist.getId() + "）设置了新的公告: " + proclamation);
            map.put("message", "设置成功！");
            return map;

        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("发型师设置公告操作失败！！（后端发生某些错误）");
            map.put("error", "设置公告操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "发型师删除公告")
    @DeleteMapping("/hairstylist/deleteProclamation")
    public Map deleteProclamation(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null) {
                logger.info("非发型师用户操作（删除公告）！！");
                map.put("error", "对不起，你不是发型师用户，无权操作！！");
                return map;
            }

            if (hairstylist.getProclamation() == null) {
                map.put("error", "你还没有正在发布的公告！");
                return map;
            }
            hairstylist.setProclamation(null);
            hairstylistService.edit(hairstylist);
            logger.info("发型师用户 " + hairstylist.getHairstylistName() + "（id=" + hairstylist.getId() + "）删除了 原公告:" + hairstylist.getProclamation());
            map.put("message", "删除成功！");
            return map;

        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("发型师删除公告操作失败！！（后端发生某些错误）");
            map.put("error", "删除公告操作失败！！（后端发生某些错误）");
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
            if (hairstylist == null) {
                logger.info("所查询的发型师不存在！！（获取发型师的可预约时间）");
                map.put("error", "该发型师不存在！");
                return map;
            }
            if (hairstylist.getApplyStatus() != 1) {
                logger.info("所查询的发型师还没有入驻门店（获取发型师的可预约时间）！！");
                map.put("error", "该发型师还没有入驻门店，暂时没有可预约时间！！");
                return map;
            }

            map.put("availableTimeList", hairstylist.getAvailableTime());
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
            if (hairstylist == null) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你不是发型师用户，无权操作！！");
                return map;
            }
            map.put("imageUrlList", hairstylist.hairstylistImageUrlList);
            return map;

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
            if (hairstylist == null) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你不是发型师用户，无权操作！！");
                return map;
            } else {

                //下面保存该理发师的作品url
                if (imageList == null) {
                    map.put("error", "请选择图片！！");
                    return map;
                }

                int imageSize = imageList.size();
                for (int i = 0; i < imageSize; i++) {
                    if (hairstylistImageUrlService.findByImgUrl(imageList.get(i)) != null) {
                        logger.info("存在相同的个人作品图片，本次图片不上传");
                        continue;
                    }
                    HairstylistImageUrl hairstylistImageUrl = new HairstylistImageUrl();
                    hairstylistImageUrl.setHairstylist(hairstylist);
                    hairstylistImageUrl.setImageUrl(imageList.get(i));
                    hairstylistImageUrlService.save(hairstylistImageUrl);
                }
                logger.info("发型师用户 " + hairstylist.getHairstylistName() + "（id=" + hairstylist.getId() + "）保存了" + imageSize + "(实际上还要减去重复个数)张个人作品图片： \n" + imageList);
                map.put("messgae", "保存成功！");

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
    @DeleteMapping("/hairstylist/deleteImageUrl")
    public Map deleteImageUrl(@RequestParam String myOpenid, @RequestParam Integer id) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null) {
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
            if (hairstylist == null) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你不是发型师用户，无权操作！！");
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
            if (hairstylist == null) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你不是发型师用户，无权操作！！");
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
                map.put("message", "你目前没有忠实顾客!");
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
            if (hairstylist == null) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你不是发型师用户，无权操作！！");
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
            if (hairstylist == null) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你不是发型师用户，无权操作！！");
                return map;
            }
            if (hairstylist.getApplyStatus() != 1) {
                logger.info("非入驻发型师用户操作（获取店内排行 - 全部）！！");
                map.put("error", "你还没有入驻门店！！");
                return map;
            }

            List<Hairstylist> hairstylists = hairstylist.shop.getHairstylists();
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
                if (data.getId() == hairstylist.getId())
                    myRankings = i + 1;//获取我的排名
            }
            map.put("resultList", resultList);
            map.put("sumNum", hairstylists.size());
            map.put("myRankings", myRankings);
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
    @GetMapping("/hairstylist/getInStoreRanking/today")
    public Map getInStoreRankingToday(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你不是发型师用户，无权操作！！");
                return map;
            }
            if (hairstylist.getApplyStatus() != 1) {
                logger.info("非入驻发型师用户操作（获取店内排行 - 今天）！！");
                map.put("error", "你还没有入驻门店！！");
                return map;
            }

            List<Hairstylist> hairstylists = hairstylist.shop.getHairstylists();

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
                if (data.getId() == hairstylist.getId())
                    myRankings = i + 1;//获取我的排名
            }
            map.put("resultList", resultList);
            map.put("sumNum", hairstylists.size());
            map.put("myRankings", myRankings);
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
    @GetMapping("/hairstylist/getInStoreRanking/month")
    public Map getInStoreRankingMonth(@RequestParam String myOpenid) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你不是发型师用户，无权操作！！");
                return map;
            }
            if (hairstylist.getApplyStatus() != 1) {
                logger.info("非入驻发型师用户操作（获取店内排行 - 本月）！！");
                map.put("error", "你还没有入驻门店！！");
                return map;
            }

            List<Hairstylist> hairstylists = hairstylist.shop.getHairstylists();

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
                if (data.getId() == hairstylist.getId())
                    myRankings = i + 1;//获取我的排名
            }
            map.put("resultList", resultList);
            map.put("sumNum", hairstylists.size());
            map.put("myRankings", myRankings);
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取店内排行列表失败！！（后端发生某些错误）\n\n");
            map.put("error", "操作失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "获取区域(经纬度相差0.001的)排行 - 全部")
    @GetMapping("/hairstylist/getRegionalRanking/all")
    public Map getRegionalRankingAll(@RequestParam String myOpenid,
                                     @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                     @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你不是发型师用户，无权操作！！");
                return map;
            }
            if (hairstylist.getApplyStatus() != 1) {
                logger.info("非入驻发型师用户操作（获取区域排行 - 全部）！！");
                map.put("error", "你还没有入驻门店，无权操作");
                return map;
            }

            Double radius = 0.001;//0.001经纬度相对大概100米
            List<Hairstylist> hairstylists = new ArrayList<>();
            Shop shop = hairstylist.getShop();
            List<Shop> shopList = shopService.getShopsByRadius(shop.getLongitude(), shop.getLatitude(), radius);

            for (Shop shop1 : shopList) {
                hairstylists.addAll(shop1.hairstylists);
            }

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
                if (data.getId() == hairstylist.getId())
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
            map.put("sumNum", hairstylists.size());
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

    @ApiOperation(value = "获取区域排行(经纬度相差0.001的) - 今天")
    @GetMapping("/hairstylist/getRegionalRanking/today")
    public Map getRegionalRankingToday(@RequestParam String myOpenid,
                                       @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                       @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你不是发型师用户，无权操作！！");
                return map;
            }
            if (hairstylist.getApplyStatus() != 1) {
                logger.info("非入驻发型师用户操作（获取区域排行 - 今天）！！");
                map.put("error", "你还没有入驻门店，无权操作");
                return map;
            }

            Double radius = 0.001;//0.001经纬度相对大概100米
            List<Hairstylist> hairstylists = new ArrayList<>();
            Shop shop = hairstylist.getShop();
            List<Shop> shopList = shopService.getShopsByRadius(shop.getLongitude(), shop.getLatitude(), radius);

            for (Shop shop1 : shopList) {
                hairstylists.addAll(shop1.hairstylists);
            }

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
                if (data.getId() == hairstylist.getId())
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
            map.put("sumNum", hairstylists.size());
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

    @ApiOperation(value = "获取区域排行(经纬度相差0.001的) - 本月")
    @GetMapping("/hairstylist/getRegionalRanking/month")
    public Map getRegionalRankingMonth(@RequestParam String myOpenid,
                                       @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                       @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {
            Hairstylist hairstylist = hairstylistService.findHairstylistByOpenid(myOpenid);
            if (hairstylist == null) {
                logger.info("非发型师用户操作！！");
                map.put("error", "对不起，你不是发型师用户，无权操作！！");
                return map;
            }
            if (hairstylist.getApplyStatus() != 1) {
                logger.info("非入驻发型师用户操作（获取区域排行 - 本月）！！");
                map.put("error", "你还没有入驻门店，无权操作");
                return map;
            }

            Double radius = 0.001;//0.001经纬度相对大概100米
            List<Hairstylist> hairstylists = new ArrayList<>();
            Shop shop = hairstylist.getShop();
            List<Shop> shopList = shopService.getShopsByRadius(shop.getLongitude(), shop.getLatitude(), radius);

            for (Shop shop1 : shopList) {
                hairstylists.addAll(shop1.hairstylists);
            }

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
                if (data.getId() == hairstylist.getId())
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
            map.put("sumNum", hairstylists.size());
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

    @ApiOperation(value = "根据经纬度获取推荐的发型师", notes = "从附近门店（经纬度相差0.01即约附近1公里左右）中随机选取一位发型师")
    @GetMapping("/user/getHairstylistRecommendedList")
    public Map getHairstylistRecommendedList(@RequestParam Double longitude,
                                             @RequestParam Double latitude) {
        Map map = new HashMap();
        try {

            Double radius = 0.01;
            List<Shop> shopList = shopService.getShopsByRadius(longitude, latitude, radius);
            List<Hairstylist> tempHairstylists = new ArrayList<>();

            if (shopList == null || shopList.size() == 0) {
                logger.info("附近没有门店~（经度：" + longitude + "  维度：" + latitude + "  ）");
                map.put("message", "附近没有门店~");
                return map;
            }

            for (Shop shop : shopList) {
                tempHairstylists.addAll(shop.getHairstylistsByStatusAndBusinessStatus(1, 1));
            }

            if (tempHairstylists == null || tempHairstylists.size() == 0) {
                logger.info("附近没有可预约发型师~（经度：" + longitude + "  维度：" + latitude + "  ）");
                map.put("message", "附近没有可预约发型师~");
                return map;
            }
            int index = (int) (Math.random() * (tempHairstylists.size()));
            Hairstylist hairstylist = tempHairstylists.get(index);
            HairstylistInfo hairstylistInfo = new HairstylistInfo(tempHairstylists.get(index));
//            System.out.println("tempHairstylists.size()="+tempHairstylists.size()+",index = "+index);

            map.put("hairstylist", hairstylistInfo);
            map.put("distance", MyUtils.getDistance(latitude, longitude, hairstylist.shop.getLatitude(), hairstylist.shop.getLongitude()));
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取附近发型师失败列表失败！！（后端发生某些错误）");
            map.put("error", "获取附近发型师失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


    @ApiOperation(value = "根据经纬度获取周围的发型师", notes = "从附近门店（经纬度相差0.01即约附近1公里左右）中获取发型师列表(按照flag排序：flag=0表示按照发型师的评分降序排序，flag=1表示按发型师已完成订单总数降序排序)")
    @GetMapping("/user/getLocalHairstylists")
    public Map getLocalHairstylists(@RequestParam Double longitude,
                                    @RequestParam Double latitude, @RequestParam Integer flag,
                                    @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                    @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {

            Double radius = 0.01;
            List<Shop> shopList = shopService.getShopsByRadius(longitude, latitude, radius);
            List<HairstylistInfo> resultList = new ArrayList<>();

            if (shopList == null || shopList.size() == 0) {
                logger.info("附近没有门店~（经度：" + longitude + "  维度：" + latitude + "  ）");
                map.put("message", "附近没有门店~");
                return map;
            }

            for (Shop shop : shopList) {
                for (Hairstylist hairstylist : shop.getHairstylistsByStatusAndBusinessStatus(1, 1))
                    resultList.add(new HairstylistInfo(hairstylist));
            }
            if (resultList == null || resultList.size() == 0) {
                logger.info("附近没有可预约发型师~（经度：" + longitude + "  维度：" + latitude + "  ）");
                map.put("message", "附近没有可预约发型师~");
                return map;
            }

            switch (flag) {
                case 1:
                    // 按已完成订单总数倒序排序
                    Collections.sort(resultList, (r1, r2) -> {
                        if (r1.getOrderSum() > r2.getOrderSum()) {
                            return -1;
                        } else if (r2.getOrderSum() < r1.getOrderSum()) {
                            return 1;
                        }
                        return 0; //相等为0
                    });
                    break;
                case 0:
                    // 按评分倒序排序
                    Collections.sort(resultList, (r1, r2) -> {
                        if (r1.getPoint() > r2.getPoint()) {
                            return -1;
                        } else if (r2.getPoint() < r1.getPoint()) {
                            return 1;
                        }
                        return 0; //相等为0
                    });
                    break;
                default:
                    map.put("error", "flag标记错误：" + flag);
                    return map;
            }

            Page<HairstylistInfo> page = MyUtils.getPage(resultList, pageNum, pageSize);

            map.put("page", page);
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取附近发型师失败列表失败！！（后端发生某些错误）");
            map.put("error", "获取附近发型师失败！！（后端发生某些错误）");
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
//                map.put("error", "对不起，你不是发型师用户，无权操作！！");
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
