package com.peng.zhu.common;
import com.peng.zhu.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

public class RedisSharedPool {
    private static ShardedJedisPool pool;
    private static Integer maxTotal= Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));//redis客户端与服务端最大连接数
    private static Integer maxIdle=Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","10"));//连接最大空闲数
    private static Integer minIdle=Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","10"));//连接最小空闲数
    private static boolean testOnBorrow=Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","10"));//借用redis实例验证
    private static boolean testOnReturn=Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","10"));//归还redis实例验证
    private static String redis1Ip=PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port=Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));
    private static String redis2Ip=PropertiesUtil.getProperty("redis2.ip");
    private static Integer redis2Port=Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));


    //初始化连接池
    private static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setBlockWhenExhausted(true);//连接池实例用完，新请求阻塞。
        config.setMaxIdle(maxIdle);
        config.setMaxTotal(maxTotal);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        JedisShardInfo info1 = new JedisShardInfo(redis1Ip,redis1Port,1000*2);
        JedisShardInfo info2 = new JedisShardInfo(redis2Ip,redis2Port,1000*2);
        List<JedisShardInfo> jedisShardInfoList = new ArrayList<JedisShardInfo>(2);
        jedisShardInfoList.add(info1);
        jedisShardInfoList.add(info2);
        //pool = new ShardedJedis(config)JedisPool(config,redisIp,redisPort,1000*2);
        pool =new ShardedJedisPool(config,jedisShardInfoList,Hashing.MURMUR_HASH,Sharded.DEFAULT_KEY_TAG_PATTERN);
    }
    static{
        initPool();
    }
    public static ShardedJedis getJedis(){
        return pool.getResource();
    }
    public static void returnResource(ShardedJedis shardedJedis){
        pool.returnResource(shardedJedis);
    }
    public static void returnBrokenResource(ShardedJedis shardedJedis){
        pool.returnBrokenResource(shardedJedis);
    }

    public static void main(String[] args) {
        ShardedJedis jedis = RedisSharedPool.getJedis();
        for (int i = 0; i <10 ; i++) {
            jedis.set("key"+i,"value"+i);
        }
        //jedis.set("zp","pz");
        //pool.destroy();//临时调用
    }
}
