package com.peng.zhu.service;

import com.peng.zhu.common.ServerResponse;
import com.peng.zhu.pojo.User;

public interface IUserService {
    ServerResponse<User> login(String username, String password);
    ServerResponse<String> register(User user);
    ServerResponse<String> checkValid(String str,String type);
    ServerResponse<String> getQuestion(String username);
    ServerResponse<String> checkAnswer(String username,String question,String answer);
    ServerResponse<String> forgetResetPassword(String username,String newPassword,String forgetToken);
    ServerResponse<String> restPassword(User user,String newPassword,String oldPassword);
    ServerResponse<User> updateInfomation(User user);
    ServerResponse<User> getInformation(int userId);
    ServerResponse<String> checkAdminRole(User user);
}
