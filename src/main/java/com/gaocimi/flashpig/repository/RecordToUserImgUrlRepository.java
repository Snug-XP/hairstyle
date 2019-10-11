package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.RecordToUserImgUrl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RecordToUserImgUrlRepository extends JpaRepository<RecordToUserImgUrl, Integer> , JpaSpecificationExecutor<RecordToUserImgUrl> {

    RecordToUserImgUrl findById(int id);

//    List<RecordToUserImgUrl> findAll();

    void deleteById(int id);

    //分页
    public Page<RecordToUserImgUrl> findAll(Pageable pageable);
}