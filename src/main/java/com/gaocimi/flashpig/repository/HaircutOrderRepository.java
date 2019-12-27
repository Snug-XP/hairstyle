package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.HaircutOrder;
import com.gaocimi.flashpig.entity.Hairstylist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface HaircutOrderRepository extends JpaRepository<HaircutOrder, Integer>, JpaSpecificationExecutor<HaircutOrder> {

    HaircutOrder findById(int id);

    HaircutOrder findByReservationNum(String reservationNum);

    void deleteById(int id);

    //分页
    public Page<HaircutOrder> findAll(Pageable pageable);

    public List<HaircutOrder> findAllByStatus(Integer status);

}