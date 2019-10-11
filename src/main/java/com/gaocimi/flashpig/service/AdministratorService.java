package com.gaocimi.flashpig.service;

import com.gaocimi.flashpig.entity.Administrator;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author xp
 * @date 2019-10-10 15:23:21
 * @description 对数据库administrator表进行相关操作
 */
public interface AdministratorService {

    public List<Administrator> getAdministratorList();

    public Administrator findAdministratorById(int id);

    public Administrator findAdministratorByOpenid(String openid);

    public void save(Administrator administrator);

    public void edit(Administrator administrator);

    public void delete(int id);

    /**
     * 判断管理员是否存在
     * @param openid
     * @return ‘1’表示存在该管理员，‘0’表示不存在该管理员
     */
    public boolean isExist(String openid);

    public Page<Administrator> findAll(int pageNum, int pageSize);
}
