package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.UserOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOrderRepository extends JpaRepository<UserOrder, Integer>{

    UserOrder findById(int id);
    
    void deleteById(int id);

    //分页
    public Page<UserOrder> findAll(Pageable pageable);
}