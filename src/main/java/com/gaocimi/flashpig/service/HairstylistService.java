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

    public Hairstylist findHairstylistByPhone(String phone);

    public void save(Hairstylist hairstylist);

    public void edit(Hairstylist hairstylist);

    public void delete(int id);

    public Page<Hairstylist> findAll(int pageNum, int pageSize);

    public Page<Hairstylist> findAllByStatus(int status, int pageNum, int pageSize);

    public long countAllByStatus(int status);


}
