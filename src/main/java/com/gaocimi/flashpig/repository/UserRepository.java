package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    User findById(int id);

    public User findByOpenid(String openid);

    void deleteById(int id);

    //分页
    public Page<User> findAll(Pageable pageable);
}