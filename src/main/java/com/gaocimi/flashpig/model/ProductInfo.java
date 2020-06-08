package com.gaocimi.flashpig.model;

import com.gaocimi.flashpig.entity.Product;
import com.gaocimi.flashpig.entity.ProductImageUrl;
import lombok.Data;

import java.util.List;

@Data
public class ProductInfo {
    private Integer productId;
    private Integer userToProductId;
    private String name;
    private String introduction;
    private String imgUrl;
    private Double price;
    private Integer num;//这个属性用于显示购物车中该商品的数量
    private String[] tag;

    public ProductInfo(Product product) {
        this.productId = product.getId();
        this.name = product.getName();
        this.introduction = product.getIntroduction();
        this.price = product.getPrice();
        this.tag = product.getTag();

        List<ProductImageUrl> imgList = product.getProductImageUrlList();
        if (imgList != null && imgList.size() > 0)
            this.imgUrl = imgList.get(0).getImageUrl();
    }

    public ProductInfo() {
        super();
    }
}
