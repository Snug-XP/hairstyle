package com.gaocimi.flashpig.controller;

import com.gaocimi.flashpig.entity.Article;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.ArticleService;
import com.gaocimi.flashpig.utils.xp.MyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@ResponseResult
@Api(value = "测试controller", description = "测试一些功能")
public class TestController {

    @Autowired
    ArticleService articleService;

    @ApiOperation(value = "时间测试", produces = "application/json")
    @GetMapping("/timeTest")
    public Map Test(Double a) {
        Double testDouble;
        testDouble = Double.parseDouble(a.toString());
        Map map = new HashMap();

        Map daily = new HashMap();
        Map weekly = new HashMap();
        Map monthly = new HashMap();

        Date today = MyUtils.getTodayFirstTime();
        Date week = MyUtils.getFirstDayOfWeek(today);
        Date month = MyUtils.getFirstDayOfMonth(today);

        for(int i = 0;i<13;i++){
            daily.put(i+"daysAgo", MyUtils.stepDay(today,-i));
            weekly.put(i+"weeksAgo",MyUtils.stepWeek(week,-i));
            monthly.put(i+"monthsAgo",MyUtils.stepMonth(month,-i));
        }
        map.put("daily",daily);
        map.put("weekly",weekly);
        map.put("monthly",monthly);
        map.put("nowTime",new Date(System.currentTimeMillis()));
        map.put("double",testDouble);
        return map;
    }

    @ApiOperation(value = "****************时间格式(yyyy-MM-dd HH:mm:ss)传输测试*************************")
    @GetMapping("/timeFormatTest")
    public Date getHaircutOrdersPage(String time) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
        return date;
    }

    @ApiOperation(value = "对象比较测试", produces = "application/json")
    @GetMapping("/stringTest")
    public boolean TestString(Integer a,Integer b) {

        Article article1 = articleService.findArticleById(a);
        Article article2 = articleService.findArticleById(b);

        return article1.equals(article2);
    }
}
