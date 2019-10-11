package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.Administrator;
import com.gaocimi.flashpig.repository.AdministratorRepository;
import com.gaocimi.flashpig.service.AdministratorService;
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
 * @date 2019-10-10 15:29:31
 * @description 对数据库administrator表进行相关操作的实现类
 */
@Service
public class AdministratorServiceImpl implements AdministratorService {
    protected static final Logger logger = LoggerFactory.getLogger(AdministratorServiceImpl.class);

	@Autowired
    public AdministratorRepository administratorRepository;

    @Override
    public List<Administrator> getAdministratorList() {
        return administratorRepository.findAll();
    }

    @Override
    public Administrator findAdministratorById(int id) {
        return administratorRepository.findById(id);
    }
    
    @Override
    public Administrator findAdministratorByOpenid(String openid) {
    	return administratorRepository.findByOpenid(openid);
    }

    @Override
    public void save(Administrator administrator) {
        administratorRepository.save(administrator);
    }

    @Override
    public void edit(Administrator administrator) {
        administratorRepository.save(administrator);
    }

    @Override
    public void delete(int id) {
        administratorRepository.deleteById(id);
    }

     
    public List<Administrator> findAll()
    {
    	return administratorRepository.findAll();
    }

    public boolean isExist(String openid){
        if(administratorRepository.findByOpenid(openid)!=null)
            return true;
        else return false;
    }
    
	// 分页获得列表
	@Override
	public Page<Administrator> findAll(int pageNum, int pageSize) {
		Sort sort = new Sort(Sort.Direction.DESC, "id");  //降序
	    Pageable pageable = PageRequest.of(pageNum,pageSize,sort);
	    
	    Page<Administrator> administratorPage = null;
	    try {
	        administratorPage = administratorRepository.findAll(pageable);
	    } 
	    catch (Exception e) {
	        e.printStackTrace();
            logger.info("查询记录出错");
	        return null;
	    }
	    return administratorPage;
	}
}


