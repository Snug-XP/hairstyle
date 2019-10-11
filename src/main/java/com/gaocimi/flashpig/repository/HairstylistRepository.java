package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.Hairstylist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

//参考https://www.cnblogs.com/ityouknow/p/5891443.html
public interface HairstylistRepository extends JpaRepository<Hairstylist, Integer>{

    Hairstylist findById(int id);

    Hairstylist findByOpenid(String openid);

    void deleteById(int id);

    //分页
    public Page<Hairstylist> findAll(Pageable pageable);
}