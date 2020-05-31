package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.ProductInOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductInOrderRepository extends JpaRepository<ProductInOrder, Integer> , JpaSpecificationExecutor<ProductInOrder> {

    ProductInOrder findById(int id);

    void deleteById(int id);

    //分页
    public Page<ProductInOrder> findAll(Pageable pageable);

}