package com.peng.zhu.controller.backend;

import com.peng.zhu.common.Const;
import com.peng.zhu.common.ServerResponse;
import com.peng.zhu.pojo.Category;
import com.peng.zhu.pojo.User;
import com.peng.zhu.service.ICategoryService;
import com.peng.zhu.service.IUserService;
import com.peng.zhu.util.CookieUtil;
import com.peng.zhu.util.JsonUtil;
import com.peng.zhu.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/manage/category/")
public class CategroyManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse<String> addCategroy(HttpServletRequest request, String categroyName, @RequestParam(value="parentId",defaultValue ="0") int parentId){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        String userString = RedisPoolUtil.get(token);
        User user = JsonUtil.str2Object(userString,User.class);
        if(user == null){
            return  ServerResponse.createByErrorMessage("用户未登录!");
        }
        //用户权限校验
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.addCategroy(categroyName,parentId);
        }
         return ServerResponse.createByErrorMessage("用户无权限!,需要管理员权限!");
    }
    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse<String> setCategoryName(HttpServletRequest request,String categroyName,int categroyId){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        String userString = RedisPoolUtil.get(token);
        User user = JsonUtil.str2Object(userString,User.class);
        if(user == null){
            return  ServerResponse.createByErrorMessage("用户未登录!");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.setCategroyName(categroyName,categroyId);
        }
        return  ServerResponse.createByErrorMessage("商品添加失败!");
    }
    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategroy(HttpServletRequest request,@RequestParam(value = "categoryId" ,defaultValue = "0") Integer categoryId){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        String userString = RedisPoolUtil.get(token);
        User user = JsonUtil.str2Object(userString,User.class);
        if(user == null){
            return  ServerResponse.createByErrorMessage("用户未登录!");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.getChildrenParallelCategory(categoryId);
        }
        return ServerResponse.createByErrorMessage("权限不够需要管理员权限!");
    }
    @RequestMapping("get_deep_gategory.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenGategory(HttpServletRequest request,@RequestParam(value="categoryId",defaultValue = "0") Integer categoryId){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录!");
        }
        String userString = RedisPoolUtil.get(token);
        User user = JsonUtil.str2Object(userString,User.class);
        if(user == null){
            return  ServerResponse.createByErrorMessage("用户未登录!");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iCategoryService.getCategoryAndDeepChildrenCategory(categoryId);
        }
        return ServerResponse.createByErrorMessage("权限不够需要管理员权限!");
    }
}
