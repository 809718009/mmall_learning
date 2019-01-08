package com.peng.zhu.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Slf4j
public class CookieUtil {
    private final static String COOKIE_NAME="mmall_login_token";
    private final static String COOKIE_DOMAIN="zpwindows.com";


    public static String readLoginToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies!=null) {
            for (Cookie cookie : cookies) {
                log.info("readLoginToken COOKIE_NAME:{} TOKEN:{}", cookie.getName(), cookie.getValue());
                if (StringUtils.equals(cookie.getName(), COOKIE_NAME)) {
                    log.info("return cookieName:{},cookieValue:{}",cookie.getName(),cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return  null;
    }
    public static void writeLoginToken(HttpServletResponse response, String token){
        Cookie cookie = new Cookie(COOKIE_NAME,token);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60*30);
        log.info("wirteLoginToken COOKIE_NAME:{} TOKEN:{}",COOKIE_NAME,token);
        response.addCookie(cookie);
    }
    public static void delLoginToken(HttpServletRequest request,HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for(Cookie cookie:cookies){
                log.info("delLoginToken COOKIE_NAME:{} TOKEN:{}", cookie.getName(), cookie.getValue());
                if (StringUtils.equals(cookie.getName(), COOKIE_NAME)) {
                    log.info("del cookieName:{},cookieValue:{}",cookie.getName(),cookie.getValue());
                    cookie.setPath("/");
                    cookie.setDomain(COOKIE_DOMAIN);
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    return;
                }
            }
        }
    }
}
