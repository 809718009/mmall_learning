package com.peng.zhu.task;

import com.peng.zhu.common.Const;
import com.peng.zhu.common.RedissonManager;
import com.peng.zhu.service.IOrderService;
import com.peng.zhu.util.PropertiesUtil;
import com.peng.zhu.util.RedisSharedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CloseOrderTask {
    @Autowired
    private IOrderService iOrderService;
    //@Scheduled(cron="0 */1 * * * ?")
    public void closeOrderTaskV1(){
        log.info("定时关单任务启动");
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.hour","2"));
        //iOrderService.closeOrder(hour);
        log.info("定时关单任务结束");
    }
    //@Scheduled(cron="0 */1 * * * ?")
    public void closeOrderTaskV2(){
        log.info("定时关单任务启动");
        long lockTime = Long.parseLong(PropertiesUtil.getProperty("lock.time","5000"));
        Long setnxResult= RedisSharedPoolUtil.setNx(Const.RedisLock.CLOSE_ORDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()+lockTime));
        if(setnxResult!=null && setnxResult.intValue()==1){
            closeOrder(Const.RedisLock.CLOSE_ORDER_TASK_LOCK);
        }else{
            log.info("没有获取到分布式锁{}",Const.RedisLock.CLOSE_ORDER_TASK_LOCK);
        }
        log.info("定时关单任务启动");
    }
    //@Scheduled(cron="0 */1 * * * ?")
    public void closeOrderTaskV3(){
        log.info("定时关单任务启动");
        long lockTime = Long.parseLong(PropertiesUtil.getProperty("lock.time","5000"));
        Long setnxResult= RedisSharedPoolUtil.setNx(Const.RedisLock.CLOSE_ORDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()+lockTime));
        if(setnxResult!=null && setnxResult.intValue()==1){
            closeOrder(Const.RedisLock.CLOSE_ORDER_TASK_LOCK);
        }else{
            String lockValue = RedisSharedPoolUtil.get(Const.RedisLock.CLOSE_ORDER_TASK_LOCK);
            if(lockValue!=null && System.currentTimeMillis()>Long.parseLong(lockValue)){
                String getSetResult = RedisSharedPoolUtil.getSet(Const.RedisLock.CLOSE_ORDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()));
                if(getSetResult==null || (getSetResult!=null &&  StringUtils.equals(getSetResult,lockValue))){
                    closeOrder(Const.RedisLock.CLOSE_ORDER_TASK_LOCK);
                }else{
                    log.info("没有获取到分布式锁{}",Const.RedisLock.CLOSE_ORDER_TASK_LOCK);
                }
            }else{
                log.info("没有获取到分布式锁{}",Const.RedisLock.CLOSE_ORDER_TASK_LOCK);
            }
        }
        log.info("定时关单任务启动");
    }
    @Scheduled(cron="0 */1 * * * ?")
    public void closeOrderTaskV4(){
        RLock rlock = RedissonManager.getRedisson().getLock(Const.RedisLock.CLOSE_ORDER_TASK_LOCK);
        boolean isRlock=false;
        try {
            if(isRlock=rlock.tryLock(0,50, TimeUnit.MILLISECONDS)){
                int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.hour","2"));
                //iOrderService.closeOrder(hour);
                log.info("Redisson获取到分布式锁:{} TreadName:{}",Const.RedisLock.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            log.info("Redisson获取到分布式锁异常:{} TreadName:{}",Const.RedisLock.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
        } finally {
            if(!isRlock){
                return;
            }
            rlock.unlock();
            log.info("Redisson释放分布式锁:{} TreadName:{}",Const.RedisLock.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());

        }
    }

    private void closeOrder(String lockName){
        RedisSharedPoolUtil.expire(lockName,50);//防止死锁，释放。
        log.info("获取:{} Thread:{}",Const.RedisLock.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.hour","2"));
        //iOrderService.closeOrder(hour);
        RedisSharedPoolUtil.del(lockName);
        log.info("释放:{} Thread:{}",Const.RedisLock.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
        log.info("====================================================================");
    }

}
