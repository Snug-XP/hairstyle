package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.ShopImageUrl;
import com.gaocimi.flashpig.repository.ShopImageUrlRepository;
import com.gaocimi.flashpig.service.ShopImageUrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xp
 * @date 2019-11-17 20:59:24
 * @description 对数据库shop_image_url表进行相关操作的实现类
 */
@Service
public class ShopImageUrlServiceImpl implements ShopImageUrlService {
    protected static final Logger logger = LoggerFactory.getLogger(ShopImageUrlServiceImpl.class);

    @Autowired
    public ShopImageUrlRepository shopImageUrlRepository;

    @Override
    public List<ShopImageUrl> getShopImageUrlList() {
        return shopImageUrlRepository.findAll();
    }

    @Override
    public ShopImageUrl findShopImageUrlById(int id) {
        return shopImageUrlRepository.findById(id);
    }

    @Override
    public ShopImageUrl findByImgUrl(String imgUrl){
        return shopImageUrlRepository.findByImageUrl(imgUrl);
    }

    @Override
    public void save(ShopImageUrl shopImageUrl) {
        shopImageUrlRepository.save(shopImageUrl);
    }

    @Override
    public void edit(ShopImageUrl shopImageUrl) {
        shopImageUrlRepository.save(shopImageUrl);
    }

    @Override
    public void delete(int id) {
        shopImageUrlRepository.deleteById(id);
    }


    public List<ShopImageUrl> findAll()
    {
        return shopImageUrlRepository.findAll();
    }

    // 分页获得列表
    @Override
    public Page<ShopImageUrl> findAll(int pageNum, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");  //降序
        Pageable pageable = PageRequest.of(pageNum,pageSize,sort);

        Page<ShopImageUrl> shopImageUrlPage = null;
        try {
            shopImageUrlPage = shopImageUrlRepository.findAll(pageable);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.info("查询记录出错");
            return null;
        }
        return shopImageUrlPage;
    }
}


