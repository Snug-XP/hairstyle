package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.WxPayOrder;
import com.gaocimi.flashpig.repository.WxPayOrderRepository;
import com.gaocimi.flashpig.service.WxPayOrderService;
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
 * @date 2020-2-22 17:17:25
 * @description 对数据库wx_pay_order表进行相关操作的实现类
 */
@Service
public class WxPayOrderServiceImpl implements WxPayOrderService {
    protected static final Logger logger = LoggerFactory.getLogger(WxPayOrderServiceImpl.class);
    @Autowired
    public WxPayOrderRepository haircutOrderRepository;

    @Override
    public List<WxPayOrder> getWxPayOrderList() {
        return haircutOrderRepository.findAll();
    }
    @Override
    public List<WxPayOrder> findAllByStatus(Integer status) {
        return haircutOrderRepository.findAllByStatus(status);
    }

    @Override
    public WxPayOrder findWxPayOrderById(int id) {
        return haircutOrderRepository.findById(id);
    }


    @Override
    public void save(WxPayOrder haircutOrder) {
        haircutOrderRepository.save(haircutOrder);
    }

    @Override
    public void edit(WxPayOrder haircutOrder) {
        haircutOrderRepository.save(haircutOrder);
    }

    @Override
    public void delete(int id) {
        haircutOrderRepository.deleteById(id);
    }


    public List<WxPayOrder> findAll()
    {
        return haircutOrderRepository.findAll();
    }

    // 分页获得列表
    @Override
    public Page<WxPayOrder> findAll(int pageNum, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");  //降序
        Pageable pageable = PageRequest.of(pageNum,pageSize,sort);

        Page<WxPayOrder> haircutOrderPage = null;
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


