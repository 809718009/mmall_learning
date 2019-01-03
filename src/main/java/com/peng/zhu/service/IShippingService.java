package com.peng.zhu.service;

import com.github.pagehelper.PageInfo;
import com.peng.zhu.common.ServerResponse;
import com.peng.zhu.pojo.Shipping;

import java.util.Map;

public interface IShippingService {
    ServerResponse<Map<String,Integer>> add(Integer userId, Shipping shipping);
    ServerResponse<String> delete(Integer userId,Integer shippingId);
    ServerResponse<String> edit(Integer userId,Shipping shipping);
    ServerResponse<Shipping> getShipping(Integer userId,Integer shippingId);
    ServerResponse<PageInfo> getShippingList(Integer userId, Integer pagNum, Integer pagSize);

}
