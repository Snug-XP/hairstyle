package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.RecordHairstylisToUser;
import com.gaocimi.flashpig.repository.RecordHairstylisToUserRepository;
import com.gaocimi.flashpig.service.RecordHairstylisToUserService;
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
 * @date 2019-9-23 01:29:34
 * @description 对数据库record_hairstylis_to_user表进行相关操作的实现类
 */
@Service
public class RecordHairstylisToUserServiceImpl implements RecordHairstylisToUserService {
    protected static final Logger logger = LoggerFactory.getLogger(RecordHairstylisToUserServiceImpl.class);

    @Autowired
    public RecordHairstylisToUserRepository recordHairstylisToUserRepository;

    @Override
    public List<RecordHairstylisToUser> getRecordHairstylisToUserList() {
        return recordHairstylisToUserRepository.findAll();
    }

    @Override
    public RecordHairstylisToUser findRecordHairstylisToUserById(int id) {
        return recordHairstylisToUserRepository.findById(id);
    }

    @Override
    public void save(RecordHairstylisToUser recordHairstylisToUser) {
        recordHairstylisToUserRepository.save(recordHairstylisToUser);
    }

    @Override
    public void edit(RecordHairstylisToUser recordHairstylisToUser) {
        recordHairstylisToUserRepository.save(recordHairstylisToUser);
    }

    @Override
    public void delete(int id) {
        recordHairstylisToUserRepository.deleteById(id);
    }


    public List<RecordHairstylisToUser> findAll()
    {
        return recordHairstylisToUserRepository.findAll();
    }

    // 分页获得列表
    @Override
    public Page<RecordHairstylisToUser> findAll(int pageNum, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");  //降序
        Pageable pageable = PageRequest.of(pageNum,pageSize,sort);

        Page<RecordHairstylisToUser> recordHairstylisToUserPage = null;
        try {
            recordHairstylisToUserPage = recordHairstylisToUserRepository.findAll(pageable);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.info("查询记录出错");
            return null;
        }
        return recordHairstylisToUserPage;
    }
}


