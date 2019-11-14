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

    Shop findByOpenid(String openid);

    void deleteById(int id);

    //分页
    public Page<Shop> findAll(Pageable pageable);

    public List<Shop> findAllByApplyStatus(Integer status);

    public List<Shop> findAllByApplyStatusAndShopNameLike(Integer status, String shopName);

    public List<Shop> findAllByApplyStatusAndProvinceAndCityAndDistrict(Integer status, String province, String city, String district);

    public List<Shop> findAllByApplyStatusAndShopNameLikeAndProvinceAndCityAndDistrict(Integer status, String shopName,
                                                                                              String province, String city, String district);

    public List<Shop> findAllByLongitudeBetweenAndLatitudeBetween(Double longitudeLow, Double longitudeHigh,
                                                                         Double LatitudeLow, Double LatitudeHigh);

    public List<Shop> findAllByLongitudeBetweenAndLatitudeBetweenAndShopName(Double longitudeLow, Double longitudeHigh,
                                                                                    Double LatitudeLow, Double LatitudeHigh, String shopName);

}