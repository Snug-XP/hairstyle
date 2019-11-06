package com.gaocimi.flashpig.service;

import com.gaocimi.flashpig.entity.Tag;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author xp
 * @date 2019-11-6 12:57:36
 * @description 对数据库tag表进行相关操作
 */
public interface TagService {

    public List<Tag> getTagList();

    public Tag findById(int id);

    public Tag findByTagName(String tagName);

    public List<Tag> findByTagNameLike(String tagName);

    public void save(Tag tag);

    public void edit(Tag tag);

    public void delete(int id);

    public Page<Tag> findAll(int pageNum, int pageSize);
}
