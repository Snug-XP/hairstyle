package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.HairService;
import com.gaocimi.flashpig.entity.Hairstylist;
import com.gaocimi.flashpig.repository.HairServiceRepository;
import com.gaocimi.flashpig.service.HairServiceService;
import com.gaocimi.flashpig.service.HairstylistService;
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
 * @date 2019-9-23 01:24:31
 * @description 对数据库hair_service表进行相关操作的实现类
 */
@Service
public class HairServiceServiceImpl implements HairServiceService {
    protected static final Logger logger = LoggerFactory.getLogger(HairServiceServiceImpl.class);

    @Autowired
    public HairServiceRepository hairServiceRepository;
    @Autowired
    HairstylistService hairstylistService;

    @Override
    public List<HairService> getHairServiceList() {
        return hairServiceRepository.findAll();
    }

    @Override
    public HairService findHairServiceById(int id) {
        return hairServiceRepository.findById(id);
    }

    @Override
    public void save(HairService hairService) {
        hairServiceRepository.save(hairService);
    }

    @Override
    public void edit(HairService hairService) {
        hairServiceRepository.save(hairService);
    }

    @Override
    public void delete(int id) {
        hairServiceRepository.deleteById(id);
    }


    public List<HairService> findAll()
    {
        return hairServiceRepository.findAll();
    }

    public void deleteAllByHairstylistId(int hairstylistId){
        Hairstylist hairstylist = hairstylistService.findHairstylistById(hairstylistId);
        for(HairService hs : hairstylist.getHairServiceList()){
            hairServiceRepository.deleteById(hs.getId());
        }
    }

    // 分页获得列表
    @Override
    public Page<HairService> findAll(int pageNum, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");  //降序
        Pageable pageable = PageRequest.of(pageNum,pageSize,sort);

        Page<HairService> hairServicePage = null;
        try {
            hairServicePage = hairServiceRepository.findAll(pageable);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.info("查询记录出错");
            return null;
        }
        return hairServicePage;
    }
}


