package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.UserAddress;
import com.gaocimi.flashpig.repository.UserAddressRepository;
import com.gaocimi.flashpig.service.UserAddressService;
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
 * @date 2020-5-1 12:17:06
 * @description 对数据user_address表进行相关操作的实现类
 */
@Service
public class UserAddressServiceImpl implements UserAddressService {
    protected static final Logger logger = LoggerFactory.getLogger(UserAddressServiceImpl.class);
    @Autowired
    public UserAddressRepository haircutOrderRepository;

    @Override
    public List<UserAddress> getUserAddressList() {
        return haircutOrderRepository.findAll();
    }

    @Override
    public UserAddress findUserAddressById(int id) {
        return haircutOrderRepository.findById(id);
    }


    @Override
    public void save(UserAddress haircutOrder) {
        haircutOrderRepository.save(haircutOrder);
    }

    @Override
    public void edit(UserAddress haircutOrder) {
        haircutOrderRepository.save(haircutOrder);
    }

    @Override
    public void delete(int id) {
        haircutOrderRepository.deleteById(id);
    }


    public List<UserAddress> findAll()
    {
        return haircutOrderRepository.findAll();
    }

    // 分页获得列表
    @Override
    public Page<UserAddress> findAll(int pageNum, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");  //降序
        Pageable pageable = PageRequest.of(pageNum,pageSize,sort);

        Page<UserAddress> haircutOrderPage = null;
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


