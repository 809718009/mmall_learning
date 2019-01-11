package com.peng.zhu.dao;

import com.peng.zhu.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectProductList();

    List<Product> selectProductListByProductNameAndProductId(@Param("productName") String productName,
                                                             @Param("productId") Integer productId);
    List<Product> selectProductListByCategoryNameAndIds(@Param("keyWord") String keyWord,@Param("categoryIds") List<Integer> categoryIds);

    Integer selectStockByProducId(int productId);
}