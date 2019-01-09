package com.peng.zhu.controller.portal;

import com.github.pagehelper.PageInfo;
import com.peng.zhu.common.ResponseCode;
import com.peng.zhu.common.ServerResponse;
import com.peng.zhu.pojo.Shipping;
import com.peng.zhu.pojo.User;
import com.peng.zhu.service.IShippingService;
import com.peng.zhu.util.CookieUtil;
import com.peng.zhu.util.JsonUtil;
import com.peng.zhu.util.RedisSharedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/shipping/")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;


    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<Map<String,Integer>> add(HttpServletRequest request, Shipping shipping){

        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        String userString = RedisSharedPoolUtil.get(token);
        User user = JsonUtil.str2Object(userString,User.class);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return  iShippingService.add(user.getId(),shipping);
    }
    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse<String> delete(HttpServletRequest request, Integer shippingId){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        String userString = RedisSharedPoolUtil.get(token);
        User user = JsonUtil.str2Object(userString,User.class);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return  iShippingService.delete(user.getId(),shippingId);
    }

    @RequestMapping("edit.do")
    @ResponseBody
    public ServerResponse<String> edit(HttpServletRequest request, Shipping shipping){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        String userString = RedisSharedPoolUtil.get(token);
        User user = JsonUtil.str2Object(userString,User.class);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return  iShippingService.edit(user.getId(),shipping);
    }

    @RequestMapping("get.do")
    @ResponseBody
    public ServerResponse<Shipping> get(HttpServletRequest request, Integer shippingId){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        String userString = RedisSharedPoolUtil.get(token);
        User user = JsonUtil.str2Object(userString,User.class);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return  iShippingService.getShipping(user.getId(),shippingId);
    }

    @RequestMapping("get_list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getList(HttpServletRequest request, @RequestParam(value = "pagNum",defaultValue = "1") Integer pagNum,
                                            @RequestParam(value = "pagSize",defaultValue = "10")Integer pagSize){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        String userString = RedisSharedPoolUtil.get(token);
        User user = JsonUtil.str2Object(userString,User.class);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return  iShippingService.getShippingList(user.getId(),pagNum,pagSize);
    }

}
