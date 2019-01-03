package com.peng.zhu.dao;

import com.peng.zhu.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int insertAndGetShippingId(Shipping shipping);

    int deleteShippingByUserIdAndShippingId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    int updateShippingByUserIdAndShippingId(Shipping shipping);

    Shipping selectShipingByUserIdAndShippingId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    List<Shipping> selectShippingListByUserId(Integer userId);
}