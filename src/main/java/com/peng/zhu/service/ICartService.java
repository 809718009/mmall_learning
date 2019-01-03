package com.peng.zhu.service;

import com.peng.zhu.common.ServerResponse;
import com.peng.zhu.vo.CartVo;

public interface ICartService {
    ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);
    ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count);
    ServerResponse<CartVo> delete(Integer userId,String productIds);
    ServerResponse<CartVo> chooseOrUnChoose(Integer userId,Integer productId,Integer checked);
    ServerResponse<Integer> getCartProductQuantity(Integer userId);
    ServerResponse<CartVo> list(Integer userId);
}
