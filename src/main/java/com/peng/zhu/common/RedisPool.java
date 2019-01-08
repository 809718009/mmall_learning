package com.peng.zhu.common;
import com.peng.zhu.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
    private static JedisPool pool;
    private static Integer maxTotal= Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));//redis客户端与服务端最大连接数
    private static Integer maxIdle=Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","10"));//连接最大空闲数
    private static Integer minIdle=Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","10"));//连接最小空闲数
    private static boolean testOnBorrow=Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","10"));//借用redis实例验证
    private static boolean testOnReturn=Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","10"));//归还redis实例验证
    private static String redisIp=PropertiesUtil.getProperty("redis.ip");
    private static Integer redisPort=Integer.parseInt(PropertiesUtil.getProperty("redis.port"));


    //初始化连接池
    private static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setBlockWhenExhausted(true);//连接池实例用完，新请求阻塞。
        config.setMaxIdle(maxIdle);
        config.setMaxTotal(maxTotal);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        pool = new JedisPool(config,redisIp,redisPort,1000*2);
    }
    static{
        initPool();
    }
    public static Jedis getJedis(){
        return pool.getResource();
    }
    public static void returnResource(Jedis jedis){
        pool.returnResource(jedis);
    }
    public static void returnBrokenResource(Jedis jedis){
        pool.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        Jedis jedis = RedisPool.getJedis();
        jedis.set("zp","pz");
        pool.destroy();//临时调用
    }
}
