package com.gaocimi.flashpig.service.impl;

import com.gaocimi.flashpig.entity.User;
import com.gaocimi.flashpig.repository.UserRepository;
import com.gaocimi.flashpig.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author xp
 * @date 2019-9-23 01:22:37
 * @description 对数据库user表进行相关操作的实现类
 */
@Service
public class UserServiceImpl implements UserService {
    protected static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
    public UserRepository userRepository;

    @Override
    public List<User> getUserList() {
        return userRepository.findAll();
    }

    @Override
    public User findUserById(int id) {
        return userRepository.findById(id);
    }
    
    @Override
    public User findUserByOpenid(String openid) {
    	return userRepository.findByOpenid(openid);
    }

    @Override
    public List<User> findAllByIsVipAndVipEndTimeBefore(Integer isVip , Date time){
        return userRepository.findAllByIsVipAndVipEndTimeBefore(isVip,time);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public void edit(User user) {
        userRepository.save(user);
    }

    @Override
    public void delete(int id) {
        userRepository.deleteById(id);
    }

     
    public List<User> findAll()
    {
    	return userRepository.findAll();
    }
    
	// 分页获得列表
	@Override
	public Page<User> findAll(int pageNum, int pageSize) {
		Sort sort = new Sort(Sort.Direction.DESC, "id");  //降序
	    Pageable pageable = PageRequest.of(pageNum,pageSize,sort);
	    
	    Page<User> userPage = null;
	    try {
	        userPage = userRepository.findAll(pageable);
	    } 
	    catch (Exception e) {
	        e.printStackTrace();
            logger.info("查询记录出错");
	        return null;
	    }
	    return userPage;
	}
}


