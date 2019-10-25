package com.gaocimi.flashpig.service;

import com.gaocimi.flashpig.entity.Album;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author xp
 * @date 2019-10-24 21:19:12
 * @description 对数据库album表进行相关操作
 */
public interface AlbumService {

    public List<Album> getAlbumList();

    public Album findAlbumById(int id);

    public void save(Album album);

    public void edit(Album album);

    public void delete(int id);

    public Page<Album> findAll(int pageNum, int pageSize);
}
