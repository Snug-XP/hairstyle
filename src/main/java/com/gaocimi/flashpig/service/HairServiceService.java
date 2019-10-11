package com.gaocimi.flashpig.service;

import java.util.List;
import com.gaocimi.flashpig.entity.HairService;
import org.springframework.data.domain.Page;

/**
 * @author xp
 * @date 2019-9-23 01:26:21
 * @description 对数据库hair_service表进行相关操作
 */
public interface HairServiceService {

    public List<HairService> getHairServiceList();

    public HairService findHairServiceById(int id);

    public void save(HairService hairService);

    public void edit(HairService hairService);

    public void delete(int id);

    public void deleteAllByHairstylistId(int hairstylistId);

    public Page<HairService> findAll(int pageNum,int pageSize);
}
