package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.Administrator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdministratorRepository extends JpaRepository<Administrator, Integer>, JpaSpecificationExecutor<Administrator> {

    Administrator findById(int id);

    public Administrator findByOpenid(String openid);

    void deleteById(int id);

    //分页
    public Page<Administrator> findAll(Pageable pageable);
}