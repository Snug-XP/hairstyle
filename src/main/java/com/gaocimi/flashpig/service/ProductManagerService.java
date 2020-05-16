package com.gaocimi.flashpig.service;

import com.gaocimi.flashpig.entity.ProductManager;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author xp
 * @date 2020-5-16 09:33:18
 * @description 对数据库product_manager表进行相关操作
 */
public interface ProductManagerService {

    public List<ProductManager> getProductManagerList();

    public ProductManager findProductManagerById(int id);

    public ProductManager findProductManagerByOpenid(String openid);

    public void save(ProductManager productManager);

    public void edit(ProductManager productManager);

    public void delete(int id);

    /**
     * 判断管理员是否存在
     * @param openid
     * @return ‘1’表示存在该管理员，‘0’表示不存在该管理员
     */
    public boolean isExist(String openid);

    public Page<ProductManager> findAll(int pageNum, int pageSize);
}
