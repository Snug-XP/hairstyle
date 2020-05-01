package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.UserAddress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface UserAddressRepository extends JpaRepository<UserAddress, Integer>, JpaSpecificationExecutor<UserAddress> {

    UserAddress findById(int id);

    void deleteById(int id);

    //分页
    public Page<UserAddress> findAll(Pageable pageable);

}