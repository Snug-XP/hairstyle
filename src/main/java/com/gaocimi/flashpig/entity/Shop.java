package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gaocimi.flashpig.utils.xp.MyUtils;
import lombok.Data;

import javax.persistence.*;
import java.util.*;

/**
 * Shop - 门店类
 *
 * @author xp
 * @date 2019-11-14 17:57:34
 */
@Entity
@Table(name = "shop")
@JsonIgnoreProperties(value = {"customerAnalyzeData", "timeRate", "loyalCustomerList", "customerList", "maleAndFemaleInfo", "mostPopularPerson", "maxOrderPerson", "maxPointPerson", "point", "applyTime", "applyResultDescription", "openid", "password", "hairstylistsByStatus", "hairstylists", "handler", "hibernateLazyInitializer", "fieldHandler"})
@Data
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 记录本次登录的微信的openid
     */
    @Column(name = "temp_openid")
    private String openid;

    /**
     * 门店名称
     */
    private String shopName;

    /**
     * 门店联系电话（也是登录账号）
     */
    private String phone;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 门店标志图片url
     */
    private String logoUrl;

    /**
     * 该门店经营执照的图片url
     */
    private String operatingLicensePictureUrl;

    /**
     * 门店所在省份
     */
    private String province;

    /**
     * 门店所在城市
     */
    private String city;

    /**
     * 门店所在区县
     */
    private String district;

    /**
     * 门店详细地址
     */
    private String address;

    /**
     * 门店经度，对于两个接近赤道的点，在纬度相等的情况下： 经度每隔0.00001度，距离相差约1米；每隔0.0001度，距离相差约10米；每隔0.001度，距离相差约100米；
     */
    private Double longitude;

    /**
     * 门店纬度，对于两个接近赤道的点，在经度相等的情况下： 纬度每隔0.00001度，距离相差约1.1米；每隔0.0001度，距离相差约11米；每隔0.001度，距离相差约111米；
     */
    private Double latitude;

    /**
     * 该门店的申请时间
     */
    private Date createTime;

    /**
     * 完成订单总数
     */
    private Integer orderSum;

    /**
     * 用户评分
     */
    private Double point;

    /**
     * 门店申请营业认定的状态（0表示申请中，1表示申请通过, -1表示申请失败）
     */
    private Integer applyStatus;
    /**
     * 门店提交营业认定申请的时间
     */
    private Date applyTime;
    /**
     * 门店申请营业认定结果的审核说明
     */
    private String applyResultDescription;


    /**
     * 门店拥有的订单列表； 定义该Shop实体所有关联的Hairstylist实体； 指定mappedBy属性表明该Shop实体不控制关联关系
     */
    @OneToMany(targetEntity = Hairstylist.class, mappedBy = "shop")
    public List<Hairstylist> hairstylists;

    /**
     * 门店上传的场景图片列表； 定义该Shop实体所有关联的ShopImageUrl实体； 指定mappedBy属性表明该Shop实体不控制关联关系
     */
    @OneToMany(targetEntity = ShopImageUrl.class, mappedBy = "shop")
    public List<ShopImageUrl> shopImageUrlList;


    /**
     * 收藏该门店的用户（即粉丝）提交的对该门店的收藏记录列表； 定义该Shop实体所有关联的UserToShop实体； 指定mappedBy属性表明该Shop实体不控制关联关系
     */
