package com.gaocimi.flashpig.service;

import com.gaocimi.flashpig.entity.HairstylistImageUrl;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author xp
 * @date 2019-9-25 01:26:44
 * @description 对数据库hairstylist_image_url表进行相关操作
 */
public interface HairstylistImageUrlService {

    public List<HairstylistImageUrl> getHairstylistImageUrlList();

    public HairstylistImageUrl findHairstylistImageUrlById(int id);

    public void save(HairstylistImageUrl hairstylistImageUrl);

    public void edit(HairstylistImageUrl hairstylistImageUrl);

    public void delete(int id);

    public Page<HairstylistImageUrl> findAll(int pageNum, int pageSize);
}
