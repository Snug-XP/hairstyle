package com.gaocimi.flashpig.service;

import java.util.List;

import com.gaocimi.flashpig.entity.Hairstylist;
import org.springframework.data.domain.Page;

/**
 * @author xp
 * @date 2019-9-23 01:21:45
 * @description 对数据库hairstylist表进行相关操作
 */
public interface HairstylistService {

    public List<Hairstylist> getHairstylistList();

    public Hairstylist findHairstylistById(int id);

    public Hairstylist findHairstylistByOpenid(String openid);

    public void save(Hairstylist hairstylist);

    public void edit(Hairstylist hairstylist);

    public void delete(int id);

    public Page<Hairstylist> findAll(int pageNum, int pageSize);

    //...添加通过status获取发型师


}
