package com.peng.zhu.service.impl;

import com.peng.zhu.common.Const;
import com.peng.zhu.common.ServerResponse;
import com.peng.zhu.dao.UserMapper;
import com.peng.zhu.pojo.User;
import com.peng.zhu.service.IUserService;
import com.peng.zhu.util.MD5Util;
import com.peng.zhu.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl  implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int reslutCount = userMapper.checkUser(username);
        if(reslutCount == 0){
            return ServerResponse.createByErrorMessage("用户不存在！");
        }
        //todo
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectBylogin(username,md5Password);
        if(user==null){
            return ServerResponse.createByErrorMessage("密码错误!");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功！",user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse<String> validResponse = checkValid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse=this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        user.setRole(Const.role.ROLE_CUSTOM);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int reslut = userMapper.insert(user);
        if(reslut >0){
            return ServerResponse.createBySuccessMessage("注册成功！");
        }
        return ServerResponse.createByErrorMessage("注册失败！");
    }
    public ServerResponse<String> checkValid(String str,String type){
        if(StringUtils.isNotBlank(type)){
            if(type.equals(Const.USERNAME)){
                int resultCount = userMapper.checkUser(str);
                if(resultCount>0)
                    return ServerResponse.createByErrorMessage("用户已经存在!");
            }
            if(type.equals(Const.EMAIL)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount>0)
                    return ServerResponse.createByErrorMessage("email已存在!");
            }
        }else{
            return ServerResponse.createByErrorMessage("参数错误!");
        }
        return ServerResponse.createBySuccessMessage("校验成功!");
    }
    public ServerResponse<String> getQuestion(String username){
        ServerResponse<String> validResponse = checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在!");
        }
        String  question = userMapper.selectQuestion(username);
        if(StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccessMessage(question);
        }
        return ServerResponse.createByErrorMessage("问题不存在是空的!");
    }
    public ServerResponse<String> checkAnswer(String username,String question,String answer)
    {
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount>0){
            String forgetToken = UUID.randomUUID().toString();
            //TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            RedisPoolUtil.setEx(Const.TOKEN_PREFIX+username,forgetToken,60*60*12);
            return ServerResponse.createBySuccessMessage(forgetToken);
        }
        return ServerResponse.createByErrorMessage("问题答案错误!");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String newPassword, String forgetToken) {
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("forgetToken参数为空");
        }
        ServerResponse<String> validResponse = checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在!");
        }
        String token = RedisPoolUtil.get(Const.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token过期无效");
        }
        if(StringUtils.equals(token,forgetToken)){
           String md5Password = MD5Util.MD5EncodeUtf8(newPassword);
           int rowCount = userMapper.updatePasswordByUsername(username,md5Password);
           if(rowCount>0){
               return ServerResponse.createBySuccessMessage("密码修改成功!");
           }
        }else{
            return ServerResponse.createBySuccessMessage("token错误，请重新获取重置密码的token!");
        }
        return ServerResponse.createByErrorMessage("重置密码失败!");
    }

    @Override
    public ServerResponse<String> restPassword(User user, String newPassword, String oldPassword) {
        int resultCount = userMapper.selectPassword(MD5Util.MD5EncodeUtf8(oldPassword),user.getId());
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("密码错误!");
        }
        String md5Password = MD5Util.MD5EncodeUtf8(newPassword);
        user.setPassword(md5Password);
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0){
           return ServerResponse.createBySuccessMessage("密码修改成功!");
        }
        return ServerResponse.createByErrorMessage("密码修改失败!");
    }

    @Override
    public ServerResponse<User> updateInfomation(User user) {
        //校验email是否存在,防止横向越权。
        int resultCheckEmail = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCheckEmail>0){
            return ServerResponse.createByErrorMessage("email已经存在!");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int resultUpdate = userMapper.updateByPrimaryKeySelective(updateUser);
        if(resultUpdate>0){
            return ServerResponse.createBySuccessMessage("更新用户成功!");
        }
        return ServerResponse.createByErrorMessage("用户更新失败!");
    }

    @Override
    public ServerResponse<User> getInformation(int userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user==null){
            return ServerResponse.createByErrorMessage("找不到当前用户!");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    @Override
    public ServerResponse<String> checkAdminRole(User user) {
        if(user.getRole().equals(Const.role.ROLE_ADMIN)){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
