package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.ProductImageUrl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ProductImageUrlRepository extends JpaRepository<ProductImageUrl, Integer>{

    ProductImageUrl findById(int id);
    
    void deleteById(int id);

    //分页
    public Page<ProductImageUrl> findAll(Pageable pageable);

    /**
     * 删除该商品的所有图片url
     * @param productId 文章id
     */
    //添加事务，不加的话会报错，目前不太了解事务的机制...
    @Transactional
    public void deleteAllByProduct_Id(int productId);
}