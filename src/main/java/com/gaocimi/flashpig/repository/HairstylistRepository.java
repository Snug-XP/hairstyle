package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.Hairstylist;
import com.gaocimi.flashpig.entity.Hairstylist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

//参考https://www.cnblogs.com/ityouknow/p/5891443.html
public interface HairstylistRepository extends JpaRepository<Hairstylist, Integer>, JpaSpecificationExecutor<Hairstylist> {

    Hairstylist findById(int id);

    Hairstylist findByOpenid(String openid);

    Hairstylist findByPersonalPhone(String phone);

    void deleteById(int id);

    public long countAllByApplyStatus(int status);

    //分页
    public Page<Hairstylist> findAll(Pageable pageable);

    public Page<Hairstylist> findAllByApplyStatus(Integer status,Pageable pageable);

}