package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.UserToHairstylist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserToHairstylistRepository extends JpaRepository<UserToHairstylist, Integer>, JpaSpecificationExecutor<UserToHairstylist> {

    UserToHairstylist findById(int id);

    UserToHairstylist findByUser_IdAndHairstylist_Id(int userId,int hairstylistId);

    void deleteById(int id);

    //分页
    public Page<UserToHairstylist> findAll(Pageable pageable);
}