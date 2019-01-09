package com.peng.zhu.util;

import com.peng.zhu.common.RedisSharedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.ShardedJedis;

@Slf4j
public class RedisSharedPoolUtil {

    public static Long expire(String key,int exTime){
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = RedisSharedPool.getJedis();
            result= jedis.expire(key,exTime);
        } catch (Exception e) {
            log.error("expire key:{} exTime:{} error",key,exTime,e);
            return result;
        }
        RedisSharedPool.returnResource(jedis);
        return result;
    }
    public static String setEx(String key,String value,int exTime){
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisSharedPool.getJedis();
            result= jedis.setex(key,exTime,value);
        } catch (Exception e) {
            log.error("setex key:{} exTime:{} value:{} error",key,exTime,value,e);
            return result;
        }
        RedisSharedPool.returnResource(jedis);
        return result;
    }
    public static String set(String key,String value){
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisSharedPool.getJedis();
            result= jedis.set(key,value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error",key,value,e);
            return result;
        }
        RedisSharedPool.returnResource(jedis);
        return result;
    }
    public static String get(String key){
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisSharedPool.getJedis();
            result= jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} error",key,e);
            return result;
        }
        RedisSharedPool.returnResource(jedis);
        return result;
    }
    public static Long del(String key){
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = RedisSharedPool.getJedis();
            result= jedis.del(key);
        } catch (Exception e) {
            log.error("get key:{} error",key,e);
            return result;
        }
        RedisSharedPool.returnResource(jedis);
        return result;
    }

    public static void main(String[] args) {
        RedisSharedPoolUtil.set("keyTest","keyTestValue");
        //String value = RedisSharedPoolUtil.get("keyTest");
        RedisSharedPoolUtil.setEx("setExKey","setKeyValue",60*10);
        RedisSharedPoolUtil.expire("keyTest",20*60);
        System.out.println("end");
    }
}
