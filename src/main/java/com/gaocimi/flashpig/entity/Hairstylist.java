package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gaocimi.flashpig.utils.MyUtils;

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
@JsonIgnoreProperties(value = {"loyalUserRecordList","haircutOrderList", "hairstylistImageUrlList", "hairServiceList", "recordToUserList", "userList", "handler", "hibernateLazyInitializer", "fieldHandler"})
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
     * 竞价排名
     */
    private Double rankValue;

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
     * 用户提交的对该发型师的收藏记录列表； 定义该Hairstylist实体所有关联的UserToHairstylist实体； 指定mappedBy属性表明该Hairstylist实体不控制关联关系
     */
    @OneToMany(targetEntity = UserToHairstylist.class, mappedBy = "hairstylist")
    public List<UserToHairstylist> loyalUserRecordList;

    /**
     * 收藏该发型师的用户（即粉丝）列表
     */
    @ManyToMany(targetEntity = User.class, mappedBy = "hairstylistList", fetch = FetchType.LAZY)
    public List<User> loyalUserList;

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

    public void setOrderSum(Integer orderSum) {
        this.orderSum = orderSum;
    }

    public int getOrderSum() {
        return orderSum;
    }

    public Double getRankValue() {
        return rankValue;
    }

    public void setRankValue(Double rankValue) {
        this.rankValue = rankValue;
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

//    不能取消注释，否则序列化时和User造成死循环使得栈溢出
//    public List<User> getLoyalUserList() { return loyalUserList; }

    public void setLoyalUserList(List<User> loyalUserList) {
        this.loyalUserList = loyalUserList;
    }

    public List<UserToHairstylist> getLoyalUserRecordList() {
        return loyalUserRecordList;
    }

    public void setLoyalUserRecordList(List<UserToHairstylist> loyalUserRecordList) {
        this.loyalUserRecordList = loyalUserRecordList;
    }

    /**
     * 获取发型师自己的已完成订单数
     *
     * @return 已完成订单数
     */
    public Integer getCompletedOrderSum() {
        int count = 0;//已完成订单数
        for (HaircutOrder order : haircutOrderList) {
            if (order.getStatus() == 2)
                count++;
        }
        return count;
    }

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

    public Map getAllOperationalData(int day) {
        Map map = new HashMap();
        Map daily = new HashMap();
        Map weekly = new HashMap();
        Map monthly = new HashMap();
        for(int i = 0;i<7;i++){
            daily.put(i,getOperationalData(i,i));
            weekly.put(i,getOperationalData(i*7,i*7+6));
            monthly.put(i,getOperationalData(i*30,i*30+30));
        }
        map.put("daily",daily);
        map.put("weekly",weekly);
        map.put("monthly",monthly);
        return map;
    }

    /**
     * 获取发型师距离今天day1到距离今天day2天(包括day1和day2且day1<=day2)的运营数据（day1=day2=0表示获取今天的运营数据）
     *
     * @param day1 距离今天day1天
     * @param day2 距离今天day2天
     * @return 距离今天day1到距离今天day2天(包括day1和day2)的运营数据
     */
    public Map getOperationalData(int day1,int day2) {
        Map map = new HashMap();
        List<HaircutOrder> orderList = new ArrayList<>();
        int newCustomerNum = 0;

        for (HaircutOrder order : haircutOrderList) {
            Long day = MyUtils.getDifferenceToday(order.getBookTime());//取得预约时间与今天23点59分相差的天数
            if (day1<=day&&day<=day2) {
                orderList.add(order);
            }
        }
        for (HaircutOrder order : orderList) {
            if (isNewCustomer(order.getUser().getId())) {
                newCustomerNum++;
            }
        }

        map.put("reservationNum", orderList.size());
        map.put("newCustomerNum", newCustomerNum);
        map.put("newLoyalCustomerNum", getNewLoyalCustomerNum(day1,day2));

        return map;
    }


    /**
     * 判断用户是不是该发型师的新顾客
     *
     * @param userId 用户id
     * @return
     */
    public boolean isNewCustomer(int userId) {
        for (HaircutOrder order : haircutOrderList) {
            if (userId == order.getUser().getId()) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取发型师距离今天day1到距离今天day2天(包括day1和day2)的新增忠实（粉丝）用户数（day=0表示今天的日报）
     *
     * @param day1 距离今天day1天
     * @param day2 距离今天day2天
     * @return 距离今天day天的新增忠实（粉丝）用户数
     */
    public int getNewLoyalCustomerNum(int day1, int day2) {
        int count = 0;
        for (UserToHairstylist record : loyalUserRecordList) {
            Long day = MyUtils.getDifferenceToday(record.getCreateTime());//取得收藏记录的创建时间与今天23点59分相差的天数
            if(day1<=day&&day<=day2){
                count++;
            }
        }
        return count;
    }


}