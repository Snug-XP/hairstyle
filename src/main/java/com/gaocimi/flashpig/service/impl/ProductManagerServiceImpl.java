package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.ProductManager;
import com.gaocimi.flashpig.repository.ProductManagerRepository;
import com.gaocimi.flashpig.service.ProductManagerService;
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
 * @date 2020-5-16 09:34:14
 * @description 对数据库product_manager表进行相关操作的实现类
 */
@Service
public class ProductManagerServiceImpl implements ProductManagerService {
    protected static final Logger logger = LoggerFactory.getLogger(ProductManagerServiceImpl.class);

	@Autowired
    public ProductManagerRepository productManagerRepository;

    @Override
    public List<ProductManager> getProductManagerList() {
        return productManagerRepository.findAll();
    }

    @Override
    public ProductManager findProductManagerById(int id) {
        return productManagerRepository.findById(id);
    }
    
    @Override
    public ProductManager findProductManagerByOpenid(String openid) {
    	return productManagerRepository.findByOpenid(openid);
    }

    @Override
    public void save(ProductManager productManager) {
        productManagerRepository.save(productManager);
    }

    @Override
    public void edit(ProductManager productManager) {
        productManagerRepository.save(productManager);
    }

    @Override
    public void delete(int id) {
        productManagerRepository.deleteById(id);
    }

    public List<ProductManager> findAll()
    {
    	return productManagerRepository.findAll();
    }

    @Override
    public boolean isExist(String openid){
        if(productManagerRepository.findByOpenid(openid)!=null)
            return true;
        else return false;
    }
    
	// 分页获得列表
	@Override
	public Page<ProductManager> findAll(int pageNum, int pageSize) {
		Sort sort = new Sort(Sort.Direction.DESC, "id");  //降序
	    Pageable pageable = PageRequest.of(pageNum,pageSize,sort);
	    
	    Page<ProductManager> productManagerPage = null;
	    try {
	        productManagerPage = productManagerRepository.findAll(pageable);
	    } 
	    catch (Exception e) {
	        e.printStackTrace();
            logger.info("查询记录出错");
	        return null;
	    }
	    return productManagerPage;
	}
}


