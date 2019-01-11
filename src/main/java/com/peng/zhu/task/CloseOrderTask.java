package com.peng.zhu.task;

import com.peng.zhu.service.IOrderService;
import com.peng.zhu.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CloseOrderTask {
    @Autowired
    private IOrderService iOrderService;
    @Scheduled(cron="0 */1 * * * ?")
    public void closeOrderTaskV1(){
        log.info("定时关单任务启动");
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.hour","2"));
        //iOrderService.closeOrder(hour);
        log.info("定时关单任务结束");
    }
}
