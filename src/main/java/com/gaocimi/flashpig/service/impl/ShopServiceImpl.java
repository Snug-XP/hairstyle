package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.Shop;
import com.gaocimi.flashpig.entity.Shop;
import com.gaocimi.flashpig.repository.ShopRepository;
import com.gaocimi.flashpig.service.ShopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * @author xp
 * @date 2019-11-14 18:54:08
 * @description 对数据库shop表进行相关操作的实现类
 */
@Service
public class ShopServiceImpl implements ShopService {
    protected static final Logger logger = LoggerFactory.getLogger(ShopServiceImpl.class);

    @Autowired
    public ShopRepository shopRepository;

    @Override
    public List<Shop> findAll() {
        return shopRepository.findAll();
    }

    @Override
    public Shop findShopById(int id) {
        return shopRepository.findById(id);
    }

    @Override
    public Shop findShopByPhone(String phone){return shopRepository.findByPhone(phone);}


    @Override
    public Shop findShopByOpenid(String openid) {
        return shopRepository.findByOpenid(openid);
    }

    @Override
    public void save(Shop shop) {
        shopRepository.save(shop);
    }

    @Override
    public void edit(Shop shop) {
        shopRepository.save(shop);
    }

    @Override
    public void delete(int id) {
        shopRepository.deleteById(id);
    }

    // 分页获得列表
    @Override
    public Page<Shop> findAll(int pageNum, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");  //降序
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        Page<Shop> shopPage = null;
        try {
            shopPage = shopRepository.findAll(pageable);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("查询记录出错");
            return null;
        }
        return shopPage;
    }

    /**
     * 分页获取获取待审核或审核已通过的门店对象（可选定省、市、县以及门店名的范围）
     *
     * @param pageNum  页数（第几页）
     * @param pageSize 每页大小
     * @return
     */
    @Override
    public Page<Shop> findRegisterShopList(Integer status, String province, String city,
                                           String district, String shopName, int pageNum, int pageSize) {

        int first = pageNum * pageSize;
        int last = pageNum * pageSize + pageSize - 1;

        List<Shop> shops = null;
        if (province == null && shopName == null)
            shops = shopRepository.findAllByApplyStatus(status);
        else if (province != null) {
            if (shopName != null)
                shops = shopRepository.findAllByApplyStatusAndShopNameLikeAndProvinceAndCityAndDistrict(status, "%" + shopName + "%", province, city, district);
            else
                shops = shopRepository.findAllByApplyStatusAndProvinceAndCityAndDistrict(status, province, city, district);
        } else {
            shops = shopRepository.findAllByApplyStatusAndShopNameLike(status, "%" + shopName + "%");
        }
        List<Shop> resultList = new ArrayList<>();

        for (int i = first; i <= last && i < shops.size(); i++) {
            resultList.add(shops.get(i));
        }

        //包装分页数据
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Shop> page = new PageImpl<>(resultList, pageable, shops.size());

        return page;
    }

    /**
     * 获取某点的经纬度半径范围内的门店列表
     *
     * @param radius 半径范围
     * @return
     */
    @Override
    public List<Shop> getShopsByRadius(Double longitude, Double latitude, Double radius) {
        List<Shop> shops = shopRepository.findAllByLongitudeBetweenAndLatitudeBetween(longitude - radius, longitude + radius, latitude - radius, latitude + radius);
        return shops;
    }


    /**
     * 根据店名关键字查找相关门店列表
     *
     * @param shopName 门店名关键字
     * @return
     */
    @Override
    public List<Shop> getShopsByShopNameLike(String shopName) {
        List<Shop> shops = shopRepository.findAllByShopNameLike("%" + shopName + "%");
        return shops;
    }

    /**
     * 分页获取待审核或者审核通过的文章的发型文章
     *
     * @param pageNum  页数（第几页）
     * @param pageSize 每页大小
     * @return
     */
    @Override
    public Page<Shop> findAllByStatus(int status, int pageNum, int pageSize) {
        int first = pageNum * pageSize;
        int last = pageNum * pageSize + pageSize - 1;

        List<Shop> shops = shopRepository.findAllByApplyStatus(status);
        List<Shop> resultList = new ArrayList<>();

        for (int i = first; i <= last && i < shops.size(); i++) {
            resultList.add(shops.get(i));
        }

        //包装分页数据
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Shop> page = new PageImpl<>(resultList, pageable, shops.size());

        return page;
    }

}


