package com.gaocimi.flashpig.repository;

import com.gaocimi.flashpig.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

//参考https://www.cnblogs.com/ityouknow/p/5891443.html
public interface ShopRepository extends JpaRepository<Shop, Integer>, JpaSpecificationExecutor<Shop> {

    Shop findById(int id);

    Shop findByPhone(String phone);

    Shop findByOpenid(String openid);

    void deleteById(int id);

    //分页
    public Page<Shop> findAll(Pageable pageable);

    public Page<Shop> findAllByApplyStatus(Integer status , Pageable pageable);

    public List<Shop> findAllByApplyStatusAndShopNameLike(Integer status, String shopName);

    public List<Shop> findAllByApplyStatusAndProvinceAndCityAndDistrict(Integer status, String province, String city, String district);

    public List<Shop> findAllByApplyStatusAndShopNameLikeAndProvinceAndCityAndDistrict(Integer status, String shopName,
                                                                                              String province, String city, String district);

    public List<Shop> findAllByLongitudeBetweenAndLatitudeBetween(Double longitudeLow, Double longitudeHigh,
                                                                         Double LatitudeLow, Double LatitudeHigh);

    public List<Shop> findAllByShopNameLike(String shopName);

    public long countAllByApplyStatus(int status);


}