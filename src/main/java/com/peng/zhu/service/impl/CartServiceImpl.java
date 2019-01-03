package com.peng.zhu.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.peng.zhu.common.Const;
import com.peng.zhu.common.ResponseCode;
import com.peng.zhu.common.ServerResponse;
import com.peng.zhu.dao.CartMapper;
import com.peng.zhu.dao.ProductMapper;
import com.peng.zhu.pojo.Cart;
import com.peng.zhu.pojo.Product;
import com.peng.zhu.service.ICartService;
import com.peng.zhu.util.BigDecimalUtil;
import com.peng.zhu.util.PropertiesUtil;
import com.peng.zhu.vo.CartProductVo;
import com.peng.zhu.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("iCartService")
public class CartServiceImpl implements ICartService {


    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;


    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count){
        if(productId == null || count==null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart userCart = cartMapper.selcetCartByUserIdandProductId(userId,productId);
        if(userCart == null){
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(count);
            cart.setChecked(Const.Cart.CHECKED);
            cartMapper.insert(cart);
        }else{
            Cart updateCountCart = new Cart();
            updateCountCart.setId(userCart.getId());
            updateCountCart.setQuantity(userCart.getQuantity()+count);
            cartMapper.updateByPrimaryKeySelective(updateCountCart);
        }
        return list(userId);
    }
    public ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count){
        if(productId == null || count==null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selcetCartByUserIdandProductId(userId,productId);
        if(cart == null){
            return  ServerResponse.createByErrorMessage("购物车不存在!");
        }
        cart.setQuantity(count);
        cartMapper.updateByPrimaryKeySelective(cart);
        return this.list(userId);
    }


    public ServerResponse<CartVo> delete(Integer userId,String productIds){
       List<String> productIdslist = Splitter.on(",").splitToList(productIds);
       if(CollectionUtils.isNotEmpty(productIdslist)){
           int rowCount = cartMapper.deleteCartProductByUserIdAndProductIds(userId,productIdslist);
           if(rowCount>0){
               return this.list(userId);
           }
       }
        return ServerResponse.createByErrorMessage("购物车删除商品失败!");
    }

    public ServerResponse<CartVo> list(Integer userId){
        CartVo cartVo = getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    public ServerResponse<CartVo> chooseOrUnChoose(Integer userId,Integer productId,Integer checked){
         int rowCount = cartMapper.updateCheckedByUserIdOrProductId(userId,productId,checked);
         if(rowCount>0){
             return this.list(userId);
         }
         return ServerResponse.createByErrorMessage("操作失败!");
    }
    public ServerResponse<Integer> getCartProductQuantity(Integer userId){
        int cartProductQuantity = cartMapper.selectCartProductQuantity(userId);
        return ServerResponse.createBySuccess(cartProductQuantity);
    }

    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartListByUserId(userId);
        List<CartProductVo> cartProductVos = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0");
        if(CollectionUtils.isNotEmpty(cartList)){

            for(Cart cartItem:cartList){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setProductId(cartItem.getProductId());
                cartProductVo.setUserId(cartItem.getUserId());
                //封装产品信息
                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if(product != null){
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductStock(product.getStock());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    //购物车该商品选购数量与该商品库存对比，设置合理的购物数量。
                    int  buyCartProductlimit = 0;
                    if(product.getStock()>=cartItem.getQuantity()){
                        //库存足够
                        buyCartProductlimit = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        //库存不足,更新购物车该商品购买的数量，设置为合理数量！
                        buyCartProductlimit = product.getStock();
                        Cart cartForLimitQuantity = new Cart();
                        cartForLimitQuantity.setId(cartItem.getId());
                        cartForLimitQuantity.setQuantity(product.getStock());
                        cartMapper.updateByPrimaryKeySelective(cartForLimitQuantity);
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                    }
                    cartProductVo.setQuantity(buyCartProductlimit);
                }

                cartProductVo.setProductChecked(cartItem.getChecked());
                cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(cartProductVo.getQuantity().doubleValue(),product.getPrice().doubleValue()));
                cartProductVos.add(cartProductVo);
                if(cartItem.getChecked() == Const.Cart.CHECKED){
                    //如果已经勾选,增加到整个的购物车总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
            }
            cartVo.setCartProductVoList(cartProductVos);
            cartVo.setAllChecked(getAllCheckedStatus(userId));
            cartVo.setCartTotalPrice(cartTotalPrice);
            cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        }
        return  cartVo;
    }

    private Boolean getAllCheckedStatus(Integer userId){
        if(userId == null){
            return false;
        }
        return cartMapper.selectAllCartProductCheckedStatus(userId)==0;
    }

}
