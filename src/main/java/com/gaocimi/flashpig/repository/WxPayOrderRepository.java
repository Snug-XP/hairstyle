package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.WxPayOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface WxPayOrderRepository extends JpaRepository<WxPayOrder, Integer>, JpaSpecificationExecutor<WxPayOrder> {

    WxPayOrder findById(int id);

    void deleteById(int id);

    //分页
    public Page<WxPayOrder> findAll(Pageable pageable);

    public List<WxPayOrder> findAllByStatus(Integer status);

}