package com.peng.zhu.controller.portal;

import com.github.pagehelper.PageInfo;
import com.peng.zhu.common.ServerResponse;
import com.peng.zhu.service.IProductService;
import com.peng.zhu.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/product/")
public class ProductController {


    @Autowired
    private IProductService iProductService;


    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getDetail(Integer productId){
        return iProductService.getProductDetail(productId) ;
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getProductList(@RequestParam(value = "keyWord",required = false) String keyWord,
                                                   @RequestParam(value = "categoryId",required = false) Integer categoryId,
                                                   @RequestParam(value = "pagNum",defaultValue = "1") Integer pagNum,@RequestParam(value = "pagSize",defaultValue = "10") Integer pagSize,
                                                   @RequestParam(value = "orderBy",defaultValue = "") String orderBy){
        return iProductService.getProductListByCategoryNameAndIds(keyWord,categoryId,pagNum,pagSize,orderBy);
    }
}
