package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.Hairstylist;
import com.gaocimi.flashpig.repository.HairstylistRepository;
import com.gaocimi.flashpig.service.HairstylistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * @author xp
 * @date 2019-9-23 01:23:46
 * @description 对数据库hairstylist表进行相关操作的实现类
 */
@Service
public class HairstylistServiceImpl implements HairstylistService {
    protected static final Logger logger = LoggerFactory.getLogger(HairstylistServiceImpl.class);

    @Autowired
    public HairstylistRepository hairstylistRepository;

    @Override
    public List<Hairstylist> getHairstylistList() {
        return hairstylistRepository.findAll();
    }

    @Override
    public Hairstylist findHairstylistById(int id) {
        return hairstylistRepository.findById(id);
    }

    @Override
    public Hairstylist findHairstylistByOpenid(String openid) {
        return hairstylistRepository.findByOpenid(openid);
    }

    @Override
    public Hairstylist findHairstylistByPhone(String phone) {
        return hairstylistRepository.findByPersonalPhone(phone);
    }

    @Override
    public void save(Hairstylist hairstylist) {
        hairstylistRepository.save(hairstylist);
    }

    @Override
    public void edit(Hairstylist hairstylist) {
        hairstylistRepository.save(hairstylist);
    }

    @Override
    public void delete(int id) {
        hairstylistRepository.deleteById(id);
    }

    // 分页获得列表
    @Override
    public Page<Hairstylist> findAll(int pageNum, int pageSize) {
        Sort sort = new Sort(Sort.Direction.ASC, "id");  //升序
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        Page<Hairstylist> hairstylistPage = null;
        try {
            hairstylistPage = hairstylistRepository.findAll(pageable);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("查询记录出错");
            return null;
        }
        return hairstylistPage;
    }


    @Override
    public Page<Hairstylist> findAllByStatus(int status, int pageNum, int pageSize){

        Sort sort = new Sort(Sort.Direction.ASC, "createTime");  //按申请时间升序
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        return hairstylistRepository.findAllByApplyStatus(status, pageable);
    }

    @Override
    public long countAllByStatus(int status){
        return hairstylistRepository.countAllByApplyStatus(status);
    }
}


