package com.gaocimi.flashpig.controller;

import com.gaocimi.flashpig.entity.*;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.*;
import com.gaocimi.flashpig.service.impl.WxPayOrderServiceImpl;
import com.gaocimi.flashpig.utils.xp.IpUtil;
import com.gaocimi.flashpig.utils.xp.MyMD5Util;
import com.gaocimi.flashpig.utils.xp.MyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@ResponseResult
@Api(value = "测试controller", description = "测试一些功能")
public class TestController {
    private static Logger logger = LoggerFactory.getLogger(WxPaymentController.class);

    @Autowired
    ArticleService articleService;
    @Autowired
    HairstylistService hairstylistService;
    @Autowired
    ShopService shopService;
    @Autowired
    UserService userService;
    @Autowired
    ProductOrderService productOrderService;
    @Autowired
    WxPayOrderService wxPayOrderService;

    @ApiOperation(value = "时间测试", produces = "application/json")
    @GetMapping("/timeTest")
    public Map Test() {
        Map map = new HashMap();

//        Map daily = new HashMap();
//        Map weekly = new HashMap();
//        Map monthly = new HashMap();
//
//        Date today = MyUtils.getTodayFirstTime();
//        Date week = MyUtils.getFirstDayOfWeek(today);
//        Date month = MyUtils.getFirstDayOfMonth(today);
//
//        for(int i = 0;i<13;i++){
//            daily.put(i+"daysAgo", MyUtils.stepDay(today,-i));
//            weekly.put(i+"weeksAgo",MyUtils.stepWeek(week,-i));
//            monthly.put(i+"monthsAgo",MyUtils.stepMonth(month,-i));
//        }
//        map.put("daily",daily);
//        map.put("weekly",weekly);
//        map.put("monthly",monthly);
        map.put("nowTime",new Date(System.currentTimeMillis()));
        map.put("new Date.getTime()",new Date().getTime());
        map.put("System.currentTimeMillis()",System.currentTimeMillis());
        map.put("getTimeFromNowAddDays(1)",MyUtils.getTimeFromDateAddDays(new Date() , 1));
        return map;
    }

    @ApiOperation(value = "****************时间格式(yyyy-MM-dd HH:mm:ss)传输测试*************************")
    @GetMapping("/timeFormatTest")
    public Date getHaircutOrdersPage(String time) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
        return date;
    }

    @ApiOperation(value = "****************获取时间的小时数*************************")
    @GetMapping("/getDataHour")
    public Map getDataHour() throws ParseException {
        Map map = new HashMap();
        Date date = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        map.put("当前小时数",calendar.get(Calendar.HOUR_OF_DAY));
        return map;
    }


    @ApiOperation(value = "对象比较测试(订单号1，订单号2)", produces = "application/json")
    @GetMapping("/equalTest")
    public boolean TestString(Integer a,Integer b) {

        Article article1 = articleService.findArticleById(a);
        Article article2 = articleService.findArticleById(b);

        return article1.equals(article2);
    }

    @ApiOperation(value = "分页测试", produces = "application/json")
    @GetMapping("/pageTest")
    public Page TestPage(@RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                         @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {

        List<Hairstylist> list = hairstylistService.getHairstylistList();
        //包装分页数据
        Sort sort = new Sort(Sort.Direction.ASC, "createTime");  //按申请时间升序
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        Page<Hairstylist> page = new PageImpl<>(list, pageable, 5*list.size());
        return page;
    }



    @ApiOperation(value = "获取用户ip测试", produces = "application/json")
    @GetMapping("/getUserIpTest")
    public Map getUserIpTest(HttpServletRequest request) {
        Map map = new HashMap();
        String ip = IpUtil.getIpAddress(request);
        logger.info("用户ip:"+ip);
        map.put("ip",ip);
        return map;
    }

    @ApiOperation(value = "获取数据测试", produces = "application/json")
    @PostMapping("/recipeData")
    public void notify(@RequestParam Map<String,Object> map){
        logger.info("\n获取的数据:\n"+map+"\n");
    }


    @ApiOperation(value = "List转数组测试", produces = "application/json")
    @PostMapping("/listToArray")
    public Map listToArray(@RequestParam List<String> list){
        Map map = new HashMap();

        String []array;

        //1）等于0，动态创建与size相同的数组，性能最好。
        //2）大于0但小于size，重新创建大小等于size的数组，增加GC负担。
        //3）等于size，在高并发情况下，数组创建完成之后，size正在变大的情况下，负面影响与2相同。
        //4）大于size，空间浪费，且在size处插入null值，存在NPE隐患。
        array = list.toArray(new String[0]);

        map.put("array",array);
        return map;
    }

    @ApiOperation(value = "获取今天days天后的日期", produces = "application/json")
    @PostMapping("/buyVipTest")
    public Map buyVipTest(@RequestParam Integer days){
        Map map = new HashMap();

        User user = userService.findUserById(26);
        map.put("result",user.buyVip(days));
        return map;
    }

    @ApiOperation(value = "测试", produces = "application/json")
    @PostMapping("/test")
    public Map test(String openid ,String password){
        Map map = new HashMap();

        Shop shop = shopService.findShopByOpenid(openid);
        if (shop == null) {
            logger.info("未登录操作！！");
            map.put("error", "请先登录！！");
            return map;
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
        map.put("message","门店“"+shop.getShopName()+"”设置密码："+encryptedPassword+"("+password+")");
        return map;
    }

//    public static void main(String[] args){
//
//        //-1表示商品购物订单
//        WxPayOrder payOrder = wxPayOrderService.findWxPayOrderById(1040);
//        ProductOrder productOrder = payOrder.getProductOrder();
//        if(payOrder==null){
//            logger.info("》》》未查找到对应商品订单，请管理员尽快查看！！！《《《）");
//        }
//        productOrder.setStatus(1);
//        productOrder.setWxPayOrder(payOrder);
//
////        productOrderService.edit(productOrder);
//    }



}
