package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.Product;
import com.gaocimi.flashpig.repository.ProductRepository;
import com.gaocimi.flashpig.service.ProductService;
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
 * @date 2020-4-26 14:11:38
 * @description 对数据库product表进行相关操作的实现类
 */
@Service
public class ProductServiceImpl implements ProductService {
    protected static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    public ProductRepository productRepository;

    @Override
    public List<Product> getProductList() {
        return productRepository.findAll();
    }

    @Override
    public Product findProductById(int id) {
        return productRepository.findById(id);
    }

    @Override
    public void save(Product product) {
        productRepository.save(product);
    }

    @Override
    public void edit(Product product) {
        productRepository.save(product);
    }

    @Override
    public void delete(int id) {
        productRepository.deleteById(id);
    }


    public List<Product> findAll() {
        return productRepository.findAll();
    }

    // 分页获得列表
    @Override
    public Page<Product> findAll(int pageNum, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");  //按创建时间倒序
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

        Page<Product> productPage = null;
        try {
            productPage = productRepository.findAll(pageable);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("查询记录出错");
            return null;
        }
        return productPage;
    }

    @Override
    public List<Product> findAllByTagLikeAndNameLike(List<String> tagList, String name) {
        Set<Product> set = new HashSet<>();//使用集合Set，自带去重功能

        for (String tag : tagList) {
            List<Product> tempList = productRepository.findAllByTagLikeAndNameLike("%" + tag + "%", "%" + name + "%");
            set.addAll(tempList);
        }
        //这样合并去重需要重写对象的equals()方法,但是发现不重写也可以
//            productList.removeAll(tempList);
//            productList.addAll(tempList);

        return new ArrayList<>(set);
    }

    @Override
    public List<Product> findAllByNameLike(String name) {
        return productRepository.findAllByNameLike("%" + name + "%");
    }

    @Override
    public List<Product> findAllByTagLike(List<String> tagList) {
        Set<Product> set = new HashSet<>();//使用集合Set，自带去重功能
        for (String tag : tagList) {
            List<Product> tempList = productRepository.findAllByTagLike("%" + tag + "%");
            set.addAll(tempList);
        }
        //这样合并去重需要重写对象的equals()方法,但是发现不重写也可以
//            productList.removeAll(tempList);
//            productList.addAll(tempList);

        return new ArrayList<>(set);
    }
}


