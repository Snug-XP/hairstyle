package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.UserToProduct;
import com.gaocimi.flashpig.repository.UserToProductRepository;
import com.gaocimi.flashpig.service.UserToProductService;
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
 * @date 2020-4-27 15:42:05
 * @description 对数据库user_to_product表进行相关操作的实现类
 */
@Service
public class UserToProductServiceImpl implements UserToProductService {
    protected static final Logger logger = LoggerFactory.getLogger(UserToProductServiceImpl.class);

    @Autowired
    public UserToProductRepository userToProductRepository;

    @Override
    public List<UserToProduct> getUserToProductList() {
        return userToProductRepository.findAll();
    }

    @Override
    public UserToProduct findUserToProductById(int id) {
        return userToProductRepository.findById(id);
    }

    @Override
    public UserToProduct findByUserAndProduct(int userId,int productId){
        return userToProductRepository.findByUser_IdAndProduct_Id(userId,productId);
    }

    @Override
    public void save(UserToProduct userToProduct) {
        userToProductRepository.save(userToProduct);
    }

    @Override
    public void edit(UserToProduct userToProduct) {
        userToProductRepository.save(userToProduct);
    }

    @Override
    public void delete(int id) {
        userToProductRepository.deleteById(id);
    }


    public List<UserToProduct> findAll() {
        return userToProductRepository.findAll();
    }

    // 分页获得列表
    @Override
    public Page<UserToProduct> findAll(int pageNum, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");  //降序
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        Page<UserToProduct> userToProductPage = null;
        try {
            userToProductPage = userToProductRepository.findAll(pageable);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("查询记录出错");
            return null;
        }
        return userToProductPage;
    }
}


