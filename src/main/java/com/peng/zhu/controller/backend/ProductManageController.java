package com.peng.zhu.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.peng.zhu.common.Const;
import com.peng.zhu.common.ServerResponse;
import com.peng.zhu.pojo.Product;
import com.peng.zhu.pojo.User;
import com.peng.zhu.service.IFileService;
import com.peng.zhu.service.IProductService;
import com.peng.zhu.service.IUserService;
import com.peng.zhu.util.CookieUtil;
import com.peng.zhu.util.JsonUtil;
import com.peng.zhu.util.PropertiesUtil;
import com.peng.zhu.util.RedisPoolUtil;
import com.peng.zhu.vo.ProductDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/manage/product/")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse save(HttpServletRequest request, Product product){
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
            return iProductService.saveOrUpdateProduct(product);
        }
        return ServerResponse.createByErrorMessage("用户无权限，需要管理员权限！");
    }
    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpServletRequest request,int productId,int status){
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
            return iProductService.setSaleStatus(productId,status);
        }
        return ServerResponse.createByErrorMessage("用户无权限，需要管理员权限！");
    }
    @RequestMapping("get_detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getDetail(HttpServletRequest request, Integer productId){
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
           return iProductService.manageProductDetail(productId);
        }
        return ServerResponse.createByErrorMessage("用户无权限，需要管理员权限！");
    }
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getList(HttpServletRequest request, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
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
          return  iProductService.getProductList(pageNum,pageSize);
        }
        return ServerResponse.createByErrorMessage("用户无权限，需要管理员权限！");
    }
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> searchProduct(HttpServletRequest request, String productName,Integer productId ,@RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
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
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }
        return ServerResponse.createByErrorMessage("用户无权限，需要管理员权限！");
    }
    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(@RequestParam(value="upload_file",required = false) MultipartFile file, HttpServletRequest request){
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
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServerResponse.createBySuccess(fileMap);
        }
        return ServerResponse.createByErrorMessage("用户无权限，需要管理员权限！");
    }


    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultMap = Maps.newHashMap();
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            ServerResponse serverResponse = ServerResponse.createByErrorMessage("用户未登录!");
            resultMap.put("msg",serverResponse);
        }
        String userString = RedisPoolUtil.get(token);
        User user = JsonUtil.str2Object(userString,User.class);
        if(user == null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员");
            return resultMap;
        }
        //富文本中对于返回值有自己的要求,我们使用是simditor所以按照simditor的要求进行返回
//        {
//            "success": true/false,
//                "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            if(StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;
        }else{
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
        }
    }
}
