package com.gaocimi.flashpig.service;

import com.gaocimi.flashpig.entity.RecordToUserImgUrl;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author xp
 * @date 2019-10-11 19:46:17
 * @description 对数据库record_to_user_img_url表进行相关操作
 */
public interface RecordToUserImgUrlService {

    public List<RecordToUserImgUrl> getRecordToUserImgUrlList();

    public RecordToUserImgUrl findRecordToUserImgUrlById(int id);

    public void save(RecordToUserImgUrl recordToUserImgUrl);

    public void edit(RecordToUserImgUrl recordToUserImgUrl);

    public void delete(int id);

    public Page<RecordToUserImgUrl> findAll(int pageNum, int pageSize);
}
