package com.peng.zhu.controller.portal;

import com.peng.zhu.common.Const;
import com.peng.zhu.common.ServerResponse;
import com.peng.zhu.pojo.User;
import com.peng.zhu.service.IUserService;
import com.peng.zhu.util.CookieUtil;
import com.peng.zhu.util.JsonUtil;
import com.peng.zhu.util.RedisSharedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/spring_session/")
public class UserSpringSessionController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value="login.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse servletResponse){
        ServerResponse<User> serverResponse = iUserService.login(username,password);
        if(serverResponse.isSuccess()){

            session.setAttribute(Const.CURRENT_USER,serverResponse.getData());
            /*CookieUtil.writeLoginToken(servletResponse,session.getId());
            RedisSharedPoolUtil.setEx(session.getId(), JsonUtil.objToStr(serverResponse.getData()),Const.RedisCacheExtime.Redis_Session_Extime);*/

        }
        return serverResponse;
    }
    @RequestMapping(value="logout.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest){
          session.removeAttribute(Const.CURRENT_USER);
      /*  String token = CookieUtil.readLoginToken(httpServletRequest);
        CookieUtil.delLoginToken(httpServletRequest,httpServletResponse);
        RedisSharedPoolUtil.del(token);*/
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value="get_user_info.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session,HttpServletRequest request){
        /*String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        String userString = RedisSharedPoolUtil.get(token);
        User user = JsonUtil.str2Object(userString,User.class);*/
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess(user);
    }

}
