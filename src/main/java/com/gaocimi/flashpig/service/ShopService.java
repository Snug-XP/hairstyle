package com.gaocimi.flashpig.service;

import com.gaocimi.flashpig.entity.Shop;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author xp
 * @date 2019-11-14 18:49:37
 * @description 对数据库shop表进行相关操作
 */
public interface ShopService {

    public List<Shop> findAll();

    public Shop findShopById(int id);

    public Shop findShopByPhone(String phone);

    public Shop findShopByOpenid(String openid);

    public void save(Shop shop);

    public void edit(Shop shop);

    public void delete(int id);

    public Page<Shop> findAll(int pageNum, int pageSize);

    /**
     * 分页获取获取待审核或审核已通过的门店对象（可选定省、市、县以及门店名的范围）
     *
     * @param pageNum  页数（第几页）
     * @param pageSize 每页大小
     * @return
     */
    public Page<Shop> findRegisterShopList(Integer status, String province, String city, String district, String shopName, int pageNum, int pageSize);

    /**
     * 获取某点的经纬度半径范围内的门店列表
     *
     * @param radius 半径范围
     * @return
     */
    public List<Shop> getShopsByRadius(Double longitude, Double latitude, Double radius);


    /**
     * 根据店名关键字查找相关门店列表
     *
     * @param shopName 门店名关键字
     * @return
     */
    public List<Shop> getShopsByShopNameLike(String shopName);

    Page<Shop> findAllByStatus(int status, int pageNum, int pageSize);

    public long countAllByStatus(int status);
}
