package com.peng.zhu.service;

import com.github.pagehelper.PageInfo;
import com.peng.zhu.common.ServerResponse;
import com.peng.zhu.vo.OrderVo;

import java.util.Map;

public interface IOrderService {
    ServerResponse pay(Integer userId, Long orderNo, String path);
    ServerResponse aliCallBack(Map<String,String> params);
    ServerResponse createOrder(Integer userId,Integer shippingId);
    ServerResponse cancleOrder(Integer userId,Long orderNo);
    ServerResponse getOrderCartProduct(Integer userId);
    ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);
    ServerResponse<PageInfo> getOrderList(Integer userId, Integer pagNum, Integer pagSize);
    ServerResponse<PageInfo> getManageOrderList(Integer pagNum,Integer pagSize);
    ServerResponse<OrderVo> getManageOrderDetail(Long orderNo);
    ServerResponse<PageInfo> getManageSearchOrder(Long orderNo,Integer pagNum,Integer pagSize);
    ServerResponse<String> manageSendGoods(Long orderNo);
    //重构新增订单关闭
    void closeOrder(int hour);
}
