package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.UserToHairstylist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserToHairstylistRepository extends JpaRepository<UserToHairstylist, Integer>{

    UserToHairstylist findById(int id);
    
    void deleteById(int id);

    //分页
    public Page<UserToHairstylist> findAll(Pageable pageable);
}