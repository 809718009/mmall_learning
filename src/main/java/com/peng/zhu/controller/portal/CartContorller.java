package com.peng.zhu.controller.portal;

import com.peng.zhu.common.Const;
import com.peng.zhu.common.ResponseCode;
import com.peng.zhu.common.ServerResponse;
import com.peng.zhu.pojo.User;
import com.peng.zhu.service.ICartService;
import com.peng.zhu.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart/")
public class CartContorller {

    @Autowired
    private ICartService iCartService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpSession session, Integer count, Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.add(user.getId(),productId,count);
    }
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartVo> list(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.list(user.getId());
    }
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpSession session, Integer count, Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.update(user.getId(),productId,count);
    }
    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse<CartVo> deleteCartProduct(HttpSession session, String productIds){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.delete(user.getId(),productIds);
    }
    @RequestMapping("choose_all.do")
    @ResponseBody
    public ServerResponse<CartVo> chooseAll(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.chooseOrUnChoose(user.getId(),null,Const.Cart.CHECKED);
    }
    @RequestMapping("unchoose_all.do")
    @ResponseBody
    public ServerResponse<CartVo> unchooseAll(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return  iCartService.chooseOrUnChoose(user.getId(),null,Const.Cart.UN_CHECKED);
    }
    @RequestMapping("choose.do")
    @ResponseBody
    public ServerResponse<CartVo> choose(HttpSession session,Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return  iCartService.chooseOrUnChoose(user.getId(),productId,Const.Cart.CHECKED);
    }
    @RequestMapping("unchoose.do")
    @ResponseBody
    public ServerResponse<CartVo> unchoose(HttpSession session,Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return  ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.chooseOrUnChoose(user.getId(),productId,Const.Cart.UN_CHECKED);
    }
    @RequestMapping("cart_product_quantity.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductsQuantity(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return  ServerResponse.createBySuccess(0);
        }
         return  iCartService.getCartProductQuantity(user.getId());
    }
}
