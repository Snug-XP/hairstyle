package com.gaocimi.flashpig.service;

import com.gaocimi.flashpig.entity.ProductImageUrl;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author xp
 * @date 2020-4-26 14:09:54
 * @description 对数据库product_image_url表进行相关操作
 */
public interface ProductImageUrlService {

    public List<ProductImageUrl> getProductImageUrlList();

    public ProductImageUrl findProductImageUrlById(int id);

    public void save(ProductImageUrl productImageUrl);

    public void edit(ProductImageUrl productImageUrl);

    public void delete(int id);

    public void deleteAllByProductId(int productId);

    public Page<ProductImageUrl> findAll(int pageNum, int pageSize);
}