//    @OneToMany(targetEntity = UserToShop.class, mappedBy = "shop")
//    public List<UserToShop> loyalUserRecordList;


    //初始化
    public Shop() {
        Date date = new Date(System.currentTimeMillis());

        setCreateTime(date);//设置注册时间
        setApplyStatus(0);//设置申请状态为申请中
        setOrderSum(0);//根据自己的订单列表（中的已完成）数量进行校正,注册时没有订单，所以为0
        setPoint(0.0);
    }

    /**
     * 校正已完成订单数量 - 根据门店所有已入驻发型师（在入驻后）的已完成订单数量进行校正
     */
    public void regulateOrderSum() {
        int count = 0;
        for (Hairstylist hairstylist : hairstylists) {
            if (hairstylist.getApplyStatus() == 1)
                count += hairstylist.getCompletedOrderSumAfterSettledTime();
        }
        this.orderSum = count;
    }

    public int getOrderSum() {
        regulateOrderSum();
        return orderSum;
    }

    /**
     * 获取门店在本月的完成订单数
     *
     * @return 本月的完成订单数
     */
    public int getCurrentMonthOrderSum() {
        int count = 0;

        //选取已入驻的发型师统计（在入驻后的）月完成订单
        for (Hairstylist hairstylist : hairstylists)
            if (hairstylist.getApplyStatus() == 1)
                count += hairstylist.getCurrentMonthOrderSumAfterSettledTime();
        return count;
    }

    /**
     * 获取门店今天的预约订单数
     *
     * @return 今天的预约订单数
     */
    public int getTodayOrderSum() {
        int todayOrderCount = 0;//今日预约人数

        for (Hairstylist hairstylist : this.hairstylists)
            todayOrderCount += hairstylist.getTodayOrderSum();

        return todayOrderCount;
    }

    public List<Hairstylist> getHairstylists() {
        return getHairstylistsByStatus(1);
    }

    public List<Hairstylist> getHairstylistsByStatus(int status) {
        List<Hairstylist> resultList = new ArrayList<>();
        for (Hairstylist h : this.hairstylists) {
            if (h.getApplyStatus() == status) {
                resultList.add(h);
            }
        }
        return resultList;
    }

    public List<Hairstylist> getHairstylistsByStatusAndBusinessStatus(int status, int businessStatus) {
        List<Hairstylist> resultList = new ArrayList<>();
        for (Hairstylist h : this.hairstylists) {
            if (h.getApplyStatus() == status && h.getBusinessStatus() == businessStatus) {
                resultList.add(h);
            }
        }
        return resultList;
    }

    public Hairstylist isExistHairstylist(int hairstylistId) {
        for (Hairstylist h : hairstylists) {
            if (h.getId() == hairstylistId)
                return h;
        }
        return null;
    }

    public String getMaxPointPerson() {
        List<Hairstylist> hairstylistList = getHairstylistsByStatus(1);
        if (hairstylistList == null || hairstylistList.size() == 0) {
            return null;
        }
        Hairstylist hairstylist = hairstylistList.get(0);
        for (Hairstylist h : hairstylistList) {
            if (hairstylist.getPoint() < h.getPoint()) {
                hairstylist = h;
            }
        }
        return hairstylist.getHairstylistName();
    }

    public String getMaxOrderPerson() {
        List<Hairstylist> hairstylistList = getHairstylistsByStatus(1);
        if (hairstylistList == null || hairstylistList.size() == 0) {
            return null;
        }
        Hairstylist hairstylist = hairstylistList.get(0);
        for (Hairstylist h : hairstylistList) {
            if (hairstylist.getOrderSum() < h.getOrderSum()) {
                hairstylist = h;
            }
        }
        return hairstylist.getHairstylistName();
    }

    public String getMostPopularPerson() {
        List<Hairstylist> hairstylistList = getHairstylistsByStatus(1);
        if (hairstylistList == null || hairstylistList.size() == 0) {
            return null;
        }
        Hairstylist hairstylist = hairstylistList.get(0);
        for (Hairstylist h : hairstylistList) {
            if (hairstylist.loyalUserRecordList.size() < h.loyalUserRecordList.size()) {
                hairstylist = h;
            }
        }
        return hairstylist.getHairstylistName();
    }

    /**
     * 获取门店的所有顾客(根据发型师入驻门店后的已完成订单)
     *
     * @return 门店的所有顾客
     */
    public List<User> getCustomerList() {
        List<Hairstylist> hairstylistList = getHairstylistsByStatus(1);
        List<HaircutOrder> orderList = new ArrayList<>();//获取所有该门店的订单
        List<User> customerList = new ArrayList<>();
        for (Hairstylist h : hairstylistList) {
            orderList.addAll(h.getCompletedOrderListAfterSettledTime());
        }
        for (HaircutOrder o : orderList) {
            if (!customerList.contains(o.user))
                customerList.add(o.user);
        }
        return customerList;
    }

    /**
     * 获取门店的所有忠实顾客（没有根据发型师入驻时间进行判断）
     *
     * @return 门店的所有忠实顾客
     */
    public Set<User> getLoyalCustomerList() {
        List<Hairstylist> hairstylistList = getHairstylistsByStatus(1);
        Set<User> customerList = new HashSet<>();
        for (Hairstylist h : hairstylistList) {
            for (UserToHairstylist record : h.loyalUserRecordList)
                customerList.add(record.user);
        }
        return customerList;
    }


    /**
     * 获取门店的用户分析数据（预约数量、顾客总数、会员总数、男女比例数据预约时间占比信息）
     *
     * @return 门店的用户分析数据
     */
    public Map getCustomerAnalyzeData() {
        Map map = new HashMap();
        try {
            int maleCustomerNum = 0;
            int maleLoyalCustomerNum = 0;
            int femaleCustomerNum = 0;
            int femaleLoyalCustomerNum = 0;
            List<User> customerList = getCustomerList();
            Set<User> loyalCustomerList = getLoyalCustomerList();
            for (User user : customerList) {
                if (user.getSex() == 1) {
                    maleCustomerNum++;
                }
                if (user.getSex() == 2) {
                    maleLoyalCustomerNum++;
                }
            }
            for (User user : loyalCustomerList) {
                if (user.getSex() == 1) {
                    maleLoyalCustomerNum++;
                }
                if (user.getSex() == 2) {
                    femaleLoyalCustomerNum++;
                }
            }
            map.put("maleCustomerNum", maleCustomerNum);
            map.put("femaleCustomerNum", femaleCustomerNum);
            map.put("maleLoyalCustomerNum", maleLoyalCustomerNum);
            map.put("femaleLoyalCustomerNum", femaleLoyalCustomerNum);

            map.put("orderSum", getOrderSum());//根据门店所有已入驻发型师（在入驻后）的已完成订单数量进行校正的
            map.put("customerSum", customerList.size());//根据门店所有已入驻发型师（在入驻后）的已完成订单进行校正的
            map.put("loyalCustomerSum", loyalCustomerList.size());

            map.put("timeRate", getTimeRate());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("/n获取门店内顾客男女比例信息发生错误/n");
            map.put("error", "获取门店内顾客男女比例信息时发生错误!!");
            return map;
        }
        return map;
    }


    /**
     * 获取门店中发型师被预约时间比例
     *
     * @return 门店中发型师被预约时间比例
     */
    public List<Integer> getTimeRate() {
        Map map = new HashMap();
        try {
            List<Hairstylist> hairstylistList = getHairstylistsByStatus(1);
            List<HaircutOrder> orderList = new ArrayList<>();//获取所有该门店的订单
            for (Hairstylist h : hairstylistList) {
                orderList.addAll(h.getCompletedOrderListAfterSettledTime());
            }
            List<Integer> result = new ArrayList<>();
            int[] timeCount = new int[25];
            for (int i = 0; i < 25; i++)
                timeCount[i] = 0;

            for (HaircutOrder order : orderList) {
                int hour = MyUtils.getHour(order.getBookTime());
                timeCount[hour]++;
            }

            for (int i = 9; i <= 22; i++) {
                result.add(timeCount[i]);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("/n获取门店内顾客男女比例信息发生错误/n");
            map.put("error", "获取门店内顾客男女比例信息时发生错误!!");
        }
        return null;
    }

}