package com.peng.zhu.service;

import com.github.pagehelper.PageInfo;
import com.peng.zhu.common.ServerResponse;
import com.peng.zhu.pojo.Product;
import com.peng.zhu.vo.ProductDetailVo;

public interface IProductService {
    public ServerResponse<String> saveOrUpdateProduct(Product product);
    public ServerResponse<String> setSaleStatus(Integer productId,Integer status);
    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);
    ServerResponse<PageInfo> getProductList(Integer pagNum, Integer pagSize);
    ServerResponse<PageInfo> searchProduct(String productName,Integer productId,Integer pagNum,Integer pagSize);
    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);
    ServerResponse<PageInfo> getProductListByCategoryNameAndIds(String keyWord,
                                                                Integer categroyId,
                                                                Integer pagNum, Integer pagSize,String orderBy);
}
