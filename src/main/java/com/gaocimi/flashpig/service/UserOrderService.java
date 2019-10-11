package com.gaocimi.flashpig.service;

import java.util.List;
import com.gaocimi.flashpig.entity.UserOrder;
import org.springframework.data.domain.Page;

/**
 * @author xp
 * @date 2019-9-23 01:21:29
 * @description 对数据库user_order表进行相关操作
 */
public interface UserOrderService {

    public List<UserOrder> getUserOrderList();

    public UserOrder findUserOrderById(int id);

    public void save(UserOrder userOrder);

    public void edit(UserOrder userOrder);

    public void delete(int id);

    public Page<UserOrder> findAll(int pageNum,int pageSize);
}
