package com.peng.zhu.controller.portal;

import com.peng.zhu.common.Const;
import com.peng.zhu.common.ResponseCode;
import com.peng.zhu.common.ServerResponse;
import com.peng.zhu.pojo.User;
import com.peng.zhu.service.ICartService;
import com.peng.zhu.util.CookieUtil;
import com.peng.zhu.util.JsonUtil;
import com.peng.zhu.util.RedisPoolUtil;
import com.peng.zhu.vo.CartVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart/")
public class CartContorller {

    @Autowired
    private ICartService iCartService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpServletRequest request, Integer count, Integer productId){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        String userString = RedisPoolUtil.get(token);
        User user = JsonUtil.str2Object(userString,User.class);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.add(user.getId(),productId,count);
    }
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartVo> list(HttpServletRequest request){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        String userString = RedisPoolUtil.get(token);
        User user = JsonUtil.str2Object(userString,User.class);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.list(user.getId());
    }
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpServletRequest request, Integer count, Integer productId){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        String userString = RedisPoolUtil.get(token);
        User user = JsonUtil.str2Object(userString,User.class);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.update(user.getId(),productId,count);
    }
    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse<CartVo> deleteCartProduct(HttpServletRequest request, String productIds){
        String deelteToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(deelteToken)){
            return ServerResponse.createByErrorMessage("用户未登录!,请登录。");
        }
        String userString = RedisPoolUtil.get(deelteToken);
        User currentUser = JsonUtil.str2Object(userString,User.class);
        if(currentUser == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.delete(currentUser.getId(),productIds);
    }
    @RequestMapping("choose_all.do")
    @ResponseBody
    public ServerResponse<CartVo> chooseAll(HttpServletRequest request){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        String userString = RedisPoolUtil.get(token);
        User user = JsonUtil.str2Object(userString,User.class);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.chooseOrUnChoose(user.getId(),null,Const.Cart.CHECKED);
    }
    @RequestMapping("unchoose_all.do")
    @ResponseBody
    public ServerResponse<CartVo> unchooseAll(HttpServletRequest request){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        String userString = RedisPoolUtil.get(token);
        User user = JsonUtil.str2Object(userString,User.class);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return  iCartService.chooseOrUnChoose(user.getId(),null,Const.Cart.UN_CHECKED);
    }
    @RequestMapping("choose.do")
    @ResponseBody
    public ServerResponse<CartVo> choose(HttpServletRequest request,Integer productId){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        String userString = RedisPoolUtil.get(token);
        User user = JsonUtil.str2Object(userString,User.class);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return  iCartService.chooseOrUnChoose(user.getId(),productId,Const.Cart.CHECKED);
    }
    @RequestMapping("unchoose.do")
    @ResponseBody
    public ServerResponse<CartVo> unchoose(HttpServletRequest request,Integer productId){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        String userString = RedisPoolUtil.get(token);
        User user = JsonUtil.str2Object(userString,User.class);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.chooseOrUnChoose(user.getId(),productId,Const.Cart.UN_CHECKED);
    }
    @RequestMapping("cart_product_quantity.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductsQuantity(HttpServletRequest request){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        String userString = RedisPoolUtil.get(token);
        User user = JsonUtil.str2Object(userString,User.class);
        if(user == null){
            return  ServerResponse.createBySuccess(0);
        }
         return  iCartService.getCartProductQuantity(user.getId());
    }
}
