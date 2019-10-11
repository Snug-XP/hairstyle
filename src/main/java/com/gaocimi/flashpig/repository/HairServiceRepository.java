package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.HairService;
import com.gaocimi.flashpig.entity.Hairstylist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HairServiceRepository extends JpaRepository<HairService, Integer>{

    HairService findById(int id);
    
    void deleteById(int id);

    List<HairService> findAllByHairstylist(Hairstylist hairstylist);

    //分页
    public Page<HairService> findAll(Pageable pageable);
}