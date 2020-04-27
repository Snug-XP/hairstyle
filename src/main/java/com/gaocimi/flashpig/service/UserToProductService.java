package com.gaocimi.flashpig.service;

import com.gaocimi.flashpig.entity.UserToProduct;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author xp
 * @date 2020-4-27 15:41:07
 * @description 对数据库user_to_product表进行相关操作
 */
public interface UserToProductService {

    public List<UserToProduct> getUserToProductList();

    public UserToProduct findUserToProductById(int id);

    public void save(UserToProduct userToProduct);

    public void edit(UserToProduct userToProduct);

    public void delete(int id);

    public Page<UserToProduct> findAll(int pageNum, int pageSize);

    public UserToProduct findByUserAndProduct(int userId, int productId);
}
