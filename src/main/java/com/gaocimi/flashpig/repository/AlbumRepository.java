package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, Integer>{

    Album findById(int id);
    
    void deleteById(int id);

    //分页
    public Page<Album> findAll(Pageable pageable);
}