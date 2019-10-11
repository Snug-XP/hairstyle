package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.HairstylistImageUrl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HairstylistImageUrlRepository extends JpaRepository<HairstylistImageUrl, Integer>{

    HairstylistImageUrl findById(int id);
    
    void deleteById(int id);

    //分页
    public Page<HairstylistImageUrl> findAll(Pageable pageable);
}