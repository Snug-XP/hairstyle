package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gaocimi.flashpig.model.ShopSimpleInfo;
import com.gaocimi.flashpig.utils.xp.MyUtils;
import lombok.Data;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Hairstylist - 发型师类
 *
 * @author xp
 * @date 2019-9-23 03:09:37
 */
@Entity
@Table(name = "hairstylist")
@JsonIgnoreProperties(value = {"applyTime","applyResultDescription","openid","shop", "articleList", "getCurrentMonthOrderSum", "allOperationalData", "loyalUserRecordList", "haircutOrderList", "hairstylistImageUrlList", "hairServiceList", "recordToUserList", "userList", "handler", "hibernateLazyInitializer", "fieldHandler"})
@Data
public class Hairstylist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 对应的微信openid
     */
    private String openid;

    /**
     * 发型师名称
     */
    private String hairstylistName;

    /**
     * 个人联系电话
     */
    private String personalPhone;

    /**
     * 个人照片的url
     */
    private String personalPhotoUrl;

    /**
     * 发型师简介
     */
    private String personalProfile;

    /**
     * 该发型师所在门店； 定义名为shop_id的外键列，该外键引用shop表的主键(id)列,采用懒加载。
     * 只有当门店不为空且applyStatus为1时，才能说明该发型师入驻了该门店
     */
    @ManyToOne(targetEntity = Shop.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    public Shop shop;

    /**
     * 可预约时间点，用逗号隔开整点时间点，例如（19:00 20:00 21:00）记为（19,20,21）
     */
    private String availableTime;

    /**
     * 用户评分
     */
    private Double point;

    /**
     * 发型师账户创建时间
     */
    private Date createTime;

    /**
     * 发型师入驻门店的时间
     */
    private Date settledTime;

    /**
     * 完成订单总数
     */
    private Integer orderSum;

    /**
     * 发型师入驻门店的申请状态（0表示申请中，1表示申请通过, -1表示申请失败）
     */
    private Integer applyStatus;

    /** 发型师的营业状态（0表示未营业，其它状态表示营业中） */
    private Integer businessStatus;

    /** 公告 */
    private String  proclamation;
    /** 发型师提交入驻门店申请的时间 */
    private Date  applyTime;
    /** 发型师申请入驻门店结果的审核说明 */
    private String applyResultDescription;


    /**
     * 发型师上传的图片列表； 定义该Hairstylist实体所有关联的HairstylistImageUrl实体； 指定mappedBy属性表明该Hairstylist实体不控制关联关系
     */
    @OneToMany(targetEntity = HairstylistImageUrl.class, mappedBy = "hairstylist")
    public List<HairstylistImageUrl> hairstylistImageUrlList;

    /**
     * 发型师拥有的服务项列表； 定义该Hairstylist实体所有关联的HairService实体； 指定mappedBy属性表明该Hairstylist实体不控制关联关系
     */
    @OneToMany(targetEntity = HairService.class, mappedBy = "hairstylist")
    public List<HairService> hairServiceList;

    /**
     * 发型师发表的发型文章列表； 定义该Hairstylist实体所有关联的Article实体； 指定mappedBy属性表明该Hairstylist实体不控制关联关系
     */
    @OneToMany(targetEntity = Article.class, mappedBy = "hairstylist")
    public List<Article> articleList;

    /**
     * 发型师拥有的订单列表； 定义该Hairstylist实体所有关联的HaircutOrder实体； 指定mappedBy属性表明该Hairstylist实体不控制关联关系
     */
    @OneToMany(targetEntity = HaircutOrder.class, mappedBy = "hairstylist")
    public List<HaircutOrder> haircutOrderList;

    /**
     * 发型师提交的对顾客的备注列表； 定义该Hairstylist实体所有关联的RecordHairstylisToUser实体； 指定mappedBy属性表明该Hairstylist实体不控制关联关系
     */
    @OneToMany(targetEntity = RecordHairstylisToUser.class, mappedBy = "hairstylist")
    public List<RecordHairstylisToUser> recordToUserList;

    /**
     * 收藏该发型师的用户（即粉丝）提交的对该发型师的收藏记录列表； 定义该Hairstylist实体所有关联的UserToHairstylist实体； 指定mappedBy属性表明该Hairstylist实体不控制关联关系
     */
    @OneToMany(targetEntity = UserToHairstylist.class, mappedBy = "hairstylist")
    public List<UserToHairstylist> loyalUserRecordList;


    //初始化
    public Hairstylist() {
        Date date = new Date(System.currentTimeMillis());

        setApplyStatus(0);
        setCreateTime(date);//设置注册时间
        setOrderSum(0);//根据自己的订单列表（中的已完成）数量进行校正,注册时没有订单，所以为0
        setPoint(-1.0);
    }


    public ShopSimpleInfo getShopSimpleInfo() {
        if (shop == null)
            return null;
        else
            return new ShopSimpleInfo(shop);
    }

    public List<String> getAvailableTime() {

        if(this.availableTime==null||this.availableTime.length()<1) return null;
        DateFormat df3 = new SimpleDateFormat("HH:mm:ss");
        ;//只显示出时时分秒（12:43:37）的格式
        List<String> timeList = new ArrayList<>();
        String[] availableTime = this.availableTime.split(",");
        for (String str : availableTime) {
            int hour;
            try {
                hour = Integer.parseInt(str);
                timeList.add(df3.format(MyUtils.getTime(hour)));
            } catch (Exception e) {
                System.out.println("可预约时间转换失败（数据为：" + str + "）");
                return null;
            }
        }

        return timeList;
    }

    public void setAvailableTime(List<String> timeList) {
        String str = "";
        for (String time : timeList) {
            if (str.length() > 0)
                str = str + "," + time;
            else
                str = "" + time;
        }
        this.availableTime = str;
    }

    public Double getPoint() {
        regulatePoint();
        return point;
    }

    /**
     * 根据自己的订单列表（已完成且有评分的）进行校正
     */
    public void regulatePoint() {
        int count = 0;
        double sumPoint = 0.0;
        for (HaircutOrder order : haircutOrderList) {
            if (order.getStatus() == 2 && order.getPoint() != null && order.getPoint() > 0) {
                count++;
                sumPoint += order.getPoint();
            }
        }
        if (count > 0)
            this.point = sumPoint / count;
    }


    /**
     * 校正已完成订单数量 - 根据自己的订单列表（中的已完成）数量进行校正
     */
    public void regulateOrderSum() {
        int count = 0;
        for (HaircutOrder order : haircutOrderList) {
            if (order.getStatus() == 2) {
                count++;
            }
        }
        this.orderSum = count;
    }

    public Integer getOrderSum() {
        regulateOrderSum();
        return orderSum;
    }

    /**
     * 获取入驻本门店后的总订单数
     */
    public int getOrderSumAfterSettledTime() {
        int count = 0;
        for (HaircutOrder order : haircutOrderList) {
            if (order.getStatus() == 2&&( this.settledTime==null||order.getBookTime().after(this.settledTime) ) ){
                count++;
            }
        }
        return count;
    }




    /**************下面是一些关于发型师数据统计相关的方法*******************************************************************/

    /**
     * 获取发型师自己的顾客总数
     *
     * @return 顾客总数
     */
    public int getCustomerSum() {
        int count = 0;//顾客总数
        List<HaircutOrder> orderList = new ArrayList<>(haircutOrderList);
        HaircutOrder order;
        while (orderList.size() > 0) {
            order = orderList.get(0);
            if (order.getStatus() == 2)
                count++;
            //从订单列表中移除同样用户的订单
            for (int i = 0; i < orderList.size(); ) {
                HaircutOrder o = orderList.get(i);
                if (o.user.getId() == order.user.getId()) {
                    orderList.remove(o);
                } else {
                    i++;
                }
            }
        }
        return count;
    }

    /**
     * 获取页面中日报周报月报折线图中的所有数据
     */
    public Map getAllOperationalData() {
        Map map = new HashMap();

        Map daily = new HashMap();
        Map weekly = new HashMap();
        Map monthly = new HashMap();

        Date today = MyUtils.getTodayFirstTime();//获取今天00点00分00秒的时间Data
        Date week = MyUtils.getFirstDayOfWeek(today);//获取今天所在周的星期一的日期Date(时间为00:00:00)
        Date month = MyUtils.getFirstDayOfMonth(today);//获取今天所在月的第一天的日期Date(时间为00:00:00)

        Map data;
        List<Integer> reservationNum = new ArrayList<>();
        List<Integer> newCustomerNum = new ArrayList<>();
        List<Integer> newLoyalCustomerNum = new ArrayList<>();
        //获取第0~6天前的数据
        for (int i = 6; i >= 0; i--) {
            data = getOperationalData(MyUtils.stepDay(today, -i), MyUtils.stepDay(today, -i + 1));
            reservationNum.add((Integer) data.get("reservationNum"));
            newCustomerNum.add((Integer) data.get("newCustomerNum"));
            newLoyalCustomerNum.add((Integer) data.get("newLoyalCustomerNum"));
        }
        daily.put("reservationNum", reservationNum);
        daily.put("newCustomerNum", newCustomerNum);
        daily.put("newLoyalCustomerNum", newLoyalCustomerNum);
        reservationNum = new ArrayList<>();
        newCustomerNum = new ArrayList<>();
        newLoyalCustomerNum = new ArrayList<>();

        //获取第0~3周前的数据
        for (int i = 3; i >= 0; i--) {
            data = getOperationalData(MyUtils.stepWeek(week, -i), MyUtils.stepWeek(week, -i + 1));
            reservationNum.add((Integer) data.get("reservationNum"));
            newCustomerNum.add((Integer) data.get("newCustomerNum"));
            newLoyalCustomerNum.add((Integer) data.get("newLoyalCustomerNum"));
        }
        weekly.put("reservationNum", reservationNum);
        weekly.put("newCustomerNum", newCustomerNum);
        weekly.put("newLoyalCustomerNum", newLoyalCustomerNum);
        reservationNum = new ArrayList<>();
        newCustomerNum = new ArrayList<>();
        newLoyalCustomerNum = new ArrayList<>();

        //获取第0~5月前的数据
        for (int i = 5; i >= 0; i--) {
            data = getOperationalData(MyUtils.stepMonth(month, -i), MyUtils.stepMonth(month, -i + 1));
            reservationNum.add((Integer) data.get("reservationNum"));
            newCustomerNum.add((Integer) data.get("newCustomerNum"));
            newLoyalCustomerNum.add((Integer) data.get("newLoyalCustomerNum"));
        }
        monthly.put("reservationNum", reservationNum);
        monthly.put("newCustomerNum", newCustomerNum);
        monthly.put("newLoyalCustomerNum", newLoyalCustomerNum);

        map.put("daily", daily);
        map.put("weekly", weekly);
        map.put("monthly", monthly);
        return map;
    }

    /**
     * 获取发型师日期在date1到date2之间(包括date1和date2且date1<=date2)的运营数据
     *
     * @param date1 日期时间1
     * @param date2 日期时间2
     * @return 日期在date1到date2之间(包括date1和date2且date1 < = date2)的运营数据
     */
    public Map getOperationalData(Date date1, Date date2) {
        Map map = new HashMap();
        List<HaircutOrder> orderList = new ArrayList<>();
        int newCustomerNum = 0;

        for (HaircutOrder order : haircutOrderList) {
            if (order.getStatus() != 2) continue;//只选择已完成的订单
            Date bookTime = order.getBookTime();
            //取出相应时间的订单
            if (bookTime.after(date1) && bookTime.before(date2)) {
                orderList.add(order);
            }
        }
        for (HaircutOrder order : orderList) {
            if (isNewCustomer(order)) {
                newCustomerNum++;
            }
        }

        map.put("reservationNum", orderList.size());//已完成的预约订单数
        map.put("newCustomerNum", newCustomerNum);//新增顾客数
        map.put("newLoyalCustomerNum", getNewLoyalCustomerNum(date1, date2));//新增忠实（粉丝）用户数

        return map;
    }

    /**
     * 获取发型师在本月的完成订单数
     *
     * @return 本月的完成订单数
     */
    public int getCurrentMonthOrderSum() {
        int count = 0;
        Date month = MyUtils.getFirstDayOfMonth(new Date(System.currentTimeMillis()));//获取今天所在月的第一天的日期Date(时间为00:00:00)
        for (HaircutOrder order : haircutOrderList) {
            if (order.getStatus() == 2 && order.getBookTime().after(month)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 获取发型师在本月(且处于入驻门店之后)的完成订单数
     *
     * @return 入驻门店之后本月的完成订单数
     */
    public int getCurrentMonthOrderSumAfterSettledTime() {
        int count = 0;
        Date month = MyUtils.getFirstDayOfMonth(new Date(System.currentTimeMillis()));//获取今天所在月的第一天的日期Date(时间为00:00:00)
        for (HaircutOrder order : haircutOrderList) {
            if (order.getStatus() == 2 && (this.settledTime==null||order.getBookTime().after(this.settledTime) )&& order.getBookTime().after(month)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 获取发型师今天的预约订单数
     *
     * @return 今天的预约订单数
     */
    public int getTodayOrderSum() {
        int todayOrderCount = 0;//今日预约人数

        for (HaircutOrder haircutOrder : haircutOrderList) {
            if (haircutOrder.getStatus() == -2) continue;//已被取消的订单不计算进去
            Long day = MyUtils.getDifferenceToday(haircutOrder.getBookTime());//取得预约时间与今天23点59分相差的天数
            if (day == 0) {
                todayOrderCount++;
            }
        }
        return todayOrderCount;
    }

    /**
     * 判断该订单用户是不是该发型师的新顾客
     *
     * @param order 订单类
     * @return
     */
    public boolean isNewCustomer(HaircutOrder order) {
        // 将订单预约的时间顺序排序
        Collections.sort(haircutOrderList, (r1, r2) -> {
            if (r1.getBookTime().after(r2.getBookTime())) {
                return 1;
            } else if (r2.getBookTime().after(r1.getBookTime())) {
                return -1;
            }
            return 0; //相等为0
        });
        for (HaircutOrder o : haircutOrderList) {
            if (o.getStatus() == 2 && o.getUser().getId() == order.getUser().getId()) {
                //找到预约时间最早的相同用户的已完成订单

                if (o.getId() == order.getId())
                    return true;
                else
                    return false;
            }
        }
        return false;
    }


    /**
     * 获取发型师日期在date1到date2之间(包括date1和date2且date1<=date2)的新增忠实（粉丝）用户数
     *
     * @param date1 日期时间1
     * @param date2 日期时间2
     * @return 日期在date1到date2之间(包括date1和date2且date1 < = date2)的新增忠实（粉丝）用户数
     */
    public int getNewLoyalCustomerNum(Date date1, Date date2) {
        int count = 0;
        for (UserToHairstylist record : loyalUserRecordList) {
            Date createTime = record.getCreateTime();
            //取出相应时间的记录
            if (createTime.after(date1) && createTime.before(date2)) {
                count++;
            }
        }
        return count;
    }


    public boolean isMyArticle(int articleId) {
        for (Article article : articleList) {
            if (articleId == article.getId())
                return true;
        }
        return false;
    }


}