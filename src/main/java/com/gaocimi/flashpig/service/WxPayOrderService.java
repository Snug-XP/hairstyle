package com.gaocimi.flashpig.service;

import com.gaocimi.flashpig.entity.WxPayOrder;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author xp
 * @date 2020-2-22 17:14:52
 * @description 对数据库wx_pay_order表进行相关操作
 */
public interface WxPayOrderService {

    public List<WxPayOrder> getWxPayOrderList();

    public List<WxPayOrder> findAllByStatus(Integer status);

    public WxPayOrder findWxPayOrderById(int id);

    public void save(WxPayOrder WxPayOrder);

    public void edit(WxPayOrder WxPayOrder);

    public void delete(int id);

    public Page<WxPayOrder> findAll(int pageNum, int pageSize);
}
