package com.peng.zhu.dao;

import com.peng.zhu.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByUserIdAndOrderId(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);

    Order selectByOrderId(Long orderNo);

    List<Order> selectOrderListByUserId(Integer userId);

    List<Order> selectOrderList();

}