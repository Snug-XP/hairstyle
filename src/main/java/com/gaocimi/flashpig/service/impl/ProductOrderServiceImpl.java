package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.ProductOrder;
import com.gaocimi.flashpig.repository.ProductOrderRepository;
import com.gaocimi.flashpig.service.ProductOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author xp
 * @date 2020-5-2 12:05:10
 * @description 对数据库poduct_order表进行相关操作的实现类
 */
@Service
public class ProductOrderServiceImpl implements ProductOrderService {
    protected static final Logger logger = LoggerFactory.getLogger(ProductOrderServiceImpl.class);

    @Autowired
    public ProductOrderRepository productOrderRepository;

    @Override
    public List<ProductOrder> getProductOrderList() {
        return productOrderRepository.findAll();
    }

    @Override
    public ProductOrder findById(int id) {
        return productOrderRepository.findById(id);
    }

    @Override
    public List<ProductOrder> findByOrderNumberLike(String orderNumber) {
        return productOrderRepository.findAllByOrderNumberLike("%"+orderNumber+"%");
    }

    @Override
    public void save(ProductOrder productOrder) {
        productOrderRepository.save(productOrder);
    }

    @Override
    public void edit(ProductOrder productOrder) {
        productOrderRepository.save(productOrder);
    }

    @Override
    public void delete(int id) {
        productOrderRepository.deleteById(id);
    }


    public List<ProductOrder> findAll() {
        return productOrderRepository.findAll();
    }

    // 分页获得列表
    @Override
    public Page<ProductOrder> findAll(int pageNum, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");  //按创建时间倒序
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        Page<ProductOrder> productOrderPage = null;
        try {
            productOrderPage = productOrderRepository.findAll(pageable);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("查询记录出错");
            return null;
        }
        return productOrderPage;
    }

    /**
     * 按订单状态分页获取商品订单列表(按订单创建时间倒序)
     *
     * @param pageNum  页数（第几页）
     * @param pageSize 每页大小
     * @return
     */
    @Override
    public Page<ProductOrder> findAllByStatus(int status, int pageNum, int pageSize) {

        Sort sort = new Sort(Sort.Direction.DESC, "createTime");  //按创建时间倒序
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        Page<ProductOrder> productOrderPage = null;
        try {
            productOrderPage = productOrderRepository.findAllByStatus(status,pageable);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("查询记录出错(按订单状态分页获取商品订单列表)");
            return null;
        }
        return productOrderPage;
    }
}


