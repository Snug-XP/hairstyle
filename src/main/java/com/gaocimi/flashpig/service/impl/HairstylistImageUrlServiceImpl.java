package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.HairstylistImageUrl;
import com.gaocimi.flashpig.repository.HairstylistImageUrlRepository;
import com.gaocimi.flashpig.service.HairstylistImageUrlService;
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
 * @date 2019-9-25 01:26:50
 * @description 对数据库hairstylist_image_url表进行相关操作的实现类
 */
@Service
public class HairstylistImageUrlServiceImpl implements HairstylistImageUrlService {
    protected static final Logger logger = LoggerFactory.getLogger(HairstylistImageUrlServiceImpl.class);

    @Autowired
    public HairstylistImageUrlRepository hairstylistImageUrlRepository;

    @Override
    public List<HairstylistImageUrl> getHairstylistImageUrlList() {
        return hairstylistImageUrlRepository.findAll();
    }

    @Override
    public HairstylistImageUrl findHairstylistImageUrlById(int id) {
        return hairstylistImageUrlRepository.findById(id);
    }

    @Override
    public void save(HairstylistImageUrl hairstylistImageUrl) {
        hairstylistImageUrlRepository.save(hairstylistImageUrl);
    }

    @Override
    public void edit(HairstylistImageUrl hairstylistImageUrl) {
        hairstylistImageUrlRepository.save(hairstylistImageUrl);
    }

    @Override
    public void delete(int id) {
        hairstylistImageUrlRepository.deleteById(id);
    }


    public List<HairstylistImageUrl> findAll()
    {
        return hairstylistImageUrlRepository.findAll();
    }

    // 分页获得列表
    @Override
    public Page<HairstylistImageUrl> findAll(int pageNum, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");  //降序
        Pageable pageable = PageRequest.of(pageNum,pageSize,sort);

        Page<HairstylistImageUrl> hairstylistImageUrlPage = null;
        try {
            hairstylistImageUrlPage = hairstylistImageUrlRepository.findAll(pageable);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.info("查询记录出错");
            return null;
        }
        return hairstylistImageUrlPage;
    }
}


