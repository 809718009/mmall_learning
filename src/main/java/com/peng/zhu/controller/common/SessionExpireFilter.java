package com.peng.zhu.controller.common;

import com.peng.zhu.common.Const;
import com.peng.zhu.pojo.User;
import com.peng.zhu.util.CookieUtil;
import com.peng.zhu.util.JsonUtil;
import com.peng.zhu.util.RedisSharedPoolUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SessionExpireFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(token)){
            String userJsonStr = RedisSharedPoolUtil.get(token);
            User user = JsonUtil.str2Object(userJsonStr,User.class);
            if(user!=null){
                RedisSharedPoolUtil.expire(token, Const.RedisCacheExtime.Redis_Session_Extime);
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
