package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.UserToProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserToProductRepository extends JpaRepository<UserToProduct, Integer> , JpaSpecificationExecutor<UserToProduct> {

    UserToProduct findById(int id);

    UserToProduct findByUser_IdAndProduct_Id(int userId, int productId);

    void deleteById(int id);

    //分页
    public Page<UserToProduct> findAll(Pageable pageable);

}