package com.gaocimi.flashpig.service;

import com.gaocimi.flashpig.entity.ShopImageUrl;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author xp
 * @date 2019-11-17 20:58:48
 * @description 对数据库shop_image_url表进行相关操作
 */
public interface ShopImageUrlService {

    public List<ShopImageUrl> getShopImageUrlList();

    public ShopImageUrl findShopImageUrlById(int id);

    public void save(ShopImageUrl shopImageUrl);

    public void edit(ShopImageUrl shopImageUrl);

    public void delete(int id);

    public Page<ShopImageUrl> findAll(int pageNum, int pageSize);

    ShopImageUrl findByImgUrl(String imgUrl);
}
