package com.gaocimi.flashpig.service;

import java.util.List;
import com.gaocimi.flashpig.entity.RecordHairstylisToUser;
import org.springframework.data.domain.Page;

/**
 * @author xp
 * @date 2019-9-23 01:28:42
 * @description 对数据库record_hairstylis_to_user表进行相关操作
 */
public interface RecordHairstylisToUserService {

    public List<RecordHairstylisToUser> getRecordHairstylisToUserList();

    public RecordHairstylisToUser findRecordHairstylisToUserById(int id);

    public void save(RecordHairstylisToUser recordHairstylisToUser);

    public void edit(RecordHairstylisToUser recordHairstylisToUser);

    public void delete(int id);

    public Page<RecordHairstylisToUser> findAll(int pageNum,int pageSize);
}
