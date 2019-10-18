package com.gaocimi.flashpig.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Hairstylist - 发型师类
 *
 * @author xp
 * @date 2019-9-23 03:09:37
 */
@Entity
@Table(name = "hairstylist")
@JsonIgnoreProperties(value = {"haircutOrderList","hairstylistImageUrlList","hairServiceList","recordToUserList","userList","handler","hibernateLazyInitializer","fieldHandler"})
public class Hairstylist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**对应的微信openid*/
    private String openid;

    /**发型师名称*/
    private String hairstylistName;

    /**个人联系电话*/
    private String personalPhone;

    /**个人照片的url*/
    private String personalPhotoUrl;

    /**发型师简介*/
    private String personalProfile;

    /**门店名称*/
    private String shopName;

    /**门店所在省份*/
    private String province;

    /**门店所在城市*/
    private String city;

    /**门店所在区县*/
    private String district;

    /**门店详细地址*/
    private String address;

    /**门店经度*/
    private Double longitude;

    /**门店纬度*/
    private Double latitude;

    /**可预约时间点，用逗号隔开整点时间点，例如（19:00 20:00 21:00）记为（19,20,21）*/
    private String availableTime;

    /**预约须知*/
    private String attention;

    /**用户评分*/
    private Double point;

    /**入驻时间*/
    private Date createTime;

    /**完成订单总数*/
    private Integer orderSum;

    /**竞价排名*/
    private Double rankValue;

    /**发型师申请状态（0表示申请中，1表示申请通过, -1表示申请失败）*/
    private Integer applyStatus;

    /**发型师上传的图片列表； 定义该Hairstylist实体所有关联的HairstylistImageUrl实体； 指定mappedBy属性表明该Hairstylist实体不控制关联关系*/
    @OneToMany(targetEntity = HairstylistImageUrl.class, mappedBy = "hairstylist")
    public List<HairstylistImageUrl> hairstylistImageUrlList;

    /**发型师拥有的服务项列表； 定义该Hairstylist实体所有关联的HairService实体； 指定mappedBy属性表明该Hairstylist实体不控制关联关系*/
    @OneToMany(targetEntity = HairService.class, mappedBy = "hairstylist")
    public List<HairService> hairServiceList;

    /**发型师拥有的订单列表； 定义该Hairstylist实体所有关联的HaircutOrder实体； 指定mappedBy属性表明该Hairstylist实体不控制关联关系*/
    @OneToMany(targetEntity = HaircutOrder.class, mappedBy = "hairstylist")
    public List<HaircutOrder> haircutOrderList;

    /**发型师提交的对顾客的备注列表； 定义该Hairstylist实体所有关联的RecordHairstylisToUser实体； 指定mappedBy属性表明该Hairstylist实体不控制关联关系*/
    @OneToMany(targetEntity = RecordHairstylisToUser.class, mappedBy = "hairstylist")
    public List<RecordHairstylisToUser> recordToUserList;

    /**收藏该发型师的用户列表*/
    @ManyToMany(targetEntity = User.class,mappedBy="hairstylistList",fetch = FetchType.LAZY)
    @JsonIgnore
    public List<User> userList;

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

    public Integer getOrderSum() {
        int count = 0;
        for(HaircutOrder order:haircutOrderList){
            if(order.getStatus()==2)
                count++;
        }
        return count;
    }

    public void setOrderSum(Integer orderSum) {
        this.orderSum = orderSum;
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


//    public List<User> getUserList() {
//        return userList;
//    }
    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}