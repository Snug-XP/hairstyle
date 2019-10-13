package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.HairService;
import com.gaocimi.flashpig.entity.Hairstylist;
import com.gaocimi.flashpig.entity.RecordHairstylisToUser;
import com.gaocimi.flashpig.entity.RecordToUserImgUrl;
import com.gaocimi.flashpig.repository.RecordHairstylisToUserRepository;
import com.gaocimi.flashpig.repository.RecordToUserImgUrlRepository;
import com.gaocimi.flashpig.service.RecordToUserImgUrlService;
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
 * @date 2019-10-11 19:48:20
 * @description 对数据库record_to_user_img_url表进行相关操作的实现类
 */
@Service
public class RecordToUserImgUrlServiceImpl implements RecordToUserImgUrlService {
    protected static final Logger logger = LoggerFactory.getLogger(RecordToUserImgUrlServiceImpl.class);

    @Autowired
    public RecordToUserImgUrlRepository recordToUserImgUrlRepository;
    @Autowired
    public RecordHairstylisToUserRepository recordToUserRepository;

    @Override
    public List<RecordToUserImgUrl> getRecordToUserImgUrlList() {
        return recordToUserImgUrlRepository.findAll();
    }

    @Override
    public RecordToUserImgUrl findRecordToUserImgUrlById(int id) {
        return recordToUserImgUrlRepository.findById(id);
    }

    @Override
    public void save(RecordToUserImgUrl recordToUserImgUrl) {
        recordToUserImgUrlRepository.save(recordToUserImgUrl);
    }

    @Override
    public void edit(RecordToUserImgUrl recordToUserImgUrl) {
        recordToUserImgUrlRepository.save(recordToUserImgUrl);
    }

    @Override
    public void delete(int id) {
        recordToUserImgUrlRepository.deleteById(id);
    }

    public void deleteAllByRecordId(int recordId){
        RecordHairstylisToUser record = recordToUserRepository.findById(recordId);
        for(RecordToUserImgUrl recordToUserImgUrl : record.getImageUrlList()){
            recordToUserImgUrlRepository.deleteById(recordToUserImgUrl.getId());
        }
    }


    public List<RecordToUserImgUrl> findAll()
    {
        return recordToUserImgUrlRepository.findAll();
    }

    // 分页获得列表
    @Override
    public Page<RecordToUserImgUrl> findAll(int pageNum, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");  //降序
        Pageable pageable = PageRequest.of(pageNum,pageSize,sort);

        Page<RecordToUserImgUrl> recordToUserImgUrlPage = null;
        try {
            recordToUserImgUrlPage = recordToUserImgUrlRepository.findAll(pageable);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.info("查询记录出错");
            return null;
        }
        return recordToUserImgUrlPage;
    }
}


