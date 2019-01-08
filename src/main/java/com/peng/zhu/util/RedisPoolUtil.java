package com.peng.zhu.util;

import com.peng.zhu.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
@Slf4j
public class RedisPoolUtil {

    public static Long expire(String key,int exTime){
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result= jedis.expire(key,exTime);
        } catch (Exception e) {
            log.error("expire key:{} exTime:{} error",key,exTime,e);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }
    public static String setEx(String key,String value,int exTime){
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result= jedis.setex(key,exTime,value);
        } catch (Exception e) {
            log.error("setex key:{} exTime:{} value:{} error",key,exTime,value,e);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }
    public static String set(String key,String value){
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result= jedis.set(key,value);
        } catch (Exception e) {
           log.error("set key:{} value:{} error",key,value,e);
           return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }
    public static String get(String key){
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result= jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} error",key,e);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }
    public static Long del(String key){
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result= jedis.del(key);
        } catch (Exception e) {
            log.error("get key:{} error",key,e);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static void main(String[] args) {
        RedisPoolUtil.set("keyTest","keyTestValue");
        String value = RedisPoolUtil.get("keyTest");
        RedisPoolUtil.setEx("setExKey","setKeyValue",60*10);
        RedisPoolUtil.expire("keyTest",20*60);
        System.out.println("end");
    }
}
