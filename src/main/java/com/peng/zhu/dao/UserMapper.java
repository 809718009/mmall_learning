package com.peng.zhu.dao;

import com.peng.zhu.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUser(String username);

    int checkEmail(String email);

    User selectBylogin(@Param("username") String username,@Param("password") String password);

    String selectQuestion(String username);

    int checkAnswer(@Param("username") String username,@Param("question") String question,@Param("answer") String answer);
    int updatePasswordByUsername(@Param("username") String username,@Param("newPassword") String newPassword);
    int selectPassword(@Param("oldPassword") String oldPassword,@Param("userId") int userId);
    int checkEmailByUserId(@Param("email") String email,@Param("userId") int userId);
}