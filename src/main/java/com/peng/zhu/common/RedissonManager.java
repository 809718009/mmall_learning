package com.peng.zhu.common;

import com.peng.zhu.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class RedissonManager {
    private Config config;
    private static Redisson redisson=null;
    private static String redis1Ip= PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port=Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));
    private static String redis2Ip=PropertiesUtil.getProperty("redis2.ip");
    private static Integer redis2Port=Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));
    @PostConstruct
    public void init(){
        try {
            config.useSingleServer().setAddress(new StringBuffer().append(redis1Ip).append(":").append(redis1Port).toString());
            Redisson.create(config);
            log.info("redisson 初始化结束.");
        } catch (Exception e) {
            log.info("redisson 初始化异常:",e);
        }
    }

    public static Redisson getRedisson() {
        return redisson;
    }
}
