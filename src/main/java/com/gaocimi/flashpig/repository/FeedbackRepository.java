package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    Feedback findById(int id);

    void deleteById(int id);

    //分页
    public Page<Feedback> findAll(Pageable pageable);

}