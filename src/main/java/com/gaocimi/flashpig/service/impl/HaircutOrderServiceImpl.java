package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.HaircutOrder;
import com.gaocimi.flashpig.repository.HaircutOrderRepository;
import com.gaocimi.flashpig.service.HaircutOrderService;
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
 * @date 2019-9-23 01:23:27
 * @description 对数据库haircut_order表进行相关操作的实现类
 */
@Service
public class HaircutOrderServiceImpl implements HaircutOrderService {
    protected static final Logger logger = LoggerFactory.getLogger(HaircutOrderServiceImpl.class);
    @Autowired
    public HaircutOrderRepository haircutOrderRepository;

    @Override
    public List<HaircutOrder> getHaircutOrderList() {
        return haircutOrderRepository.findAll();
    }
    @Override
    public List<HaircutOrder> findAllByStatus(Integer status) {
        return haircutOrderRepository.findAllByStatus(status);
    }

    @Override
    public HaircutOrder findHaircutOrderById(int id) {
        return haircutOrderRepository.findById(id);
    }

    @Override
    public HaircutOrder findByReservationNum(String reservationNum){
        return haircutOrderRepository.findByReservationNum(reservationNum);
    }

    @Override
    public void save(HaircutOrder haircutOrder) {
        haircutOrderRepository.save(haircutOrder);
    }

    @Override
    public void edit(HaircutOrder haircutOrder) {
        haircutOrderRepository.save(haircutOrder);
    }

    @Override
    public void delete(int id) {
        haircutOrderRepository.deleteById(id);
    }


    public List<HaircutOrder> findAll()
    {
        return haircutOrderRepository.findAll();
    }

    // 分页获得列表
    @Override
    public Page<HaircutOrder> findAll(int pageNum, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");  //降序
        Pageable pageable = PageRequest.of(pageNum,pageSize,sort);

        Page<HaircutOrder> haircutOrderPage = null;
        try {
            haircutOrderPage = haircutOrderRepository.findAll(pageable);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.info("查询记录出错");
            return null;
        }
        return haircutOrderPage;
    }
}


