package com.gaocimi.flashpig.service;

import com.gaocimi.flashpig.entity.ProductOrder;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author xp
 * @date 2020-5-2 11:58:47
 * @description 对数据库product_order表进行相关操作
 */
public interface ProductOrderService {

    public List<ProductOrder> getProductOrderList();

    public ProductOrder findById(int id);

    public List<ProductOrder> findByOrderNumberLisk(String orderNumber);

    public void save(ProductOrder productOrder);

    public void edit(ProductOrder productOrder);

    public void delete(int id);

    public Page<ProductOrder> findAll(int pageNum, int pageSize);

    /**
     * 分页获取待审核或者审核通过的文章的发型文章
     *
     * @param pageNum  页数（第几页）
     * @param pageSize 每页大小
     * @return
     */
    public Page<ProductOrder> findAllByStatus(int status, int pageNum, int pageSize);
}
