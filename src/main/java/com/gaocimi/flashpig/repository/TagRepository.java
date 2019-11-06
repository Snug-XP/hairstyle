package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Integer>, JpaSpecificationExecutor<Tag> {

    Tag findById(int id);

    public Tag findByTagName(String tagName);

    public List<Tag> findAllByTagNameLike(String tagName);

    void deleteById(int id);
    //分页

    public Page<Tag> findAll(Pageable pageable);
}