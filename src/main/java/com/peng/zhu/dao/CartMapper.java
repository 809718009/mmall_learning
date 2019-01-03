package com.peng.zhu.dao;

import com.peng.zhu.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selcetCartByUserIdandProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    List<Cart> selectCartListByUserId(Integer userId);

    int selectAllCartProductCheckedStatus(Integer userId);

    int deleteCartProductByUserIdAndProductIds(@Param("userId") Integer userId,@Param("productIds") List<String> productIds);

    int updateCheckedByUserIdOrProductId(@Param("userId") Integer userId,@Param("productId") Integer productId,@Param("checked") Integer checked);

    int selectCartProductQuantity(Integer userId);

    List<Cart> selectCheckedCartListByUserId(Integer userId);
}