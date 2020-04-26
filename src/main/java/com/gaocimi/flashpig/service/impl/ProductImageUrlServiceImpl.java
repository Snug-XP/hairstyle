package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.ProductImageUrl;
import com.gaocimi.flashpig.repository.ProductImageUrlRepository;
import com.gaocimi.flashpig.service.ProductImageUrlService;
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
 * @date 2019-10-21 20:16:20
 * @description 对数据库product_image_url表进行相关操作的实现类
 */
@Service
public class ProductImageUrlServiceImpl implements ProductImageUrlService {
    protected static final Logger logger = LoggerFactory.getLogger(ProductImageUrlServiceImpl.class);

    @Autowired
    public ProductImageUrlRepository productImageUrlRepository;

    @Override
    public List<ProductImageUrl> getProductImageUrlList() {
        return productImageUrlRepository.findAll();
    }

    @Override
    public ProductImageUrl findProductImageUrlById(int id) {
        return productImageUrlRepository.findById(id);
    }

    @Override
    public void save(ProductImageUrl productImageUrl) {
        productImageUrlRepository.save(productImageUrl);
    }

    @Override
    public void edit(ProductImageUrl productImageUrl) {
        productImageUrlRepository.save(productImageUrl);
    }

    @Override
    public void delete(int id) {
        productImageUrlRepository.deleteById(id);
    }

    @Override
    public void deleteAllByProductId(int productId){
        productImageUrlRepository.deleteAllByProduct_Id(productId);
    }



    public List<ProductImageUrl> findAll()
    {
        return productImageUrlRepository.findAll();
    }

    // 分页获得列表
    @Override
    public Page<ProductImageUrl> findAll(int pageNum, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");  //降序
        Pageable pageable = PageRequest.of(pageNum,pageSize,sort);

        Page<ProductImageUrl> productImageUrlPage = null;
        try {
            productImageUrlPage = productImageUrlRepository.findAll(pageable);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.info("查询记录出错");
            return null;
        }
        return productImageUrlPage;
    }
}


