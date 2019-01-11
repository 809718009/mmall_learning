package com.peng.zhu.controller.backend;

import com.peng.zhu.common.Const;
import com.peng.zhu.common.ServerResponse;
import com.peng.zhu.pojo.User;
import com.peng.zhu.service.IUserService;
import com.peng.zhu.util.CookieUtil;
import com.peng.zhu.util.JsonUtil;
import com.peng.zhu.util.RedisSharedPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/user/")
public class UserManageController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value="login.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpServletResponse servletResponse, HttpSession session){
        ServerResponse<User> response = iUserService.login(username,password);
        if(response.isSuccess()){
            User user = response.getData();
            if(user.getRole().equals(Const.role.ROLE_ADMIN)){
                //session.setAttribute(Const.USERNAME,user);
                CookieUtil.writeLoginToken(servletResponse,session.getId());
                RedisSharedPoolUtil.setEx(session.getId(), JsonUtil.objToStr(response.getData()),Const.RedisCacheExtime.Redis_Session_Extime);
                return  response;
            }
        }else{
            return ServerResponse.createByErrorMessage("不是管理员，无法登录!");
        }
        return  response;
    }
}
