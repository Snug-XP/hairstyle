package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.ProductManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductManagerRepository extends JpaRepository<ProductManager, Integer> {

    ProductManager findById(int id);

    public ProductManager findByOpenid(String openid);

    void deleteById(int id);

    //分页
    public Page<ProductManager> findAll(Pageable pageable);
}