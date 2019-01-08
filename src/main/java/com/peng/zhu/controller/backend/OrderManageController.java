package com.peng.zhu.controller.backend;

import com.github.pagehelper.PageInfo;
import com.peng.zhu.common.Const;
import com.peng.zhu.common.ServerResponse;
import com.peng.zhu.pojo.User;
import com.peng.zhu.service.IOrderService;
import com.peng.zhu.service.IUserService;
import com.peng.zhu.util.CookieUtil;
import com.peng.zhu.util.JsonUtil;
import com.peng.zhu.util.RedisPoolUtil;
import com.peng.zhu.vo.OrderVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/order/")
public class OrderManageController {


    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderService iOrderService;



    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpServletRequest request, @RequestParam(value = "pagNum", defaultValue = "1") Integer pagNum, @RequestParam(value = "pagSize", defaultValue = "10") Integer pagSize) {
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        String userString = RedisPoolUtil.get(token);
        User user = JsonUtil.str2Object(userString,User.class);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //填充业务逻辑
            return iOrderService.getManageOrderList(pagNum,pagSize);
        }
        return ServerResponse.createByErrorMessage("权限不够需要管理员权限!");

    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> detail(HttpServletRequest request, Long orderNo) {
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        String userString = RedisPoolUtil.get(token);
        User user = JsonUtil.str2Object(userString,User.class);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //填充业务逻辑
            return iOrderService.getManageOrderDetail(orderNo);
        }
        return ServerResponse.createByErrorMessage("权限不够需要管理员权限!");

    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> search(HttpServletRequest request, Long orderNo,@RequestParam(value = "pagNum", defaultValue = "1") Integer pagNum, @RequestParam(value = "pagSize", defaultValue = "10") Integer pagSize) {
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        String userString = RedisPoolUtil.get(token);
        User user = JsonUtil.str2Object(userString,User.class);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //填充业务逻辑
            return iOrderService.getManageSearchOrder(orderNo,pagNum,pagSize);
        }
        return ServerResponse.createByErrorMessage("权限不够需要管理员权限!");

    }

    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse<String> sendGoods(HttpServletRequest request, Long orderNo) {
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        String userString = RedisPoolUtil.get(token);
        User user = JsonUtil.str2Object(userString,User.class);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //填充业务逻辑
            return iOrderService.manageSendGoods(orderNo);
        }
        return ServerResponse.createByErrorMessage("权限不够需要管理员权限!");

    }



















}

