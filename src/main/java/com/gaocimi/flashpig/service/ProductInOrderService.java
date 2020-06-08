package com.gaocimi.flashpig.service;

import com.gaocimi.flashpig.entity.ProductInOrder;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author xp
 * @date 2020-5-31 10:50:29
 * @description 对数据库product_in_order表进行相关操作
 */
public interface ProductInOrderService {

    public ProductInOrder findProductInOrderById(int id);

    public void save(ProductInOrder productInOrder);

    public void edit(ProductInOrder productInOrder);

    public void delete(int id);

    public Page<ProductInOrder> findAll(int pageNum, int pageSize);
}
