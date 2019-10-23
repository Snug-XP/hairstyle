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
        Sort sort = new Sort(Sort.Direction.DESC, "id");  //降序
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

    /**
     * 分页获取正在注册的发型师用户
     *
     * @param pageNum  页数（第几页）
     * @param pageSize 每页大小
     * @return
     */
    @Override
    public Page<Hairstylist> findRegisterList(int pageNum, int pageSize) {

        int first = pageNum * pageSize;
        int last = pageNum * pageSize + pageSize - 1;

        List<Hairstylist> hairstylists = hairstylistRepository.findAllByApplyStatus(0);
        List<Hairstylist> resultList = new ArrayList<>();

        for (int i = first; i <= last && i < hairstylists.size(); i++) {
            resultList.add(hairstylists.get(i));
        }

        //包装分页数据
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Hairstylist> page = new PageImpl<>(resultList, pageable, hairstylists.size());

        return page;
    }

    /**
     * 获取某点的经纬度半径范围内的发型师列表
     *
     * @param radius 半径范围
     * @return
     */
    @Override
    public List<Hairstylist> getHairstylistsByRadius(Double longitude,Double latitude, Double radius) {
        List<Hairstylist> hairstylists = hairstylistRepository.findAllByLongitudeBetweenAndLatitudeBetween(longitude-radius,longitude+radius,latitude-radius,latitude+radius);
        return hairstylists;
    }


    /**
     * 获取某点的经纬度半径范围内并且商铺名包含关键字的发型师列表
     *
     * @param shopName 商铺名关键字
     * @param radius 半径范围
     * @return
     */
    @Override
    public List<Hairstylist> getHairstylistsByRadiusAndShopName(Double longitude,Double latitude, Double radius,String shopName) {
        List<Hairstylist> hairstylists = hairstylistRepository.findAllByLongitudeBetweenAndLatitudeBetweenAndShopNameLike(longitude-radius,longitude+radius,latitude-radius,latitude+radius,"%"+shopName+"%");
        return hairstylists;
    }


}


