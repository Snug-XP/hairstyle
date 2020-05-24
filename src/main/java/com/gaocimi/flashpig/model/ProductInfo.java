package com.gaocimi.flashpig.model;

import com.gaocimi.flashpig.entity.Product;
import com.gaocimi.flashpig.entity.ProductImageUrl;
import lombok.Data;

import java.util.List;

@Data
public class ProductInfo {
    private Integer productId;
    private String name;
    private String introduction;
    private String imgUrl;
    private String[] tag;

    public ProductInfo(Product product) {
        this.productId = product.getId();
        this.name = product.getName();
        this.introduction = product.getIntroduction();
        this.tag = product.getTag();

        List<ProductImageUrl> imgList = product.getProductImageUrlList();
        if (imgList != null && imgList.size() > 0)
            this.imgUrl = imgList.get(0).getImageUrl();
    }

    public ProductInfo() {
        super();
    }
}
