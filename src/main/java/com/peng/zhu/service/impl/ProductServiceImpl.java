package com.peng.zhu.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.peng.zhu.common.Const;
import com.peng.zhu.common.ResponseCode;
import com.peng.zhu.common.ServerResponse;
import com.peng.zhu.dao.CategoryMapper;
import com.peng.zhu.dao.ProductMapper;
import com.peng.zhu.pojo.Category;
import com.peng.zhu.pojo.Product;
import com.peng.zhu.service.ICategoryService;
import com.peng.zhu.service.IProductService;
import com.peng.zhu.util.DateTimeUtil;
import com.peng.zhu.util.FTPUtil;
import com.peng.zhu.util.PropertiesUtil;
import com.peng.zhu.vo.ProductDetailVo;
import com.peng.zhu.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("iProductService")
public class ProductServiceImpl  implements IProductService {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ICategoryService iCategoryService;

    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse<String> saveOrUpdateProduct(Product product){
        if(product!=null){
            String[] subImages = product.getSubImages().split(",");
            if(subImages.length>0){
               product.setMainImage(subImages[0]);
            }
            if(product.getId()==null){
                int rowCount = productMapper.insert(product);
                if(rowCount>0){
                    return ServerResponse.createBySuccessMessage("添加商品成功!");
                }else{
                    return ServerResponse.createByErrorMessage("添加商品失败!");
                }
            }
            if(product.getId()!=null){
               int rowCount = productMapper.updateByPrimaryKeySelective(product);
                if(rowCount>0){
                    return ServerResponse.createBySuccessMessage("更新商品成功!");
                }else{
                    return ServerResponse.createByErrorMessage("更新商品失败!");
                }
            }
        }
        return ServerResponse.createByErrorMessage("更新或添加商品失败!");
    }

    public ServerResponse<String> setSaleStatus(Integer productId,Integer status){
        if(productId!=null && status!=null){
            Product product = new Product();
            product.setId(productId);
            product.setStatus(status);
            int rowCount = productMapper.updateByPrimaryKeySelective(product);
            if(rowCount>0){
                return ServerResponse.createBySuccessMessage("更新商品销售状态成功!");
            }
            return ServerResponse.createByErrorMessage("更新商品销售状态失败!");
        }
        return ServerResponse.createByErrorMessage("参数错误，更新商品销售状态失败!");
    }

    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if(productId==null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product  product = productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.createByErrorMessage("商品已下架或删除!");
        }
        ServerResponse<ProductDetailVo> pDVoServerResponse = assembleProductDetailVo(product);
        return pDVoServerResponse;

    }
    private ServerResponse<ProductDetailVo> assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setId(product.getId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setSubtitle(product.getSubtitle());
        //获取图片服务器地址
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.zp.com/"));
        //对时间进行转换
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return ServerResponse.createBySuccess(productDetailVo);
    }
    public ServerResponse<PageInfo> getProductList(Integer pagNum,Integer pagSize){
        PageHelper.startPage(pagNum,pagSize);
        List<Product> productList = productMapper.selectProductList();
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem:productList){
            productListVoList.add(assembleProductList(productItem));
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }
    private ProductListVo assembleProductList(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.zp.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }
    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,Integer pagNum,Integer pagSize){
        if(StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
            PageHelper.startPage(pagNum,pagSize);
            List<Product> productList = productMapper.selectProductListByProductNameAndProductId(productName,productId);
            List<ProductListVo> productListVoList = Lists.newArrayList();
            for(Product productItem:productList){
                productListVoList.add(assembleProductList(productItem));
            }
            PageInfo pageInfo = new PageInfo(productList);
            pageInfo.setList(productListVoList);
            return ServerResponse.createBySuccess(pageInfo);
    }
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if(productId==null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product  product = productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.createByErrorMessage("商品已下架或删除!");
        }
        if(product.getStatus()!= Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("商品已下架或删除!");
        }
        ServerResponse<ProductDetailVo> pDVoServerResponse = assembleProductDetailVo(product);
        return pDVoServerResponse;
    }

    public ServerResponse<PageInfo> getProductListByCategoryNameAndIds(String keyWord,
                                                                     Integer categroyId,
                                                                     Integer pagNum, Integer pagSize,String orderBy) {
        if(StringUtils.isBlank(keyWord) && categroyId==null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Category category = categoryMapper.selectByPrimaryKey(categroyId);
        List<Integer> categoryListIds = new ArrayList<Integer>();
        if(category!=null){
            if(StringUtils.isBlank(keyWord) && categroyId==null){
                //没传参数，或者没有此分类!,返回空集合.
                PageHelper.startPage(pagNum,pagSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return  ServerResponse.createBySuccess(pageInfo);
            }
            categoryListIds = iCategoryService.getCategoryAndDeepChildrenCategory(categroyId).getData();
        }
        if(StringUtils.isNotBlank(keyWord)){
            keyWord = new StringBuilder().append("%").append(keyWord).append("%").toString();
        }
        PageHelper.startPage(pagNum,pagSize);
        //排序处理
        if(Const.ProductListOrderBy.PRICE_ASE_DSE.contains(orderBy)){
            String[] orderByArray = orderBy.split("_");
            PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
        }
        List<Product> productList = productMapper.selectProductListByCategoryNameAndIds(StringUtils.isBlank(keyWord)?null:keyWord,categoryListIds.size()==0?null:categoryListIds);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem:productList){
            productListVoList.add(assembleProductList(productItem));
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
