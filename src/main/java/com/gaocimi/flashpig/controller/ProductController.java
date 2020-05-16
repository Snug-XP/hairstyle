package com.gaocimi.flashpig.controller;

import com.gaocimi.flashpig.entity.*;
import com.gaocimi.flashpig.model.ProductInfo;
import com.gaocimi.flashpig.result.ResponseResult;
import com.gaocimi.flashpig.service.*;
import com.gaocimi.flashpig.utils.xp.MyUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author xp
 * @date 2020-4-26 14:40:07
 * @description 商品相关业务
 */
@RestController
@ResponseResult
@Api(value = "商品服务相关业务", description = "商品相关业务")
public class ProductController {
    protected static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    ProductService productService;
    @Autowired
    ProductManagerService productManagerService;
    @Autowired
    UserService userService;
    @Autowired
    ProductImageUrlService imageUrlService;
    @Autowired
    AlbumService albumService;
    @Autowired
    UserToProductService userToProductService;

    @ApiOperation(value = "发布商品")
    @PostMapping("/productManager/addProduct")
    public Map addProduct(@RequestParam String myOpenid, @RequestParam String name, @RequestParam String introduction,
                          @RequestParam Double price, @RequestParam Integer remainingQuantity,
                          @RequestParam(value = "tagList", required = false) List<String> tagList,
                          @RequestParam(value = "imgUrlList", required = false) List<String> imgUrlList) {
        Map map = new HashMap();
        try {

            ProductManager productManager = productManagerService.findProductManagerByOpenid(myOpenid);
            if (productManager == null) {
                logger.info("添加商品失败！！（没有权限！！）");
                map.put("error", "无权限！");
                return map;
            }

            Product product = new Product();
            product.setProductManager(productManager);
            product.setTag(tagList);
            product.setName(name);
            product.setIntroduction(introduction);
            product.setPrice(price);
            product.setRemainingQuantity(remainingQuantity);

            productService.save(product);

            //储存商品的图片url列表
            if (imgUrlList != null) {
                for (String imageUrlStr : imgUrlList) {
                    ProductImageUrl imageUrl = new ProductImageUrl();
                    imageUrl.setProduct(product);
                    imageUrl.setImageUrl(imageUrlStr);

                    imageUrlService.save(imageUrl);
                }
            }
            logger.info("商品管理员“" + productManager.getName() + "”（id=" + productManager.getId() + "）发布了商品“" + product.getName() + "”（id=" + product.getId() + "）");
            map.put("message", "商品发布成功！");
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("商品发布失败！！（后端发生某些错误）");
            map.put("error", "商品发布失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "删除商品")
    @DeleteMapping("/productManager/deleteProduct")
    public Map deleteProduct(@RequestParam String myOpenid, @RequestParam Integer productId) {

        Map map = new HashMap();
        try {
            Product product = productService.findProductById(productId);
            if (product == null) {
                logger.info("id为" + productId + "的商品不存在（删除商品）！");
                map.put("error", "该商品不存在！！");
                return map;
            }

            ProductManager productManager = productManagerService.findProductManagerByOpenid(myOpenid);
            if (productManager == null) {
                logger.info("删除商品失败！！（没有权限！！）");
                map.put("error", "无权限！");
                return map;
            }

            logger.info("管理员“" + productManager.getName() + "”（id=" + productManager.getId() + "）删除了商品“" + product.getName() + "”（id=" + product.getId() + "）");

            productService.delete(productId);
            map.put("message", "商品删除成功！");
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("商品删除失败！！（后端发生某些错误）");
            map.put("error", "商品删除失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "修改商品信息", notes = "没有相关属性将不修改其原有信息")
    @PutMapping("/productManager/updateProduct")
    public Map updateProduct(@RequestParam String myOpenid, @RequestParam Integer productId,
                             @RequestParam(value = "name", required = false) String name,
                             @RequestParam(value = "introduction", required = false) String introduction,
                             @RequestParam(value = "price", required = false) Double price,
                             @RequestParam(value = "remainingQuantity", required = false) Integer remainingQuantity,
                             @RequestParam(value = "tagList", required = false) List<String> tagList,
                             @RequestParam(value = "imgUrlList", required = false) List<String> imgUrlList) {
        Map map = new HashMap();
        try {

            Product product = productService.findProductById(productId);

            if (product == null) {
                logger.info("id为" + productId + "的商品不存在（修改商品信息）！");
                map.put("error", "该商品不存在！！");
                return map;
            }

            ProductManager productManager = productManagerService.findProductManagerByOpenid(myOpenid);
            if (productManager == null) {
                logger.info("非管理员用户操作！！（修改商品信息:操作openid=" + myOpenid + "）");
                map.put("error", "无权操作！！");
                return map;
            }

            if (name != null)
                product.setName(name);
            if (introduction != null)
                product.setIntroduction(introduction);
            if (price != null)
                product.setPrice(price);
            if (remainingQuantity != null)
                product.setRemainingQuantity(remainingQuantity);
            if (tagList != null)
                product.setTag(tagList);

            productService.edit(product);

            if (imgUrlList != null && !imgUrlList.isEmpty()) {
                //删除原有商品的图片url
                imageUrlService.deleteAllByProductId(productId);

                //储存商品的图片url列表
                for (String imageUrlStr : imgUrlList) {
                    ProductImageUrl imageUrl = new ProductImageUrl();
                    imageUrl.setProduct(product);
                    imageUrl.setImageUrl(imageUrlStr);

                    imageUrlService.save(imageUrl);
                }
            }
            logger.info("管理员“" + productManager.getName() + "”（id=" + productManager.getId() + "）修改了商品“" + product.getName() + "”（id=" + product.getId() + "）的信息：");
            map.put("message", "商品信息修改成功！");
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("商品信息修改失败！！（后端发生某些错误）");
            map.put("error", "商品信息修改失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


    @ApiOperation(value = "获取自己发布的的商品列表(分页展示)", notes = "权限：发型师(管理员会有一个发型师记录)")
    @GetMapping("/productManager/getMyProductList")
    public Map getMyProductList(@RequestParam String myOpenid,
                                @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {

        Map map = new HashMap();
        try {
            ProductManager productManager = productManagerService.findProductManagerByOpenid(myOpenid);
            if (productManager == null) {
                logger.info("非管理员用户操作！！（获取自己发布的的商品列表:操作openid=" + myOpenid + "）");
                map.put("error", "无权操作！！");
                return map;
            }

            List<Product> tempProductList = productManager.getProductList();

            if (tempProductList == null || tempProductList.isEmpty()) {
                map.put("message", "你还没有创建过商品哦~");
                return map;
            }

            // 按创建时间倒序排序
            Collections.sort(tempProductList, (o1, o2) -> {
                if (o2.getCreateTime().after(o1.getCreateTime())) {
                    return 1;
                } else if (o1.getCreateTime().after(o2.getCreateTime())) {
                    return -1;
                }
                return 0; //相等为0
            });

            map.put("page", MyUtils.getPage(tempProductList, pageNum, pageSize));
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取自己发布的商品列表失败！！（后端发生某些错误）");
            map.put("error", "获取自己发布的商品列表失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


    @ApiOperation(value = "获取单个商品信息", produces = "application/json")
    @GetMapping("/product/getOne")
    public Map getOne(@RequestParam String myOpenid, @RequestParam Integer productId) {
        Map map = new HashMap();
        try {
            User user = userService.findUserByOpenid(myOpenid);
            Product product = productService.findProductById(productId);
            if (user == null) {
                logger.info("（" + myOpenid + "）该用户不存在！(获取单个商品信息)");
                map.put("error", "无效的用户！！");
                return map;
            }
            if (product == null) {
                logger.info("id为" + productId + "的商品不存在！(获取单个商品信息)");
                map.put("error", "该商品不存在！");
                return map;
            }

            if (userToProductService.findByUserAndProduct(user.getId(), productId) != null) {
                map.put("isCollected", "yes");
            } else {
                map.put("isCollected", "no");
            }
            map.put("product", product);
            return map;

        } catch (Exception e) {
            logger.info("后端发生异常(获取单个商品信息)：\n");
            map.put("error", "抱歉，后端发生异常!!");
            e.printStackTrace();
            return map;
        }
    }

    @ApiOperation(value = "获取所有商品列表（分页展示）")
    @GetMapping("/products/getAll")
    public Page<Product> getAllByPage(@RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                      @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
    ) {
        Page<Product> page = productService.findAll(pageNum, pageSize);
        return page;
    }

    @ApiOperation(value = "收藏或取消收藏该商品（转换用户对商品的收藏关系）")
    @PostMapping("/product/addOrRemoveCollection")
    public Map addOrRemoveCollection(@RequestParam String myOpenid, @RequestParam Integer productId) {
        Map map = new HashMap();
        try {
            User user = userService.findUserByOpenid(myOpenid);
            Product product = productService.findProductById(productId);
            if (user == null) {
                logger.info("（" + myOpenid + "）该用户不存在！(收藏或取消收藏该商品)");
                map.put("error", "无效的用户！！");
                return map;
            }
            if (product == null) {
                logger.info("id为" + productId + "的商品不存在！(收藏或取消收藏该商品)");
                map.put("error", "该商品不存在！！");
                return map;
            }
            UserToProduct userToProduct = userToProductService.findByUserAndProduct(user.getId(), productId);
            if (userToProduct != null) {
                userToProductService.delete(userToProduct.getId());
                logger.info("id为" + user.getId() + "的用户“" + user.getName() + "”取消收藏了id为" + product.getId() + "的商品（name：" + product.getName() + "）");
                map.put("message", "取消收藏成功！");
                return map;
            }
            userToProduct = new UserToProduct(user, product);
            userToProductService.save(userToProduct);
            logger.info("id为" + user.getId() + "的用户“" + user.getName() + "”收藏了id为" + product.getId() + "的商品（name：" + product.getName() + "）");
            map.put("message", "收藏成功！");

        } catch (Exception e) {
            logger.info("后端发生异常：\n");
            logger.error(e.getMessage());
            map.put("error", "抱歉，后端发生异常!!");
        }

        return map;
    }


    @ApiOperation(value = "普通用户分页获取自己收藏的商品列表")
    @GetMapping("/product/getMyCollection")
    public Map getMyCollectionByPage(@RequestParam String myOpenid,
                                     @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                     @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {

            User user = userService.findUserByOpenid(myOpenid);
            if (user == null) {
                logger.info("（" + myOpenid + "）该用户不存在！(获取自己收藏的商品列表)");
                map.put("error", "无效的用户！！");
                return map;
            }
            List<UserToProduct> tempProductList = user.getProductRecordList();
            List<Product> resultProductList = new ArrayList<>();

            if (tempProductList == null || tempProductList.isEmpty()) {
                map.put("message", "你还没有收藏商品哦~");
                return map;
            }

            // 按时间倒序排序
            Collections.sort(tempProductList, (o1, o2) -> {
                if (o2.getCreateTime().after(o1.getCreateTime())) {
                    return 1;
                } else if (o1.getCreateTime().after(o2.getCreateTime())) {
                    return -1;
                }
                return 0; //相等为0
            });

            //获取所求页数的商品数据
            int first = pageNum * pageSize;
            int last = pageNum * pageSize + pageSize - 1;
            for (int i = first; i <= last && i < tempProductList.size(); i++) {
                resultProductList.add(tempProductList.get(i).product);
            }

            //包装分页数据
            Pageable pageable = PageRequest.of(pageNum, pageSize);
            Page<Product> page = new PageImpl<>(resultProductList, pageable, tempProductList.size());

            map.put("page", page);
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("获取自己收藏的商品列表失败！！（后端发生某些错误）");
            map.put("error", "获取收藏列表失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }


    @ApiOperation(value = "用户获取相关标签的商品列表")
    @GetMapping("/product/getProductListByTaglist")
    public Map getRecommendList(@RequestParam String myOpenid,
                                @RequestParam List<String> tagList,
                                @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
                                @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Map map = new HashMap();
        try {

            User user = userService.findUserByOpenid(myOpenid);
            if (user == null) {
                logger.info("（" + myOpenid + "）该用户不存在！(用户获取相关标签的商品列表`)");
                map.put("error", "无效的用户！！");
                return map;
            }

            List<Product> tempProductList = productService.findAllByTagLike(tagList);
            List<ProductInfo> resultList = new ArrayList<>();

            if (tempProductList == null || tempProductList.isEmpty()) {
                logger.info("没有找到相关标签的商品（用户获取相关标签的商品）");
                map.put("message", "抱歉，没有找到相关标签的商品~");
                return map;
            }

            // 按创建时间倒序排序
            Collections.sort(tempProductList, (o1, o2) -> {
                if (o2.getCreateTime().after(o1.getCreateTime())) {
                    return 1;
                } else if (o1.getCreateTime().after(o2.getCreateTime())) {
                    return -1;
                }
                return 0; //相等为0
            });


            //获取所求页数的商品数据
            int first = pageNum * pageSize;
            int last = pageNum * pageSize + pageSize - 1;
            for (int i = first; i <= last && i < tempProductList.size(); i++) {
                resultList.add(new ProductInfo(tempProductList.get(i)));
            }

            //包装分页数据
            Pageable pageable = PageRequest.of(pageNum, pageSize);
            Page<ProductInfo> page = new PageImpl<>(resultList, pageable, tempProductList.size());


            map.put("page", page);
            return map;
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.info("用户获取相关标签的商品列表失败！！（后端发生某些错误）");
            map.put("error", "获取相关标签的商品列表失败！！（后端发生某些错误）");
            e.printStackTrace();
            return map;
        }
    }
}
