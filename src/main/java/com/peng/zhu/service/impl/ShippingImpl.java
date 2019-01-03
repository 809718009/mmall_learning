package com.peng.zhu.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.peng.zhu.common.ServerResponse;
import com.peng.zhu.dao.ShippingMapper;
import com.peng.zhu.pojo.Shipping;
import com.peng.zhu.service.IShippingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service("iShippingService")
public class ShippingImpl  implements IShippingService {


    @Autowired
    private ShippingMapper shippingMapper;



    public ServerResponse<Map<String,Integer>> add(Integer userId, Shipping shipping){
        if(shipping != null){
            shipping.setUserId(userId);
            int rowCount = shippingMapper.insertAndGetShippingId(shipping);
            if(rowCount>0){
                Map<String,Integer> map = new HashMap<String,Integer>();
                map.put("shippingId",shipping.getId());
                return ServerResponse.createBySuccess("创建新地址成功!",map);
            }
        }
        return  ServerResponse.createByErrorMessage("创建新地址失败!");
    }

    public ServerResponse<String> delete(Integer userId,Integer shippingId){
        if(userId != null && shippingId != null){
            int rowCount = shippingMapper.deleteShippingByUserIdAndShippingId(userId,shippingId);
            if(rowCount>0){
                return  ServerResponse.createBySuccessMessage("删除地址成功!");
            }
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    public ServerResponse<String> edit(Integer userId,Shipping shipping){
        if(userId != null && shipping != null){
            shipping.setUserId(userId);
            int rowCount = shippingMapper.updateShippingByUserIdAndShippingId(shipping);
            if(rowCount>0){
                return  ServerResponse.createBySuccessMessage("更新地址成功!");
            }
        }
        return ServerResponse.createByErrorMessage("更新地址失败!");
    }
    public ServerResponse<Shipping> getShipping(Integer userId,Integer shippingId){
        if(userId != null && shippingId != null){
            Shipping shipping = shippingMapper.selectShipingByUserIdAndShippingId(userId,shippingId);
            if(shipping != null){
                return  ServerResponse.createBySuccess(shipping);
            }
        }
        return ServerResponse.createByErrorMessage("获取地址失败!");
    }


    public ServerResponse<PageInfo> getShippingList(Integer userId,Integer pagNum,Integer pagSize){
        if(userId != null){
            PageHelper.startPage(pagNum,pagSize);
            List<Shipping> shippingList = shippingMapper.selectShippingListByUserId(userId);
            PageInfo pageInfo = new PageInfo(shippingList);
            pageInfo.setList(shippingList);
            return ServerResponse.createBySuccess(pageInfo);
        }
        return ServerResponse.createByErrorMessage("获取该用户所有地址失败!");
    }
}
