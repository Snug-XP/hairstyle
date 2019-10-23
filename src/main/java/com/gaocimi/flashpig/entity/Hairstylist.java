package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gaocimi.flashpig.utils.xp.MyUtils;

import javax.persistence.*;
import java.util.*;

/**
 * Hairstylist - 发型师类
 *
 * @author xp
 * @date 2019-9-23 03:09:37
 */
@Entity
@Table(name = "hairstylist")
@JsonIgnoreProperties(value = {"allOperationalData", "loyalUserRecordList", "haircutOrderList", "hairstylistImageUrlList", "hairServiceList", "recordToUserList", "userList", "handler", "hibernateLazyInitializer", "fieldHandler"})
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
     * 门店名称
     */
    private String shopName;

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
     * 门店经度
     */
    private Double longitude;

    /**
     * 门店纬度
     */
    private Double latitude;

    /**
     * 可预约时间点，用逗号隔开整点时间点，例如（19:00 20:00 21:00）记为（19,20,21）
     */
    private String availableTime;

    /**
     * 预约须知
     */
    private String attention;

    /**
     * 用户评分
     */
    private Double point;

    /**
     * 入驻时间
     */
    private Date createTime;

    /**
     * 完成订单总数
     */
    private Integer orderSum;

    /**
     * 发型师申请状态（0表示申请中，1表示申请通过, -1表示申请失败）
     */
    private Integer applyStatus;

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


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getHairstylistName() {
        return hairstylistName;
    }

    public void setHairstylistName(String hairstylistName) {
        this.hairstylistName = hairstylistName;
    }

    public String getPersonalPhone() {
        return personalPhone;
    }

    public void setPersonalPhone(String personalPhone) {
        this.personalPhone = personalPhone;
    }

    public String getPersonalPhotoUrl() {
        return personalPhotoUrl;
    }

    public void setPersonalPhotoUrl(String personalPhotoUrl) {
        this.personalPhotoUrl = personalPhotoUrl;
    }

    public String getPersonalProfile() {
        return personalProfile;
    }

    public void setPersonalProfile(String personalProfile) {
        this.personalProfile = personalProfile;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getAvailableTime() {
        return availableTime;
    }

    public void setAvailableTime(String availableTime) {
        this.availableTime = availableTime;
    }

    public String getAttention() {
        return attention;
    }

    public void setAttention(String attention) {
        this.attention = attention;
    }

    public Double getPoint() {
        return point;
    }

    public void setPoint(Double point) {
        this.point = point;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 根据自己的订单列表（中的已完成）数量进行校正
     */
    public void setOrderSum() {
        int count = 0;
        for (HaircutOrder order : haircutOrderList) {
            if (order.getStatus() == 2) {
                count++;
            }
        }
        this.orderSum = count;
    }

    public Integer getOrderSum() {
        setOrderSum();
        return orderSum;
    }

    public Integer getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(Integer applyStatus) {
        this.applyStatus = applyStatus;
    }

    public List<HairstylistImageUrl> getHairstylistImageUrlList() {
        return hairstylistImageUrlList;
    }

    public void setHairstylistImageUrlList(List<HairstylistImageUrl> hairstylistImageUrlList) {
        this.hairstylistImageUrlList = hairstylistImageUrlList;
    }

    public List<HairService> getHairServiceList() {
        return hairServiceList;
    }

    public void setHairServiceList(List<HairService> hairServiceList) {
        this.hairServiceList = hairServiceList;
    }

    public List<HaircutOrder> getHaircutOrderList() {
        return haircutOrderList;
    }

    public void setHaircutOrderList(List<HaircutOrder> haircutOrderList) {
        this.haircutOrderList = haircutOrderList;
    }

    public List<RecordHairstylisToUser> getRecordToUserList() {
        return recordToUserList;
    }

    public void setRecordToUserList(List<RecordHairstylisToUser> recordToUserList) {
        this.recordToUserList = recordToUserList;
    }

    public List<UserToHairstylist> getLoyalUserRecordList() {
        return loyalUserRecordList;
    }

    public void setLoyalUserRecordList(List<UserToHairstylist> loyalUserRecordList) {
        this.loyalUserRecordList = loyalUserRecordList;
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

        //获取第0~6天（周、月）前的数据
        for (int i = 0; i < 7; i++) {
            daily.put(i + "daysAgo", getOperationalData(MyUtils.stepDay(today, -i), MyUtils.stepDay(today, -i + 1)));
            weekly.put(i + "weeksAgo", getOperationalData(MyUtils.stepWeek(week, -i), MyUtils.stepWeek(week, -i + 1)));
            monthly.put(i + "monthsAgo", getOperationalData(MyUtils.stepMonth(month, -i), MyUtils.stepMonth(month, -i + 1)));
        }
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
            if (o.getStatus() != -2 && o.getUser().getId() == order.getUser().getId()) {
                //找到预约时间最早的相同用户的有效订单（即排除取消的订单）

                if (o.getId() == order.getId())
                    return true;
                else
                    return false;
            }
        }
        //能执行这边的说明传入订单是非有效的（已被取消或被从数据库删除掉的订单）
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


}