package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.ProductInOrder;
import com.gaocimi.flashpig.repository.ProductInOrderRepository;
import com.gaocimi.flashpig.service.ProductInOrderService;
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
 * @date 2020-5-31 10:52:05
 * @description 对数据库product_in_order表进行相关操作的实现类
 */
@Service
public class ProductInOrderServiceImpl implements ProductInOrderService {
    protected static final Logger logger = LoggerFactory.getLogger(ProductInOrderServiceImpl.class);

    @Autowired
    public ProductInOrderRepository productInOrderRepository;

    @Override
    public ProductInOrder findProductInOrderById(int id) {
        return productInOrderRepository.findById(id);
    }

    @Override
    public void save(ProductInOrder productInOrder) {
        productInOrderRepository.save(productInOrder);
    }

    @Override
    public void edit(ProductInOrder productInOrder) {
        productInOrderRepository.save(productInOrder);
    }

    @Override
    public void delete(int id) {
        productInOrderRepository.deleteById(id);
    }


    public List<ProductInOrder> findAll() {
        return productInOrderRepository.findAll();
    }

    // 分页获得列表
    @Override
    public Page<ProductInOrder> findAll(int pageNum, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");  //降序
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        Page<ProductInOrder> productInOrderPage = null;
        try {
            productInOrderPage = productInOrderRepository.findAll(pageable);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("查询记录出错");
            return null;
        }
        return productInOrderPage;
    }
}


