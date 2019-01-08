package com.peng.zhu.controller.portal;

import com.peng.zhu.common.Const;
import com.peng.zhu.common.RedisPool;
import com.peng.zhu.common.ServerResponse;
import com.peng.zhu.pojo.User;
import com.peng.zhu.service.IUserService;
import com.peng.zhu.util.CookieUtil;
import com.peng.zhu.util.JsonUtil;
import com.peng.zhu.util.RedisPoolUtil;
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
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value="login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse servletResponse){
        ServerResponse<User> serverResponse = iUserService.login(username,password);
        if(serverResponse.isSuccess()){

            //session.setAttribute(Const.CURRENT_USER,serverResponse.getData());
            CookieUtil.writeLoginToken(servletResponse,session.getId());
            RedisPoolUtil.setEx(session.getId(), JsonUtil.objToStr(serverResponse.getData()),Const.RedisCacheExtime.Redis_Session_Extime);
        }
        return serverResponse;
    }
    @RequestMapping(value="logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest){
        //session.removeAttribute(Const.CURRENT_USER);
        String token = CookieUtil.readLoginToken(httpServletRequest);
        CookieUtil.delLoginToken(httpServletRequest,httpServletResponse);
        RedisPoolUtil.del(token);
        return ServerResponse.createBySuccess();
    }
    @RequestMapping(value="register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }
    @RequestMapping(value="check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String>  checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }
    @RequestMapping(value="get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session,HttpServletRequest request){
        //User user = (User)session.getAttribute(Const.CURRENT_USER);
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        String userString = RedisPoolUtil.get(token);
        User user = JsonUtil.str2Object(userString,User.class);
        return ServerResponse.createBySuccess(user);
    }
    @RequestMapping(value="forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        return iUserService.getQuestion(username);
    }
    @RequestMapping(value="forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }
    @RequestMapping(value="forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username,String newPassword,String forgetToken){
        return iUserService.forgetResetPassword(username,newPassword,forgetToken);
    }
    @RequestMapping(value="reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> restPassword(HttpSession session,String newPassword,String oldPassword){
         User user = (User)session.getAttribute(Const.CURRENT_USER);
         if(user==null){
             return ServerResponse.createByErrorMessage("用户未登录!");
         }
         return iUserService.restPassword(user,newPassword,oldPassword);
    }
    @RequestMapping(value="update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpSession session,User user){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        return iUserService.updateInfomation(user);
    }
    @RequestMapping(value="get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpSession session){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        return iUserService.getInformation(currentUser.getId());
    }
}
