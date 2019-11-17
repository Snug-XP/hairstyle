package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.ShopImageUrl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopImageUrlRepository extends JpaRepository<ShopImageUrl, Integer>{

    ShopImageUrl findById(int id);
    
    void deleteById(int id);

    //分页
    public Page<ShopImageUrl> findAll(Pageable pageable);

    ShopImageUrl findByImageUrl(String imgUrl);
}