package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.UserOrder;
import com.gaocimi.flashpig.repository.UserOrderRepository;
import com.gaocimi.flashpig.service.UserOrderService;
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
 * @description 对数据库user_order表进行相关操作的实现类
 */
@Service
public class UserOrderServiceImpl implements UserOrderService {
    protected static final Logger logger = LoggerFactory.getLogger(UserOrderServiceImpl.class);
    @Autowired
    public UserOrderRepository userOrderRepository;

    @Override
    public List<UserOrder> getUserOrderList() {
        return userOrderRepository.findAll();
    }

    @Override
    public UserOrder findUserOrderById(int id) {
        return userOrderRepository.findById(id);
    }

    @Override
    public void save(UserOrder userOrder) {
        userOrderRepository.save(userOrder);
    }

    @Override
    public void edit(UserOrder userOrder) {
        userOrderRepository.save(userOrder);
    }

    @Override
    public void delete(int id) {
        userOrderRepository.deleteById(id);
    }


    public List<UserOrder> findAll()
    {
        return userOrderRepository.findAll();
    }

    // 分页获得列表
    @Override
    public Page<UserOrder> findAll(int pageNum, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");  //降序
        Pageable pageable = PageRequest.of(pageNum,pageSize,sort);

        Page<UserOrder> userOrderPage = null;
        try {
            userOrderPage = userOrderRepository.findAll(pageable);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.info("查询记录出错");
            return null;
        }
        return userOrderPage;
    }
}


