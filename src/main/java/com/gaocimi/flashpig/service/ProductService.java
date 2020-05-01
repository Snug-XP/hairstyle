package com.gaocimi.flashpig.service;

import com.gaocimi.flashpig.entity.Product;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author xp
 * @date 2020-4-26 14:06:19
 * @description 对数据库product表进行相关操作
 */
public interface ProductService {

    public List<Product> getProductList();

    public Product findProductById(int id);

    public void save(Product product);

    public void edit(Product product);

    public void delete(int id);

    public List<Product> findAllByTagLike(List<String> tagList);

    public List<Product> findAllByNameLike(String name);

    public List<Product> findAllByTagLikeAndNameLike(List<String> tagList, String name);

    public Page<Product> findAll(int pageNum, int pageSize);

}
