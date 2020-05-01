package com.gaocimi.flashpig.service;

import com.gaocimi.flashpig.entity.UserAddress;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author xp
 * @date 2020-5-1 12:15:42
 * @description 对数据库user_address表进行相关操作
 */
public interface UserAddressService {

    public List<UserAddress> getUserAddressList();

    public UserAddress findUserAddressById(int id);

    public void save(UserAddress UserAddress);

    public void edit(UserAddress UserAddress);

    public void delete(int id);

    public Page<UserAddress> findAll(int pageNum, int pageSize);
}
