package com.peng.zhu.dao;

import com.peng.zhu.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    List<OrderItem> selectOrderItemsByOrderIdAndUserId(@Param("orderNo") Long orderNo, @Param("userId") Integer userId);

    List<OrderItem> selectOrderItemsByOrderNo(@Param("orderNo") Long orderNo);

    int batchInsert(@Param("orderItemList") List<OrderItem> orderItemList);
}