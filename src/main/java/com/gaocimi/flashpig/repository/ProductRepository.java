package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {

    Product findById(int id);

    void deleteById(int id);

    List<Product> findAllByTagLike(String tag);

    List<Product> findAllByNameLike(String name);

    List<Product> findAllByTagLikeAndNameLike(String tag, String name);

    //分页
    public Page<Product> findAll(Pageable pageable);
}