package com.gaocimi.flashpig.service;

import java.util.List;

import com.gaocimi.flashpig.entity.Hairstylist;
import org.springframework.data.domain.Page;

/**
 * @author xp
 * @date 2019-9-23 01:21:45
 * @description 对数据库hairstylist表进行相关操作
 */
public interface HairstylistService {

    public List<Hairstylist> getHairstylistList();

    public Hairstylist findHairstylistById(int id);

    public Hairstylist findHairstylistByOpenid(String openid);

    public void save(Hairstylist hairstylist);

    public void edit(Hairstylist hairstylist);

    public void delete(int id);

    public Page<Hairstylist> findAll(int pageNum, int pageSize);

    /**
     * 分页获取正在注册的发型师用户
     *
     * @param pageNum  页数（第几页）
     * @param pageSize 每页大小
     * @return
     */
    public Page<Hairstylist> findRegisterList(int pageNum, int pageSize);

    /**
     * 获取某点的经纬度半径范围内的发型师列表
     *
     * @param radius 半径范围
     * @return
     */
    public List<Hairstylist> getHairstylistsByRadius(Double longitude, Double latitude, Double radius);


    /**
     * 获取某点的经纬度半径范围内并且商铺名包含关键字的发型师列表
     *
     * @param shopName 商铺名关键字
     * @param radius 半径范围
     * @return
     */
    public List<Hairstylist> getHairstylistsByRadiusAndShopName(Double longitude, Double latitude, Double radius, String shopName);

}
