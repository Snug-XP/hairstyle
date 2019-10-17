package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.User;
import com.gaocimi.flashpig.entity.UserFormid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFormidRepository extends JpaRepository<UserFormid, Integer> {

    UserFormid findById(int id);

    void deleteById(int id);

}