package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.ProductOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer>, JpaSpecificationExecutor<ProductOrder> {

    ProductOrder findById(int id);

    void deleteById(int id);

    //分页
    public Page<ProductOrder> findAll(Pageable pageable);

    public Page<ProductOrder> findAllByStatus(int status, Pageable pageable);

    List<ProductOrder> findAllByStatus(int status);

    public List<ProductOrder> findAllByOrderNumberLike(String orderNumber);
}