package com.gaocimi.flashpig.service;

import java.util.List;

import com.gaocimi.flashpig.entity.HaircutOrder;
import org.springframework.data.domain.Page;

/**
 * @author xp
 * @date 2019-9-23 01:21:29
 * @description 对数据库haircut_order表进行相关操作
 */
public interface HaircutOrderService {

    public List<HaircutOrder> getHaircutOrderList();

    public HaircutOrder findHaircutOrderById(int id);

    public void save(HaircutOrder haircutOrder);

    public void edit(HaircutOrder haircutOrder);

    public void delete(int id);

    public Page<HaircutOrder> findAll(int pageNum, int pageSize);
}
