package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.Album;
import com.gaocimi.flashpig.repository.AlbumRepository;
import com.gaocimi.flashpig.service.AlbumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xp
 * @date 2019-10-24 21:20:17
 * @description 对数据库album表进行相关操作的实现类
 */
@Service
public class AlbumServiceImpl implements AlbumService {
    protected static final Logger logger = LoggerFactory.getLogger(AlbumServiceImpl.class);

    @Autowired
    public AlbumRepository albumRepository;

    @Override
    public List<Album> getAlbumList() {
        return albumRepository.findAll();
    }

    @Override
    public Album findAlbumById(int id) {
        return albumRepository.findById(id);
    }

    @Override
    public void save(Album album) {
        albumRepository.save(album);
    }

    @Override
    public void edit(Album album) {
        albumRepository.save(album);
    }

    @Override
    public void delete(int id) {
        albumRepository.deleteById(id);
    }


    public List<Album> findAll() {
        return albumRepository.findAll();
    }

    // 分页获得列表
    @Override
    public Page<Album> findAll(int pageNum, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");  //降序
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        Page<Album> albumPage = null;
        try {
            albumPage = albumRepository.findAll(pageable);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("查询记录出错");
            return null;
        }
        return albumPage;
    }
}


